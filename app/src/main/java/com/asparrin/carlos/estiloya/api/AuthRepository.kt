package com.asparrin.carlos.estiloya.api

import android.content.Context
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
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en el login: ${response.code()}"))
            }
        } catch (e: Exception) {
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
            val request = TwoFactorRequest(correo, code)
            val response = authService.verify2FA(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en la verificación 2FA: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun registerAlternativeEmail(correo: String, alternativeEmail: String): Result<RegisterEmailResponse> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterEmailRequest(correo, alternativeEmail)
            val response = authService.registerAlternativeEmail(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al registrar email alternativo: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun send2FACode(correo: String): Result<SendCodeResponse> = withContext(Dispatchers.IO) {
        try {
            val request = SendCodeRequest(correo)
            val response = authService.send2FACode(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al enviar código 2FA: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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