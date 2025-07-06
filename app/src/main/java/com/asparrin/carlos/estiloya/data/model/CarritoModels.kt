package com.asparrin.carlos.estiloya.data.model

import com.google.gson.annotations.SerializedName

// Modelo para item del carrito
data class CarritoItem(
    val id: Long,
    @SerializedName("productoId")
    val productoId: Long,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val subtotal: Double,
    val imagen: String
)

// Modelo para agregar producto al carrito
data class AgregarAlCarritoRequest(
    @SerializedName("productoId")
    val productoId: Long,
    val cantidad: Int
)

// Modelo para actualizar cantidad en carrito
data class ActualizarCantidadRequest(
    @SerializedName("productoId")
    val productoId: Long,
    val cantidad: Int
)

// Modelo para respuesta de operaciones del carrito
data class CarritoResponse(
    val success: Boolean,
    val message: String,
    val data: List<CarritoItem>? = null
)

// Modelo para resumen de compra
data class ResumenCompra(
    val subtotal: Double,
    val descuento: Double,
    val total: Double,
    @SerializedName("cantidadItems")
    val cantidadItems: Int
)

// Modelo para respuesta del resumen
data class ResumenCompraResponse(
    val success: Boolean,
    val message: String,
    val data: ResumenCompra? = null
)

// Modelo para finalizar compra
data class FinalizarCompraResponse(
    val success: Boolean,
    val message: String,
    val ordenId: String? = null
) 