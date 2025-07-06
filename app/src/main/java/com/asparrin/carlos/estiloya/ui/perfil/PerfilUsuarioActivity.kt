package com.asparrin.carlos.estiloya.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.databinding.ActivityPerfilUsuarioBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.asparrin.carlos.estiloya.utils.SessionManager

class PerfilUsuarioActivity : BaseActivity() {
    private lateinit var binding: ActivityPerfilUsuarioBinding
    private lateinit var sessionManager: SessionManager

    override fun getLayoutResourceId(): Int = R.layout.activity_perfil_usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityPerfilUsuarioBinding.bind(child)
        sessionManager = SessionManager(this)
        cargarDatosUsuario()
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, com.asparrin.carlos.estiloya.ui.home.HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun cargarDatosUsuario() {
        // Obtener datos del usuario desde SessionManager
        val user = sessionManager.getUser()
        
        if (user != null && sessionManager.estaLogueado()) {
            // Mostrar datos del usuario
            binding.tvNombre.text = "${user.nombre} ${user.apellidos}"
            binding.tvEmail.text = user.correo
        } else {
            // Si no hay usuario logueado, mostrar mensaje
            Toast.makeText(this, "No hay datos de usuario disponibles", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 