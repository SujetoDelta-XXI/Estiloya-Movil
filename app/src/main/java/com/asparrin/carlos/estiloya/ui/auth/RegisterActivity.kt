package com.asparrin.carlos.estiloya.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.data.mock.MockUsuario
import com.asparrin.carlos.estiloya.data.model.Usuario
import com.asparrin.carlos.estiloya.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val correo = binding.emailEditText.text.toString().trim()
            val nombre = binding.nombreEditText.text.toString().trim()
            val apellidos = binding.apellidosEditText.text.toString().trim()
            val telefono = binding.telefonoEditText.text.toString().trim()
            val clave = binding.passwordEditText.text.toString()

            if (!correo.endsWith("@tecsup.edu.pe")) {
                Toast.makeText(this, "Correo debe ser institucional", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoUsuario = Usuario(
                id = 0,
                nombre = nombre,
                apellidos = apellidos,
                correo = correo,
                contraseña = clave,
                telefono = telefono,
                rol = "USER"
            )

            val registrado = MockUsuario.registrar(nuevoUsuario)

            if (registrado) {
                Toast.makeText(this, "✅ Registro exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "❌ Este correo ya existe", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
