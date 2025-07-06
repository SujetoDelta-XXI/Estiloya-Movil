package com.asparrin.carlos.estiloya.data.model

import com.google.gson.annotations.SerializedName

// Modelos para autenticación básica
data class LoginRequest(
    val correo: String,
    val contraseña: String
)

data class RegisterRequest(
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val contraseña: String,
    val telefono: String
)

data class AuthResponse(
    val success: Boolean = true,
    val message: String? = null,
    val token: String? = null,
    val user: Usuario? = null,
    @SerializedName("requiere2FA")
    val requiere2FA: Boolean = false,
    val metodos: Map<String, Boolean>? = null,
    val correo: String? = null,
    val jwt: String? = null
)

// Modelos para 2FA
data class TwoFactorRequest(
    val correo: String,
    val code: String
)

data class TwoFactorResponse(
    val success: Boolean = true,
    val message: String? = null,
    val token: String? = null,
    val user: Usuario? = null
)

data class SendCodeRequest(
    val correo: String
)

data class SendCodeResponse(
    val success: Boolean,
    val message: String
)

data class RegisterEmailRequest(
    val correo: String,
    val alternativeEmail: String
)

data class RegisterEmailResponse(
    val success: Boolean,
    val message: String
)

// Modelos para recuperación de contraseña
data class ForgotPasswordRequest(
    val correo: String
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String
)

data class ResetPasswordRequest(
    val correo: String,
    val token: String,
    val contraseña: String
)

data class ResetPasswordResponse(
    val success: Boolean,
    val message: String
)

// Modelo para login con Google
data class GoogleLoginRequest(
    val idToken: String
)

data class GoogleLoginResponse(
    val success: Boolean = true,
    val message: String? = null,
    val token: String? = null,
    val user: Usuario? = null,
    @SerializedName("requiere2FA")
    val requiere2FA: Boolean = false,
    val metodos: Map<String, Boolean>? = null,
    val correo: String? = null,
    val jwt: String? = null
)

// Modelo para prueba de conexión
data class TestConnectionResponse(
    val success: Boolean,
    val message: String,
    val timestamp: String? = null
)

// Modelo para perfil de usuario
data class UpdateProfileRequest(
    val nombre: String,
    val apellidos: String,
    val telefono: String
)

data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val user: Usuario? = null
) 