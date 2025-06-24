package com.asparrin.carlos.estiloya.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.data.mock.MockUsuario
import com.asparrin.carlos.estiloya.databinding.ActivityLoginBinding
import com.asparrin.carlos.estiloya.ui.home.HomeActivity
import com.asparrin.carlos.estiloya.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.loginButton.setOnClickListener {
            val correo = binding.emailEditText.text.toString().trim()
            val clave = binding.passwordEditText.text.toString()

            val usuario = MockUsuario.validarLogin(correo, clave)

            if (usuario != null) {
                session.guardarUsuario(usuario)  // ← Guardamos en SharedPreferences

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("nombreUsuario", usuario.nombre)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
