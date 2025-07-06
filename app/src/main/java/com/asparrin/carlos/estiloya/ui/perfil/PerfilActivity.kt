package com.asparrin.carlos.estiloya.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.databinding.ActivityPerfilBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.asparrin.carlos.estiloya.utils.SessionManager
import com.asparrin.carlos.estiloya.viewModel.PerfilViewModel

class PerfilActivity : BaseActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: PerfilViewModel

    override fun getLayoutResourceId(): Int = R.layout.activity_perfil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityPerfilBinding.bind(child)
        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[PerfilViewModel::class.java]

        setupObservers()
        setupListeners()
        cargarDatosUsuario()
    }


    private fun setupObservers() {
        // Observar datos del usuario
        viewModel.usuario.observe(this) { usuario ->
            if (usuario != null) {
                mostrarDatosUsuario(usuario)
            }
        }

        // Observar estado de edición
        viewModel.isEditing.observe(this) { isEditing ->
            actualizarModoEdicion(isEditing)
        }

        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observar errores
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.limpiarMensajes()
            }
        }

        // Observar mensajes de éxito
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.limpiarMensajes()
            }
        }
    }

    private fun setupListeners() {
        binding.btnEditarPerfil.setOnClickListener {
            if (viewModel.isEditing.value == true) {
                // Guardar cambios
                guardarCambios()
            } else {
                // Entrar en modo edición
                viewModel.toggleEdicion()
            }
        }

        binding.btnCambiarContrasena.setOnClickListener {
            // TODO: Implementar cambio de contraseña
            Toast.makeText(this, "Funcionalidad de cambio de contraseña en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatosUsuario() {
        // Verificar si hay usuario logueado
        if (sessionManager.estaLogueado()) {
            // Cargar datos locales primero
            viewModel.cargarDatosLocales(this)
            // Luego cargar datos actualizados desde la API
            viewModel.cargarUsuario(this)
        } else {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun mostrarDatosUsuario(usuario: com.asparrin.carlos.estiloya.data.model.Usuario) {
        binding.tvNombreCompleto.text = "${usuario.nombre} ${usuario.apellidos}"
        binding.tvEmail.text = usuario.correo
        
        binding.etNombre.setText(usuario.nombre)
        binding.etApellidos.setText(usuario.apellidos)
        binding.etEmail.setText(usuario.correo)
        binding.etTelefono.setText(usuario.telefono ?: "")
        binding.etContrasena.setText("••••••••") // Contraseña oculta
    }

    private fun actualizarModoEdicion(isEditing: Boolean) {
        if (isEditing) {
            // Modo edición
            binding.btnEditarPerfil.text = "Guardar"
            binding.etNombre.isEnabled = true
            binding.etApellidos.isEnabled = true
            binding.etTelefono.isEnabled = true
            binding.etEmail.isEnabled = false // El email no se puede editar
            binding.etContrasena.isEnabled = false // La contraseña no se puede editar desde aquí
        } else {
            // Modo visualización
            binding.btnEditarPerfil.text = "Editar Perfil"
            binding.etNombre.isEnabled = false
            binding.etApellidos.isEnabled = false
            binding.etTelefono.isEnabled = false
            binding.etEmail.isEnabled = false
            binding.etContrasena.isEnabled = false
        }
    }

    private fun guardarCambios() {
        val nombre = binding.etNombre.text.toString().trim()
        val apellidos = binding.etApellidos.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        if (apellidos.isEmpty()) {
            Toast.makeText(this, "Los apellidos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar usuario
        viewModel.actualizarUsuario(this, nombre, apellidos, telefono)
    }
} 