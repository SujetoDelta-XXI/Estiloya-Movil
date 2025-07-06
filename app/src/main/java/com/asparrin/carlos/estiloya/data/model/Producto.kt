package com.asparrin.carlos.estiloya.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.math.BigDecimal
import java.time.LocalDate

@Parcelize
data class Descuento(
    val id: Long,
    val porcentaje: Int
) : Parcelable

@Parcelize
data class Producto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val imagen: String,
    val precio: @RawValue BigDecimal? = null,
    val stock: Int,
    val tipo: String,
    @SerializedName("fecha_creacion")
    val fechaCreacion: @RawValue LocalDate? = null,
    val estado: String,
    val categoria: @RawValue Categoria? = null,
    val descuento: Descuento? = null
) : Parcelable {
    /**
     * Devuelve el porcentaje de descuento, o 0 si no hay descuento.
     */
    val descuentoPorcentajeCalculado: Int
        get() = descuento?.porcentaje ?: 0

    /**
     * Precio tras aplicar el descuento.
     */
    val precioConDescuento: BigDecimal
        get() = if (precio != null && descuentoPorcentajeCalculado > 0) {
            precio.multiply(BigDecimal.valueOf((100 - descuentoPorcentajeCalculado).toLong()))
                .divide(BigDecimal.valueOf(100))
        } else {
            precio ?: BigDecimal.ZERO
        }

    /**
     * Nombre de la categoría para compatibilidad con código existente
     */
    val categoriaNombre: String
        get() = categoria?.nombre ?: ""

    /**
     * Porcentaje de descuento para compatibilidad con código existente
     */
    val descuentoPorcentaje: Int?
        get() = descuento?.porcentaje
}
