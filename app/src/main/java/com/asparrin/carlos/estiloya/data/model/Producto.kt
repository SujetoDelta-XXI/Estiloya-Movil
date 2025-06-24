package com.asparrin.carlos.estiloya.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val imagenUrl: String,
    val precio: Double,
    val descuento: Int = 0,
    val categoria: String,
    val tipo: String
) : Parcelable
