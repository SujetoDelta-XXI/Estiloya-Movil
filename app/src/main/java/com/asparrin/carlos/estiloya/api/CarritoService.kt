package com.asparrin.carlos.estiloya.api

import com.asparrin.carlos.estiloya.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface CarritoService {
    
    /**
     * Obtener carrito actual del usuario
     */
    @GET("usuario/carrito")
    suspend fun obtenerCarrito(): Response<List<CarritoItem>>
    
    /**
     * Agregar producto al carrito
     */
    @POST("usuario/carrito/agregar")
    suspend fun agregarProducto(
        @Body request: AgregarAlCarritoRequest
    ): Response<Unit>
    
    /**
     * Actualizar cantidad de producto en el carrito
     */
    @PUT("usuario/carrito/actualizar")
    suspend fun actualizarCantidad(
        @Body request: ActualizarCantidadRequest
    ): Response<Unit>
    
    /**
     * Eliminar producto del carrito
     */
    @DELETE("usuario/carrito/eliminar/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long
    ): Response<Unit>
    
    /**
     * Obtener resumen de compra
     */
    @GET("usuario/carrito/resumen")
    suspend fun obtenerResumen(): Response<ResumenCompra>
    
    /**
     * Finalizar compra
     */
    @POST("usuario/carrito/finalizar")
    suspend fun finalizarCompra(): Response<FinalizarCompraResponse>
    
    /**
     * Obtener producto por ID (para validaciones)
     */
    @GET("usuario/productos/{id}")
    suspend fun obtenerProductoPorId(
        @Path("id") id: Long
    ): Response<Producto>
} 