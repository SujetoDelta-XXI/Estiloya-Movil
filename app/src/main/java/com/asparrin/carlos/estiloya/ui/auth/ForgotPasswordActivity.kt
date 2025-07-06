package com.asparrin.carlos.estiloya.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.databinding.ActivityForgotPasswordBinding
import com.asparrin.carlos.estiloya.viewModel.AuthViewModel

class ForgotPasswordActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var authViewModel: AuthViewModel
    
    companion object {
        private const val TAG = "ForgotPasswordActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inicializar ViewModel
        authViewModel = AuthViewModel(this)
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        // Observar estado de carga
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.sendButton.isEnabled = !isLoading
        }
        
        // Observar mensajes de error
        authViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: $errorMessage")
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.sendButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            
            if (validateEmail(email)) {
                Log.d(TAG, "Solicitando recuperación de contraseña para email: $email")
                authViewModel.forgotPassword(email)
                Toast.makeText(this, "Email de recuperación enviado", Toast.LENGTH_LONG).show()
                finish()
            }
        }
        
        binding.backButton.setOnClickListener {
            finish()
        }
    }
    
    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            binding.emailEditText.error = "El email es requerido"
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Email inválido"
            return false
        }
        
        return true
    }
} 