package com.asparrin.carlos.estiloya.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.databinding.ActivityTwoFactorBinding
import com.asparrin.carlos.estiloya.ui.home.HomeActivity
import com.asparrin.carlos.estiloya.utils.SessionManager
import com.asparrin.carlos.estiloya.viewModel.AuthState
import com.asparrin.carlos.estiloya.viewModel.AuthViewModel

class TwoFactorActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTwoFactorBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager
    
    companion object {
        private const val TAG = "TwoFactorActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTwoFactorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inicializar ViewModel y SessionManager
        authViewModel = AuthViewModel(this)
        sessionManager = SessionManager(this)
        
        setupObservers()
        setupClickListeners()
        
        // Mostrar el correo alternativo donde se envió el código
        updateEmailText()
        
        // Enviar código inicial automáticamente
        sendInitialCode()
        
        // Establecer foco en el campo de código
        binding.codeEditText.requestFocus()
    }
    
    private fun setupObservers() {
        // Observar estado de autenticación
        authViewModel.authState.observe(this) { state ->
            when (state) {
                AuthState.AUTHENTICATED -> {
                    Log.d(TAG, "2FA completado, navegando a Home")
                    // Mostrar mensaje de éxito antes de navegar
                    Toast.makeText(this, "¡Validación exitosa! Bienvenido a EstiloYa", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
                AuthState.REQUIRES_2FA -> {
                    Log.d(TAG, "Aún requiere 2FA")
                }
                AuthState.NOT_AUTHENTICATED -> {
                    Log.d(TAG, "Usuario no autenticado, navegando a Login")
                    navigateToLogin()
                }
                AuthState.LOADING -> {
                    Log.d(TAG, "Cargando...")
                }
            }
        }
        
        // Observar estado de carga
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.verifyButton.isEnabled = !isLoading
            binding.resendButton.isEnabled = !isLoading
            binding.codeEditText.isEnabled = !isLoading
            
            if (isLoading) {
                binding.verifyButton.text = "Validando..."
                binding.resendButton.text = "Enviando..."
            } else {
                binding.verifyButton.text = "Validar"
                binding.resendButton.text = "Reenviar código"
            }
        }
        
        // Observar mensajes de error
        authViewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: $errorMessage")
            }
        }
        
        // Observar resultado de verificación 2FA
        authViewModel.twoFactorResult.observe(this) { response ->
            if (response.success) {
                Toast.makeText(this, "Verificación 2FA exitosa", Toast.LENGTH_SHORT).show()
                // Limpiar el campo de código después de verificación exitosa
                binding.codeEditText.text?.clear()
            } else {
                val message = response.message ?: "Error desconocido en la verificación 2FA"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                // Limpiar el campo de código en caso de error para que el usuario pueda intentar de nuevo
                binding.codeEditText.text?.clear()
                binding.codeEditText.requestFocus()
            }
        }
        
        // Observar resultado de envío de código
        authViewModel.sendCodeResult.observe(this) { response ->
            if (response.success) {
                Toast.makeText(this, "Código reenviado exitosamente", Toast.LENGTH_SHORT).show()
                // Actualizar el texto para mostrar que se reenvió
                updateEmailText()
            } else {
                val message = response.message ?: "Error desconocido al reenviar código"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                
                // Si hay error al enviar código, intentar una solución alternativa
                Log.e(TAG, "Error al enviar código 2FA: $message")
                
                // Mostrar mensaje informativo sobre el problema
                Toast.makeText(this, "Problema con el envío de código. Verificando configuración...", Toast.LENGTH_LONG).show()
                
                // Deshabilitar el botón de reenvío temporalmente
                binding.resendButton.isEnabled = false
                binding.root.postDelayed({
                    binding.resendButton.isEnabled = true
                }, 30000) // Habilitar después de 30 segundos
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.verifyButton.setOnClickListener {
            val code = binding.codeEditText.text.toString().trim()
            val email = getUserEmail() // Para verificación usamos el email principal
            
            if (validateCode(code) && email != null) {
                Log.d(TAG, "Verificando código 2FA para email: $email")
                authViewModel.verify2FA(email, code)
            }
        }
        
        binding.resendButton.setOnClickListener {
            if (hasAlternativeEmail()) {
                Log.d(TAG, "Reenviando código 2FA")
                authViewModel.send2FACode() // Sin parámetros, el backend maneja el correo
            } else {
                Log.d(TAG, "Usuario no tiene correo alternativo configurado, redirigiendo a configuración")
                Toast.makeText(this, "Configurando correo alternativo para 2FA...", Toast.LENGTH_SHORT).show()
                
                // Redirigir a la actividad de configuración de correo alternativo
                val mainEmail = getUserEmail()
                val intent = Intent(this, AlternativeEmailActivity::class.java)
                intent.putExtra("main_email", mainEmail)
                startActivity(intent)
                finish()
            }
        }
        
        binding.backButton.setOnClickListener {
            // Mostrar confirmación antes de cancelar 2FA
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cancelar verificación")
                .setMessage("¿Estás seguro de que quieres cancelar la verificación en dos pasos?")
                .setPositiveButton("Sí, cancelar") { _, _ ->
                    // Limpiar token temporal y volver a login
                    sessionManager.clearTemporaryToken()
                    navigateToLogin()
                }
                .setNegativeButton("No, continuar", null)
                .show()
        }
    }
    
    private fun validateCode(code: String): Boolean {
        if (code.isEmpty()) {
            binding.codeEditText.error = "El código es requerido"
            return false
        }
        
        if (code.length != 6) {
            binding.codeEditText.error = "El código debe tener 6 dígitos"
            return false
        }
        
        // Verificar que solo contenga dígitos
        if (!code.matches(Regex("^[0-9]{6}$"))) {
            binding.codeEditText.error = "El código debe contener solo números"
            return false
        }
        
        return true
    }
    
    private fun getUserEmail(): String? {
        // Primero intentar obtener del Intent (más confiable)
        val emailFromIntent = intent.getStringExtra("main_email")
        if (!emailFromIntent.isNullOrEmpty()) {
            Log.d(TAG, "getUserEmail - email from intent: $emailFromIntent")
            return emailFromIntent
        }
        
        // Intentar obtener email del usuario guardado en sesión
        val user = sessionManager.getUser()
        val mainEmail = user?.correo
        
        // Si no hay usuario guardado, intentar obtener del resultado del login
        if (mainEmail == null) {
            val loginResult = authViewModel.loginResult.value
            // Priorizar el campo correo directo de la respuesta
            val emailFromLogin = loginResult?.correo ?: loginResult?.user?.correo
            Log.d(TAG, "getUserEmail - email from login: $emailFromLogin")
            return emailFromLogin
        }
        
        Log.d(TAG, "getUserEmail - email from session: $mainEmail")
        return mainEmail
    }

    private fun hasAlternativeEmail(): Boolean {
        // Obtener datos del Intent (pasados desde LoginActivity)
        val correoAlternativo = intent.getStringExtra("correo_alternativo")
        val tiene2FAConfigurado = intent.getBooleanExtra("tiene_2fa_configurado", false)
        val hasEmailMethod = intent.getBooleanExtra("has_email_method", false)
        
        // Verificar si el usuario tiene correo alternativo configurado
        val hasEmail = !correoAlternativo.isNullOrEmpty() || tiene2FAConfigurado || hasEmailMethod
        
        Log.d(TAG, "hasAlternativeEmail - correoAlternativo: $correoAlternativo, tiene2FAConfigurado: $tiene2FAConfigurado, hasEmailMethod: $hasEmailMethod, hasEmail: $hasEmail")
        
        return hasEmail
    }
    
    private fun getUserAlternativeEmail(): String? {
        // Obtener email alternativo del Intent (pasado desde LoginActivity)
        val correoAlternativo = intent.getStringExtra("correo_alternativo")
        
        if (!correoAlternativo.isNullOrEmpty()) {
            Log.d(TAG, "getUserAlternativeEmail - correoAlternativo del intent: $correoAlternativo")
            return correoAlternativo
        }
        
        // Fallback: intentar obtener email alternativo del usuario guardado en sesión
        val user = sessionManager.getUser()
        val alternativeEmail = user?.correoAuth
        
        if (!alternativeEmail.isNullOrEmpty()) {
            Log.d(TAG, "getUserAlternativeEmail - correoAuth del usuario en sesión: $alternativeEmail")
            return alternativeEmail
        }
        
        // Fallback: intentar obtener del almacenamiento local
        val sharedPreferences = getSharedPreferences("EstiloyaSession", MODE_PRIVATE)
        val localEmail = sharedPreferences.getString("alternative_email", null)
        
        if (!localEmail.isNullOrEmpty()) {
            Log.d(TAG, "getUserAlternativeEmail - email local: $localEmail")
            return localEmail
        }
        
        Log.d(TAG, "getUserAlternativeEmail - no se encontró email alternativo")
        return null
    }
    
    private fun sendInitialCode() {
        val mainEmail = getUserEmail()
        
        Log.d(TAG, "sendInitialCode - email principal: $mainEmail")
        
        // Debug: mostrar información del usuario en sesión
        val user = sessionManager.getUser()
        Log.d(TAG, "sendInitialCode - usuario en sesión: ${user != null}")
        if (user != null) {
            Log.d(TAG, "sendInitialCode - correo del usuario: ${user.correo}")
            Log.d(TAG, "sendInitialCode - correo alternativo del usuario: ${user.correoAuth}")
        }
        
        // Debug: mostrar información del resultado del login
        val loginResult = authViewModel.loginResult.value
        Log.d(TAG, "sendInitialCode - resultado del login: ${loginResult != null}")
        if (loginResult != null) {
            Log.d(TAG, "sendInitialCode - correo del login: ${loginResult.correo}")
            Log.d(TAG, "sendInitialCode - usuario del login: ${loginResult.user != null}")
            if (loginResult.user != null) {
                Log.d(TAG, "sendInitialCode - correo alternativo del login: ${loginResult.user.correoAuth}")
            }
        }
        
        // Verificar si el usuario tiene correo alternativo configurado
        if (!hasAlternativeEmail()) {
            Log.d(TAG, "Usuario no tiene correo alternativo configurado, redirigiendo a configuración")
            Toast.makeText(this, "Configurando correo alternativo para 2FA...", Toast.LENGTH_SHORT).show()
            
            // Redirigir a la actividad de configuración de correo alternativo
            val intent = Intent(this, AlternativeEmailActivity::class.java)
            intent.putExtra("main_email", mainEmail)
            startActivity(intent)
            finish()
            return
        }
        
        // Si tiene correo alternativo, enviar código 2FA
        Log.d(TAG, "Usuario tiene correo alternativo configurado, enviando código 2FA")
        Toast.makeText(this, "Enviando código de verificación...", Toast.LENGTH_SHORT).show()
        authViewModel.send2FACode() // Sin parámetros, el backend maneja el correo
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onBackPressed() {
        // Mostrar confirmación antes de cancelar 2FA
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cancelar verificación")
            .setMessage("¿Estás seguro de que quieres cancelar la verificación en dos pasos?")
            .setPositiveButton("Sí, cancelar") { _, _ ->
                // Limpiar token temporal y volver a login
                sessionManager.clearTemporaryToken()
                navigateToLogin()
            }
            .setNegativeButton("No, continuar", null)
            .show()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun updateEmailText() {
        val mainEmail = getUserEmail()
        val alternativeEmail = getUserAlternativeEmail()
        val hasSms = intent.getBooleanExtra("has_sms_method", false)
        val hasEmail = intent.getBooleanExtra("has_email_method", false)
        
        // Mostrar el correo alternativo real si está disponible
        if (!alternativeEmail.isNullOrEmpty()) {
            binding.emailText.text = "Código enviado a: $alternativeEmail"
        } else {
            binding.emailText.text = "Código enviado a tu correo alternativo"
        }
        
        Log.d(TAG, "updateEmailText - hasAlternativeEmail: ${hasAlternativeEmail()}, hasSms: $hasSms, hasEmail: $hasEmail, alternativeEmail: $alternativeEmail")
    }
} 