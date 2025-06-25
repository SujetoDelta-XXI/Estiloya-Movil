package com.asparrin.carlos.estiloya.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Descuento(
    val id: Long,
    @SerializedName("porcentaje")
    val porcentaje: Int
) : Parcelable

@Parcelize
data class Producto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    @SerializedName("imagen")
    val imagenUrl: String,
    val precio: Double,
    val stock: Int,
    val tipo: String,
    val estado: String,
    @SerializedName("categoriaNombre")
    val categoria: String,
    @SerializedName("descuentoPorcentaje")
    val descuentoPorcentaje: Int? = null,
    @SerializedName("descuento")
    val descuento: Descuento? = null
) : Parcelable {
    /**
     * Devuelve el porcentaje de descuento, o 0 si no hay descuento.
     */
    val descuentoPorcentajeCalculado: Int
        get() = descuentoPorcentaje ?: descuento?.porcentaje ?: 0

    /**
     * Precio tras aplicar el descuento.
     */
    val precioConDescuento: Double
        get() = if (descuentoPorcentajeCalculado > 0) {
            precio * (100 - descuentoPorcentajeCalculado) / 100.0
        } else {
            precio
        }
}
