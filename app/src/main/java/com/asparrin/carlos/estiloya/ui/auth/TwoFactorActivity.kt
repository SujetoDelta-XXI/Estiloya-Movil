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
        val alternativeEmail = getUserAlternativeEmail()
        if (alternativeEmail != null) {
            binding.emailText.text = "Código enviado a: $alternativeEmail"
        } else {
            binding.emailText.text = "Código enviado a tu correo alternativo"
        }
        
        // Enviar código inicial automáticamente
        sendInitialCode()
    }
    
    private fun setupObservers() {
        // Observar estado de autenticación
        authViewModel.authState.observe(this) { state ->
            when (state) {
                AuthState.AUTHENTICATED -> {
                    Log.d(TAG, "2FA completado, navegando a Home")
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
            } else {
                val message = response.message ?: "Error desconocido en la verificación 2FA"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
        
        // Observar resultado de envío de código
        authViewModel.sendCodeResult.observe(this) { response ->
            if (response.success) {
                Toast.makeText(this, "Código reenviado exitosamente", Toast.LENGTH_SHORT).show()
                // Actualizar el texto para mostrar que se reenvió
                val alternativeEmail = getUserAlternativeEmail()
                if (alternativeEmail != null) {
                    binding.emailText.text = "Código reenviado a: $alternativeEmail"
                }
            } else {
                val message = response.message ?: "Error desconocido al reenviar código"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.verifyButton.setOnClickListener {
            val code = binding.codeEditText.text.toString().trim()
            val email = getUserEmail()
            
            if (validateCode(code) && email != null) {
                Log.d(TAG, "Verificando código 2FA para email: $email")
                authViewModel.verify2FA(email, code)
            }
        }
        
        binding.resendButton.setOnClickListener {
            val email = getUserEmail()
            if (email != null) {
                Log.d(TAG, "Reenviando código 2FA para email: $email")
                authViewModel.send2FACode(email)
            }
        }
        
        binding.backButton.setOnClickListener {
            // Limpiar token temporal y volver a login
            sessionManager.clearTemporaryToken()
            navigateToLogin()
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
        
        return true
    }
    
    private fun getUserEmail(): String? {
        // Intentar obtener email del usuario guardado en sesión
        val user = sessionManager.getUser()
        return user?.correo
    }
    
    private fun getUserAlternativeEmail(): String? {
        // Intentar obtener email alternativo del usuario guardado en sesión
        val user = sessionManager.getUser()
        return user?.correoAuth
    }
    
    private fun sendInitialCode() {
        val email = getUserEmail()
        if (email != null) {
            Log.d(TAG, "Enviando código inicial 2FA para email: $email")
            authViewModel.send2FACode(email)
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
} 