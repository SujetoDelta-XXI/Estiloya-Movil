package com.asparrin.carlos.estiloya.data.model

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val password: String,
    val telefono: String? = null,
    val fechaRegistro: String = "", // lo simulamos como texto
    val correoAuth: String? = null,
    val tiene2fa: Boolean = false,
    val rol: String = "USER" // 'USER' o 'ADMIN'
)
