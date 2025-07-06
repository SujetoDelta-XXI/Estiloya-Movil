package com.asparrin.carlos.estiloya.api

import android.content.Context
import android.util.Log
import com.asparrin.carlos.estiloya.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    
    private val authService = ApiClient.createAuthService(context)
    
    // Métodos de autenticación básica
    suspend fun register(
        nombre: String,
        apellidos: String,
        correo: String,
        contraseña: String,
        telefono: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(nombre, apellidos, correo, contraseña, telefono)
            val response = authService.register(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en el registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(correo: String, contraseña: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(correo, contraseña)
            val response = authService.login(request)
            
            Log.d("AuthRepository", "Login response code: ${response.code()}")
            Log.d("AuthRepository", "Login response body: ${response.body()}")
            Log.d("AuthRepository", "Login response error body: ${response.errorBody()?.string()}")
            
            if (response.isSuccessful) {
                val authResponse = response.body()!!
                Log.d("AuthRepository", "Login exitoso - requiere2FA: ${authResponse.requiere2FA}")
                Log.d("AuthRepository", "Login exitoso - metodos: ${authResponse.metodos}")
                Log.d("AuthRepository", "Login exitoso - user: ${authResponse.user}")
                Log.d("AuthRepository", "Login exitoso - correo: ${authResponse.correo}")
                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Error en el login: ${response.code()}"
                Log.e("AuthRepository", "Login error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception", e)
            Result.failure(e)
        }
    }
    
    suspend fun loginWithGoogle(idToken: String): Result<GoogleLoginResponse> = withContext(Dispatchers.IO) {
        try {
            val request = GoogleLoginRequest(idToken)
            val response = authService.loginWithGoogle(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en login con Google: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Métodos de 2FA
    suspend fun verify2FA(correo: String, code: String): Result<TwoFactorResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d("AuthRepository", "=== VERIFY 2FA REPOSITORY ===")
            Log.d("AuthRepository", "Correo: $correo")
            Log.d("AuthRepository", "Código: $code")
            Log.d("AuthRepository", "=== END VERIFY 2FA REPOSITORY ===")
            
            val response = authService.verify2FA(code)
            
            Log.d("AuthRepository", "Verify 2FA response code: ${response.code()}")
            Log.d("AuthRepository", "Verify 2FA response body: ${response.body()}")
            Log.d("AuthRepository", "Verify 2FA response error body: ${response.errorBody()?.string()}")
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Error en la verificación 2FA: ${response.code()}"
                Log.e("AuthRepository", "Verify 2FA error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Verify 2FA exception", e)
            Result.failure(e)
        }
    }
    
    suspend fun registerAlternativeEmail(correo: String, alternativeEmail: String): Result<RegisterEmailResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d("AuthRepository", "Registrando email alternativo: $alternativeEmail para correo: $correo")
            val response = authService.registerAlternativeEmail(alternativeEmail)
            
            Log.d("AuthRepository", "Response code: ${response.code()}")
            Log.d("AuthRepository", "Response body: ${response.body()}")
            Log.d("AuthRepository", "Response error body: ${response.errorBody()?.string()}")
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                // Intentar leer el mensaje de error del body
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Error al registrar email alternativo: ${response.code()}"
                Log.e("AuthRepository", "Error registering alternative email: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Exception registering alternative email", e)
            Result.failure(e)
        }
    }
    
    suspend fun send2FACode(correo: String): Result<SendCodeResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d("AuthRepository", "Enviando código 2FA para correo: $correo")
            val response = authService.send2FACode("correo")
            
            Log.d("AuthRepository", "Response code: ${response.code()}")
            Log.d("AuthRepository", "Response body: ${response.body()}")
            Log.d("AuthRepository", "Response error body: ${response.errorBody()?.string()}")
            
            if (response.isSuccessful) {
                // El backend devuelve texto plano, no JSON
                // Crear respuesta manual basada en el código de respuesta
                Log.d("AuthRepository", "Backend devolvió texto plano (código 200), creando respuesta manual")
                val manualResponse = SendCodeResponse(
                    success = true,
                    message = "Código enviado exitosamente"
                )
                Result.success(manualResponse)
            } else {
                // Intentar leer el mensaje de error del body
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Error al enviar código 2FA: ${response.code()}"
                Log.e("AuthRepository", "Error sending 2FA code: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Exception sending 2FA code", e)
            // Si hay error de JSON malformado, crear respuesta manual
            if (e.message?.contains("malformed JSON") == true) {
                Log.d("AuthRepository", "Backend devolvió texto plano, creando respuesta manual después de error")
                val manualResponse = SendCodeResponse(
                    success = true,
                    message = "Código enviado exitosamente"
                )
                Result.success(manualResponse)
            } else {
                Result.failure(e)
            }
        }
    }
    
    // Métodos de recuperación de contraseña
    suspend fun forgotPassword(correo: String): Result<ForgotPasswordResponse> = withContext(Dispatchers.IO) {
        try {
            val request = ForgotPasswordRequest(correo)
            val response = authService.forgotPassword(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al solicitar recuperación: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resetPassword(correo: String, token: String, contraseña: String): Result<ResetPasswordResponse> = withContext(Dispatchers.IO) {
        try {
            val request = ResetPasswordRequest(correo, token, contraseña)
            val response = authService.resetPassword(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al restablecer contraseña: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Método de prueba de conexión
    suspend fun testConnection(): Result<TestConnectionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authService.testConnection()
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en prueba de conexión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Métodos de perfil de usuario
    suspend fun getProfile(): Result<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authService.getProfile()
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(nombre: String, apellidos: String, telefono: String): Result<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateProfileRequest(nombre, apellidos, telefono)
            val response = authService.updateProfile(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 