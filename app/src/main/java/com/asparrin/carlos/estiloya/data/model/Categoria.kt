package com.asparrin.carlos.estiloya.data.model

import com.google.gson.annotations.SerializedName

data class Categoria(
    val id: Long,
    val nombre: String,
    @SerializedName("descripcion")
    val descripcion: String? = null,
    @SerializedName("imagen")
    val imagenUrl: String? = null,
    val activo: Boolean = true
) 