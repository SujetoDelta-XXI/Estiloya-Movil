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
        mostrarDatosUsuario()
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, com.asparrin.carlos.estiloya.ui.home.HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun mostrarDatosUsuario() {
        val nombre = sessionManager.obtenerNombre() ?: "-"
        val email = sessionManager.obtenerCorreo() ?: "-"
        binding.tvNombre.text = nombre
        binding.tvEmail.text = email
    }
} 