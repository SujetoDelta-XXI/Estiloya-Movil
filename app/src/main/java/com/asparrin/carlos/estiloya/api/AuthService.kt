package com.asparrin.carlos.estiloya.api

import com.asparrin.carlos.estiloya.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    
    // Endpoints de autenticación básica
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/login-google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<GoogleLoginResponse>
    
    // Endpoints de 2FA
    @POST("auth/login/verify-2fa")
    suspend fun verify2FA(@Query("code") code: String): Response<TwoFactorResponse>
    
    @POST("auth/2fa/register-email")
    suspend fun registerAlternativeEmail(@Query("alternativo") alternativeEmail: String): Response<RegisterEmailResponse>
    
    @POST("auth/2fa/send-code")
    suspend fun send2FACode(@Query("metodo") metodo: String = "correo"): Response<SendCodeResponse>
    
    // Endpoints de recuperación de contraseña
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>
    
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>
    
    // Endpoint de prueba de conexión
    @GET("auth/test-connection")
    suspend fun testConnection(): Response<TestConnectionResponse>
    
    // Endpoints de perfil de usuario
    @GET("usuario/perfil")
    suspend fun getProfile(): Response<ProfileResponse>
    
    @PUT("usuario/perfil")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ProfileResponse>
} 