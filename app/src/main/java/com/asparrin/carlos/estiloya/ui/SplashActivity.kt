package com.asparrin.carlos.estiloya.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.databinding.ActivitySplashBinding
import com.asparrin.carlos.estiloya.ui.auth.LoginActivity
import com.asparrin.carlos.estiloya.ui.auth.TwoFactorActivity
import com.asparrin.carlos.estiloya.ui.home.HomeActivity
import com.asparrin.carlos.estiloya.utils.SessionManager
import com.asparrin.carlos.estiloya.viewModel.AuthState
import com.asparrin.carlos.estiloya.viewModel.AuthViewModel

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager
    
    companion object {
        private const val TAG = "SplashActivity"
        private const val SPLASH_DELAY = 2000L // 2 segundos
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inicializar ViewModel y SessionManager
        authViewModel = AuthViewModel(this)
        sessionManager = SessionManager(this)
        
        setupObservers()
        
        // Verificar estado de autenticación después de un delay
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationState()
        }, SPLASH_DELAY)
    }
    
    private fun setupObservers() {
        // Observar estado de autenticación
        authViewModel.authState.observe(this) { state ->
            when (state) {
                AuthState.AUTHENTICATED -> {
                    Log.d(TAG, "Usuario autenticado, navegando a Home")
                    navigateToHome()
                }
                AuthState.REQUIRES_2FA -> {
                    Log.d(TAG, "Usuario requiere 2FA, navegando a TwoFactorActivity")
                    navigateToTwoFactor()
                }
                AuthState.NOT_AUTHENTICATED -> {
                    Log.d(TAG, "Usuario no autenticado, navegando a Login")
                    navigateToLogin()
                }
                AuthState.LOADING -> {
                    Log.d(TAG, "Verificando autenticación...")
                }
            }
        }
    }
    
    private fun checkAuthenticationState() {
        Log.d(TAG, "Verificando estado de autenticación...")
        
        // Verificar si hay token de autenticación
        val authToken = sessionManager.getAuthToken()
        val tempToken = sessionManager.getTemporaryToken()
        val user = sessionManager.getUser()
        
        Log.d(TAG, "Auth token: ${authToken != null}")
        Log.d(TAG, "Temporary token: ${tempToken != null}")
        Log.d(TAG, "User: ${user != null}")
        
        if (authToken != null && user != null) {
            // Usuario completamente autenticado
            Log.d(TAG, "Usuario completamente autenticado")
            authViewModel.checkAuthState()
        } else if (tempToken != null) {
            // Usuario en proceso de 2FA
            Log.d(TAG, "Usuario en proceso de 2FA")
            authViewModel.checkAuthState()
        } else {
            // Usuario no autenticado
            Log.d(TAG, "Usuario no autenticado")
            authViewModel.checkAuthState()
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun navigateToTwoFactor() {
        val intent = Intent(this, TwoFactorActivity::class.java)
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