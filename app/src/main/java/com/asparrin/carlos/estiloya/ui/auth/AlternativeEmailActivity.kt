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
            Log.d(TAG, "📧 RegisterEmailResult recibido: success=${response.isSuccess}, message=${response.message}")
            
            if (response.isSuccess) {
                Toast.makeText(this, "Correo alternativo registrado exitosamente", Toast.LENGTH_LONG).show()
                
                // Navegar directamente a la actividad de 2FA
                Log.d(TAG, "🚀 Iniciando navegación a TwoFactorActivity")
                val intent = Intent(this, TwoFactorActivity::class.java)
                
                // Obtener el main_email del intent original que inició esta actividad
                val mainEmail = this.intent.getStringExtra("main_email")
                Log.d(TAG, "📧 Main email para pasar a TwoFactorActivity: $mainEmail")
                if (!mainEmail.isNullOrEmpty()) {
                    intent.putExtra("main_email", mainEmail)
                    Log.d(TAG, "✅ Main email agregado al intent")
                } else {
                    Log.w(TAG, "⚠️ Main email no encontrado en el intent")
                }
                
                // Pasar el correo alternativo que se acaba de registrar
                val alternativeEmail = binding.alternativeEmailEditText.text.toString().trim()
                if (!alternativeEmail.isEmpty()) {
                    intent.putExtra("correo_alternativo", alternativeEmail)
                    intent.putExtra("tiene_2fa_configurado", true)
                    intent.putExtra("has_email_method", true)
                    intent.putExtra("has_sms_method", false)
                    Log.d(TAG, "✅ Correo alternativo agregado al intent: $alternativeEmail")
                }
                
                Log.d(TAG, "🚀 Iniciando TwoFactorActivity...")
                startActivity(intent)
                Log.d(TAG, "✅ TwoFactorActivity iniciada, finalizando AlternativeEmailActivity")
                finish()
            } else {
                val message = response.message ?: "Error desconocido al registrar correo alternativo"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                
                // Si falla el registro, el usuario debe intentar de nuevo
                Log.e(TAG, "❌ Error en registro de email alternativo: $message")
            }
        }
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            val alternativeEmail = binding.alternativeEmailEditText.text.toString().trim()
            
            if (validateEmail(alternativeEmail)) {
                Log.d(TAG, "Registrando correo alternativo: $alternativeEmail")
                // Obtener el correo principal del intent original que inició esta actividad
                val mainEmail = this.intent.getStringExtra("main_email") ?: ""
                Log.d(TAG, "Main email obtenido del intent: $mainEmail")
                
                // Guardar el correo alternativo localmente para uso temporal
                saveAlternativeEmailLocally(alternativeEmail)
                
                // Intentar registrar en el backend
                authViewModel.registerAlternativeEmail(mainEmail, alternativeEmail)
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
    
    private fun saveAlternativeEmailLocally(email: String) {
                        val sharedPreferences = getSharedPreferences("EstiloyaSession", MODE_PRIVATE)
        sharedPreferences.edit().putString("alternative_email", email).apply()
        Log.d(TAG, "Alternative email saved locally: $email")
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