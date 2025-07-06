package com.asparrin.carlos.estiloya.api

import android.content.Context
import android.util.Log
import com.asparrin.carlos.estiloya.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    
    private val sessionManager = SessionManager(context)
    
    companion object {
        private const val TAG = "AuthInterceptor"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Lista de endpoints que NO requieren autenticación
        val publicEndpoints = listOf(
            "auth/register",
            "auth/login", 
            "auth/login-google",
            "auth/login/verify-2fa",
            "auth/2fa/register-email",
            "auth/2fa/send-code",
            "auth/forgot-password",
            "auth/reset-password",
            "auth/test-connection"
        )
        
        // Verificar si el endpoint actual es público
        val isPublicEndpoint = publicEndpoints.any { endpoint ->
            originalRequest.url.encodedPath.contains(endpoint)
        }
        
        Log.d(TAG, "Request URL: ${originalRequest.url}")
        Log.d(TAG, "Is public endpoint: $isPublicEndpoint")
        
        if (isPublicEndpoint) {
            Log.d(TAG, "Skipping authentication for public endpoint")
            return chain.proceed(originalRequest)
        }
        
        // Obtener el token activo (principal o temporal)
        val token = sessionManager.getActiveToken()
        
        Log.d(TAG, "Token available: ${token != null}")
        Log.d(TAG, "Auth token: ${sessionManager.getAuthToken() != null}")
        Log.d(TAG, "Temporary token: ${sessionManager.getTemporaryToken() != null}")
        
        if (token != null) {
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            
            Log.d(TAG, "Adding Authorization header with token")
            return chain.proceed(authenticatedRequest)
        } else {
            Log.d(TAG, "No token available, proceeding without authentication")
            return chain.proceed(originalRequest)
        }
    }
} 