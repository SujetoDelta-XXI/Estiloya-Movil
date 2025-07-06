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
            "auth/forgot-password",
            "auth/reset-password",
            "auth/test-connection"
            // NOTA: auth/login/verify-2fa NO está aquí porque requiere token temporal
        )
        
        // Verificar si el endpoint actual es público
        val requestPath = originalRequest.url.encodedPath
        val isPublicEndpoint = publicEndpoints.any { endpoint ->
            // Verificación más específica para evitar que auth/login/verify-2fa sea marcado como público
            if (endpoint == "auth/login") {
                requestPath == "/api/auth/login" // Solo exactamente auth/login
            } else {
                requestPath.contains(endpoint)
            }
        }
        
        Log.d(TAG, "Request URL: ${originalRequest.url}")
        Log.d(TAG, "Request path: $requestPath")
        Log.d(TAG, "Is public endpoint: $isPublicEndpoint")
        
        // Logging específico para verificación 2FA
        if (originalRequest.url.encodedPath.contains("verify-2fa")) {
            Log.d(TAG, "=== VERIFY 2FA ENDPOINT DEBUG ===")
            Log.d(TAG, "URL: ${originalRequest.url}")
            Log.d(TAG, "Method: ${originalRequest.method}")
            Log.d(TAG, "Headers: ${originalRequest.headers}")
            Log.d(TAG, "=== END VERIFY 2FA DEBUG ===")
        }
        
        if (isPublicEndpoint) {
            Log.d(TAG, "Skipping authentication for public endpoint")
            return chain.proceed(originalRequest)
        }
        
        // Obtener el token activo (principal o temporal)
        val token = sessionManager.getActiveToken()
        val authToken = sessionManager.getAuthToken()
        val tempToken = sessionManager.getTemporaryToken()
        
        Log.d(TAG, "=== TOKEN DEBUG ===")
        Log.d(TAG, "Active token: ${token != null}")
        Log.d(TAG, "Auth token: ${authToken != null}")
        Log.d(TAG, "Temporary token: ${tempToken != null}")
        Log.d(TAG, "Auth token value: ${authToken?.take(50)}...")
        Log.d(TAG, "Temp token value: ${tempToken?.take(50)}...")
        Log.d(TAG, "Active token value: ${token?.take(50)}...")
        Log.d(TAG, "=== END TOKEN DEBUG ===")
        
        if (token != null) {
            // Verificar que el token no esté vacío o malformado
            if (token.isBlank()) {
                Log.e(TAG, "Token is blank, proceeding without authentication")
                return chain.proceed(originalRequest)
            }
            
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            
            Log.d(TAG, "Adding Authorization header with token: ${token.take(50)}...")
            Log.d(TAG, "Request URL: ${authenticatedRequest.url}")
            Log.d(TAG, "Request method: ${authenticatedRequest.method}")
            Log.d(TAG, "Full Authorization header: Bearer ${token.take(100)}...")
            
            // Logging específico para verificación 2FA
            if (originalRequest.url.encodedPath.contains("verify-2fa")) {
                Log.d(TAG, "=== VERIFY 2FA WITH TOKEN ===")
                Log.d(TAG, "Token type: ${if (sessionManager.getTemporaryToken() != null) "TEMPORARY" else "AUTH"}")
                Log.d(TAG, "Token value: ${token.take(100)}...")
                Log.d(TAG, "Full request headers: ${authenticatedRequest.headers}")
                Log.d(TAG, "=== END VERIFY 2FA WITH TOKEN ===")
            }
            
            return chain.proceed(authenticatedRequest)
        } else {
            Log.d(TAG, "No token available, proceeding without authentication")
            
            // Logging específico para verificación 2FA sin token
            if (originalRequest.url.encodedPath.contains("verify-2fa")) {
                Log.e(TAG, "=== VERIFY 2FA WITHOUT TOKEN ===")
                Log.e(TAG, "ERROR: No token available for verify-2fa endpoint")
                Log.e(TAG, "Auth token: ${sessionManager.getAuthToken() != null}")
                Log.e(TAG, "Temporary token: ${sessionManager.getTemporaryToken() != null}")
                Log.e(TAG, "=== END VERIFY 2FA WITHOUT TOKEN ===")
            }
            
            return chain.proceed(originalRequest)
        }
    }
} 