package com.asparrin.carlos.estiloya.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class CustomDesign(
    val id: Long,
    @SerializedName("url_imagen")
    val urlImagen: String,
    val descripcion: String,
    @SerializedName("fecha_creacion")
    val fechaCreacion: String, // LocalDate se serializa como String
    val estado: String, // aprobado|denegado|pendiente
    val usuario: Usuario? = null
)