package com.asparrin.carlos.estiloya.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.databinding.ActivityLoginBinding
import com.asparrin.carlos.estiloya.ui.home.HomeActivity
import com.asparrin.carlos.estiloya.viewModel.AuthState
import com.asparrin.carlos.estiloya.viewModel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel
    
    companion object {
        private const val TAG = "LoginActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inicializar ViewModel
        authViewModel = AuthViewModel(this)
        
        setupObservers()
        setupClickListeners()
        
        // Verificar estado de autenticación
        authViewModel.checkAuthState()
    }
    
    private fun setupObservers() {
        // Observar estado de autenticación
        authViewModel.authState.observe(this) { state ->
            when (state) {
                AuthState.AUTHENTICATED -> {
                    Log.d(TAG, "Login exitoso, navegando a Home")
                    navigateToHome()
                }
                AuthState.REQUIRES_2FA -> {
                    Log.d(TAG, "Requiere 2FA, verificando si tiene correo alternativo")
                    checkAlternativeEmail()
                }
                AuthState.NOT_AUTHENTICATED -> {
                    Log.d(TAG, "Usuario no autenticado")
                }
                AuthState.LOADING -> {
                    Log.d(TAG, "Cargando...")
                }
            }
        }
        
        // Observar estado de carga
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loginButton.isEnabled = !isLoading
        }
        
        // Observar mensajes de error
        authViewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: $errorMessage")
            }
        }
        
        // Observar resultado del login
        authViewModel.loginResult.observe(this) { response ->
            Log.d(TAG, "Login response recibida: success=${response.success}, requiere2FA=${response.requiere2FA}")
            Log.d(TAG, "Login response - metodos: ${response.metodos}")
            Log.d(TAG, "Login response - correo: ${response.correo}")
            Log.d(TAG, "Login response - user: ${response.user}")
            
            if (response.success) {
                if (response.requiere2FA) {
                    Log.d(TAG, "Login exitoso pero requiere 2FA")
                    // El estado se manejará en authState
                } else {
                    Log.d(TAG, "Login exitoso sin 2FA")
                    // El estado se manejará en authState
                }
            } else {
                val message = response.message ?: "Error desconocido en el login"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val correo = binding.emailEditText.text.toString().trim()
            val contraseña = binding.passwordEditText.text.toString().trim()
            
            if (validateInputs(correo, contraseña)) {
                Log.d(TAG, "Intentando login con correo: $correo")
                authViewModel.login(correo, contraseña)
            }
        }
        
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        binding.forgotPasswordText.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
    
    private fun validateInputs(correo: String, contraseña: String): Boolean {
        if (correo.isEmpty()) {
            binding.emailEditText.error = "El correo es requerido"
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.emailEditText.error = "Correo inválido"
            return false
        }
        
        if (contraseña.isEmpty()) {
            binding.passwordEditText.error = "La contraseña es requerida"
            return false
        }
        
        if (contraseña.length < 6) {
            binding.passwordEditText.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        
        return true
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun checkAlternativeEmail() {
        val loginResponse = authViewModel.loginResult.value
        val user = loginResponse?.user
        val correo = loginResponse?.correo
        val metodos = loginResponse?.metodos
        val correoAlternativo = loginResponse?.correoAlternativo
        val tiene2FAConfigurado = loginResponse?.tiene2FAConfigurado
        
        Log.d(TAG, "=== checkAlternativeEmail DEBUG ===")
        Log.d(TAG, "loginResponse completo: $loginResponse")
        Log.d(TAG, "user: ${user != null}, correo: $correo")
        Log.d(TAG, "metodos: $metodos")
        Log.d(TAG, "correoAlternativo: $correoAlternativo")
        Log.d(TAG, "tiene2FAConfigurado: $tiene2FAConfigurado")
        Log.d(TAG, "user?.correoAuth: ${user?.correoAuth}")
        Log.d(TAG, "user?.correoAuth es null: ${user?.correoAuth == null}")
        Log.d(TAG, "user?.correoAuth está vacío: ${user?.correoAuth?.isEmpty()}")
        Log.d(TAG, "metodos?.get(\"correo\"): ${metodos?.get("correo")}")
        Log.d(TAG, "metodos?.get(\"sms\"): ${metodos?.get("sms")}")
        
        // Verificar si el usuario tiene correo alternativo configurado
        // Usar los nuevos campos del backend en lugar de user?.correoAuth
        val hasAlternativeEmail = !correoAlternativo.isNullOrEmpty() || (tiene2FAConfigurado == true)
        Log.d(TAG, "hasAlternativeEmail calculado: $hasAlternativeEmail")
        Log.d(TAG, "=== FIN DEBUG ===")
        
        if (hasAlternativeEmail) {
            Log.d(TAG, "✅ Usuario tiene correo alternativo configurado, navegando a TwoFactorActivity")
            val intent = Intent(this, TwoFactorActivity::class.java)
            if (correo != null) {
                intent.putExtra("main_email", correo)
            }
            if (user != null) {
                intent.putExtra("main_email", user.correo)
            }
            // Pasar los datos del backend para que TwoFactorActivity los use
            intent.putExtra("correo_alternativo", correoAlternativo)
            intent.putExtra("tiene_2fa_configurado", tiene2FAConfigurado ?: false)
            intent.putExtra("has_email_method", metodos?.get("correo") ?: false)
            intent.putExtra("has_sms_method", metodos?.get("sms") ?: false)
            startActivity(intent)
            finish() // Cerrar LoginActivity para evitar que vuelva atrás
        } else {
            Log.d(TAG, "❌ Usuario NO tiene correo alternativo configurado, navegando a AlternativeEmailActivity")
            val intent = Intent(this, AlternativeEmailActivity::class.java)
            if (correo != null) {
                intent.putExtra("main_email", correo)
            }
            if (user != null) {
                intent.putExtra("main_email", user.correo)
            }
            startActivity(intent)
            finish() // Cerrar LoginActivity para evitar que vuelva atrás
        }
    }
}
