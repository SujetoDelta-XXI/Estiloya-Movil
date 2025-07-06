package com.asparrin.carlos.estiloya.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.databinding.ActivitySplashBinding
import com.asparrin.carlos.estiloya.ui.auth.LoginActivity
import com.asparrin.carlos.estiloya.ui.home.HomeActivity
import com.asparrin.carlos.estiloya.utils.SessionManager

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager
    
    companion object {
        private const val TAG = "SplashActivity"
        private const val SPLASH_DELAY = 2000L // 2 segundos
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inicializar SessionManager
        sessionManager = SessionManager(this)
        
        // Verificar estado de autenticación después de un delay
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationState()
        }, SPLASH_DELAY)
    }
    

    
    private fun checkAuthenticationState() {
        Log.d(TAG, "Verificando estado de autenticación...")
        
        // Verificar si hay token de autenticación
        val authToken = sessionManager.getAuthToken()
        val user = sessionManager.getUser()
        
        Log.d(TAG, "Auth token: ${authToken != null}")
        Log.d(TAG, "User: ${user != null}")
        
        if (authToken != null && user != null) {
            // Usuario completamente autenticado
            Log.d(TAG, "Usuario completamente autenticado, navegando a Home")
            navigateToHome()
        } else {
            // Usuario no autenticado o en proceso incompleto
            // Limpiar cualquier estado incompleto y ir a login
            Log.d(TAG, "Usuario no autenticado o proceso incompleto, limpiando y navegando a login")
            sessionManager.clearSession()
            navigateToLogin()
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    

    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
} 