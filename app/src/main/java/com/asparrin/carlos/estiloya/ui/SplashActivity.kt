package com.asparrin.carlos.estiloya.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.databinding.ActivitySplashBinding
import com.asparrin.carlos.estiloya.ui.auth.LoginActivity
import com.asparrin.carlos.estiloya.ui.home.HomeActivity
import com.asparrin.carlos.estiloya.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        // Mostrar splash por 3 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 3000) // 3 segundos
    }

    private fun navigateToNextScreen() {
        val intent = if (session.estaLogueado()) {
            // Si ya está logueado, ir a Home
            Intent(this, HomeActivity::class.java)
        } else {
            // Si no está logueado, ir a Login
            Intent(this, LoginActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
} 