package com.asparrin.carlos.estiloya.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.databinding.ActivityRegisterBinding
import com.asparrin.carlos.estiloya.viewModel.AuthState
import com.asparrin.carlos.estiloya.viewModel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    
    companion object {
        private const val TAG = "RegisterActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inicializar ViewModel
        authViewModel = AuthViewModel(this)
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        // Observar estado de autenticación
        authViewModel.authState.observe(this) { state ->
            when (state) {
                AuthState.AUTHENTICATED -> {
                    Log.d(TAG, "Usuario registrado exitosamente, navegando a Login")
                    navigateToLogin()
                }
                AuthState.REQUIRES_2FA -> {
                    Log.d(TAG, "Registro requiere 2FA, navegando a Login")
                    navigateToLogin()
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
            binding.registerButton.isEnabled = !isLoading
        }
        
        // Observar mensajes de error
        authViewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: $errorMessage")
            }
        }
        
        // Observar resultado de registro
        authViewModel.registerResult.observe(this) { response ->
            if (response.success) {
                Toast.makeText(this, "Registro exitoso. Por favor inicia sesión", Toast.LENGTH_SHORT).show()
                // Navegar a Login después de un breve delay para que se vea el mensaje
                binding.root.postDelayed({
                    navigateToLogin()
                }, 1500)
            } else {
                val message = response.message ?: "Error desconocido en el registro"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val nombre = binding.nombreEditText.text.toString().trim()
            val apellidos = binding.apellidosEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val contraseña = binding.passwordEditText.text.toString().trim()
            val telefono = binding.telefonoEditText.text.toString().trim()
            
            if (validateInputs(nombre, apellidos, email, contraseña, telefono)) {
                Log.d(TAG, "Intentando registro con email: $email")
                authViewModel.register(nombre, apellidos, email, contraseña, telefono)
            }
        }
        
        binding.loginLinkButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    
    private fun validateInputs(
        nombre: String,
        apellidos: String,
        email: String,
        contraseña: String,
        telefono: String
    ): Boolean {
        if (nombre.isEmpty()) {
            binding.nombreEditText.error = "El nombre es requerido"
            return false
        }
        
        if (apellidos.isEmpty()) {
            binding.apellidosEditText.error = "Los apellidos son requeridos"
            return false
        }
        
        if (email.isEmpty()) {
            binding.emailEditText.error = "El email es requerido"
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Email inválido"
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
        
        if (telefono.isEmpty()) {
            binding.telefonoEditText.error = "El teléfono es requerido"
            return false
        }
        
        return true
    }
    
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
