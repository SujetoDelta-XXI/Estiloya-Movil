package com.asparrin.carlos.estiloya.data.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val password: String,
    val telefono: String? = null,
    @SerializedName("fecha_registro")
    val fechaRegistro: String = "", // lo simulamos como texto
    @SerializedName("correo_auth")
    val correoAuth: String? = null,
    @SerializedName("tiene_2fa")
    val tiene2fa: Boolean = false,
    val rol: String = "USER" // 'USER' o 'ADMIN'
)
