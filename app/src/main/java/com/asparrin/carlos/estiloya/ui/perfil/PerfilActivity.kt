package com.asparrin.carlos.estiloya.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.databinding.ActivityPerfilBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.asparrin.carlos.estiloya.utils.SessionManager

class PerfilActivity : BaseActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var sessionManager: SessionManager

    override fun getLayoutResourceId(): Int = R.layout.activity_perfil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = com.asparrin.carlos.estiloya.databinding.ActivityPerfilBinding.bind(child)
        sessionManager = com.asparrin.carlos.estiloya.utils.SessionManager(this)

        setupListeners()
        cargarDatosUsuario()
    }


    private fun setupListeners() {
        binding.btnEditarPerfil.setOnClickListener {
            // TODO: Implementar edición de perfil
            Toast.makeText(this, "Funcionalidad de edición en desarrollo", Toast.LENGTH_SHORT).show()
        }

        binding.btnCambiarContrasena.setOnClickListener {
            // TODO: Implementar cambio de contraseña
            Toast.makeText(this, "Funcionalidad de cambio de contraseña en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatosUsuario() {
        // Obtener datos del usuario desde SessionManager
        val nombre = sessionManager.obtenerNombre()
        
        if (nombre != null && sessionManager.estaLogueado()) {
            // Mostrar datos del usuario (simplificado por ahora)
            binding.tvNombreCompleto.text = nombre
            binding.tvEmail.text = "usuario@email.com" // Placeholder
            
            binding.etNombre.setText(nombre)
            binding.etApellidos.setText("Apellidos") // Placeholder
            binding.etEmail.setText("usuario@email.com") // Placeholder
            binding.etTelefono.setText("") // Placeholder
        } else {
            // Si no hay usuario logueado, mostrar mensaje
            Toast.makeText(this, "No hay datos de usuario disponibles", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 