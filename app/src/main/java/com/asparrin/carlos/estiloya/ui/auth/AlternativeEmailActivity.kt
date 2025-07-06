package com.asparrin.carlos.estiloya.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.databinding.ActivityAlternativeEmailBinding
import com.asparrin.carlos.estiloya.viewModel.AuthViewModel

class AlternativeEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlternativeEmailBinding
    private lateinit var authViewModel: AuthViewModel
    
    companion object {
        private const val TAG = "AlternativeEmailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlternativeEmailBinding.inflate(layoutInflater)
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
            binding.saveButton.isEnabled = !isLoading
        }

        // Observar mensajes de error
        authViewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: $errorMessage")
            }
        }

        // Observar resultado del registro de email alternativo
        authViewModel.registerEmailResult.observe(this) { response ->
            if (response.success) {
                Toast.makeText(this, "Correo alternativo registrado exitosamente", Toast.LENGTH_LONG).show()
                // Navegar a la actividad de 2FA
                val intent = Intent(this, TwoFactorActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val message = response.message ?: "Error desconocido al registrar correo alternativo"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            val alternativeEmail = binding.alternativeEmailEditText.text.toString().trim()
            
            if (validateEmail(alternativeEmail)) {
                Log.d(TAG, "Registrando correo alternativo: $alternativeEmail")
                // Obtener el correo principal del SessionManager o del intent
                val mainEmail = intent.getStringExtra("main_email") ?: ""
                authViewModel.registerAlternativeEmail(mainEmail, alternativeEmail)
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
    
    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            binding.alternativeEmailEditText.error = "El correo alternativo es requerido"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.alternativeEmailEditText.error = "Correo alternativo inválido"
            return false
        }

        return true
    }
} 