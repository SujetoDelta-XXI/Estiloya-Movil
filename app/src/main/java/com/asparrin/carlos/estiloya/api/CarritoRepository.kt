package com.asparrin.carlos.estiloya.api

import com.asparrin.carlos.estiloya.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarritoRepository(private val carritoService: CarritoService) {
    
    /**
     * Obtener carrito actual
     */
    suspend fun obtenerCarrito(): Result<List<CarritoItem>> = withContext(Dispatchers.IO) {
        try {
            val response = carritoService.obtenerCarrito()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener carrito: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Agregar producto al carrito
     */
    suspend fun agregarProducto(productoId: Long, cantidad: Int): Result<CarritoResponse> = withContext(Dispatchers.IO) {
        try {
            // Validar cantidad
            if (cantidad <= 0) {
                return@withContext Result.failure(Exception("La cantidad debe ser mayor a 0"))
            }
            
            // Verificar stock disponible
            val producto = obtenerProductoPorId(productoId).getOrNull()
            if (producto != null && producto.stock < cantidad) {
                return@withContext Result.failure(Exception("Stock insuficiente. Disponible: ${producto.stock}"))
            }
            
            val request = AgregarAlCarritoRequest(productoId, cantidad)
            val response = carritoService.agregarProducto(request)
            
            if (response.isSuccessful) {
                // El servidor devuelve una respuesta vacía exitosa, recargar el carrito
                val carritoActual = obtenerCarrito().getOrNull() ?: emptyList()
                Result.success(CarritoResponse(true, "Producto agregado", carritoActual))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                Result.failure(Exception("Error al agregar producto: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Actualizar cantidad de producto
     */
    suspend fun actualizarCantidad(productoId: Long, cantidad: Int): Result<CarritoResponse> = withContext(Dispatchers.IO) {
        try {
            // Si cantidad es 0, eliminar producto
            if (cantidad == 0) {
                return@withContext eliminarProductoPorProductoId(productoId)
            }
            
            // Validar cantidad mínima
            if (cantidad < 0) {
                return@withContext Result.failure(Exception("La cantidad no puede ser negativa"))
            }
            
            // Verificar stock disponible
            val producto = obtenerProductoPorId(productoId).getOrNull()
            if (producto != null && producto.stock < cantidad) {
                return@withContext Result.failure(Exception("Stock insuficiente. Disponible: ${producto.stock}"))
            }
            
            val request = ActualizarCantidadRequest(productoId, cantidad)
            val response = carritoService.actualizarCantidad(request)
            
            if (response.isSuccessful) {
                // El servidor devuelve una respuesta vacía exitosa, recargar el carrito
                val carritoActual = obtenerCarrito().getOrNull() ?: emptyList()
                Result.success(CarritoResponse(true, "Cantidad actualizada", carritoActual))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                Result.failure(Exception("Error al actualizar cantidad: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Eliminar producto del carrito por ID del item
     */
    suspend fun eliminarProducto(itemId: Long): Result<CarritoResponse> = withContext(Dispatchers.IO) {
        try {
            val response = carritoService.eliminarProducto(itemId)
            
            if (response.isSuccessful) {
                // El servidor devuelve una respuesta vacía exitosa, recargar el carrito
                val carritoActual = obtenerCarrito().getOrNull() ?: emptyList()
                Result.success(CarritoResponse(true, "Producto eliminado", carritoActual))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                Result.failure(Exception("Error al eliminar producto: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Eliminar producto del carrito por ID del producto
     */
    private suspend fun eliminarProductoPorProductoId(productoId: Long): Result<CarritoResponse> = withContext(Dispatchers.IO) {
        try {
            // Primero obtener el carrito para encontrar el itemId
            val carrito = obtenerCarrito().getOrNull() ?: emptyList()
            val item = carrito.find { it.productoId == productoId }
            
            if (item != null) {
                eliminarProducto(item.id)
            } else {
                Result.failure(Exception("Producto no encontrado en el carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtener resumen de compra
     */
    suspend fun obtenerResumen(): Result<ResumenCompra> = withContext(Dispatchers.IO) {
        try {
            val response = carritoService.obtenerResumen()
            
            if (response.isSuccessful) {
                val resumen = response.body()
                if (resumen != null) {
                    Result.success(resumen)
                } else {
                    Result.failure(Exception("Resumen no disponible"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                Result.failure(Exception("Error al obtener resumen: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Finalizar compra
     */
    suspend fun finalizarCompra(): Result<FinalizarCompraResponse> = withContext(Dispatchers.IO) {
        try {
            val response = carritoService.finalizarCompra()
            
            if (response.isSuccessful) {
                Result.success(response.body() ?: FinalizarCompraResponse(false, "Respuesta vacía"))
            } else {
                Result.failure(Exception("Error al finalizar compra: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtener producto por ID para validaciones
     */
    private suspend fun obtenerProductoPorId(id: Long): Result<Producto> = withContext(Dispatchers.IO) {
        try {
            val response = carritoService.obtenerProductoPorId(id)
            
            if (response.isSuccessful) {
                val producto = response.body()
                if (producto != null) {
                    Result.success(producto)
                } else {
                    Result.failure(Exception("Producto no encontrado"))
                }
            } else {
                Result.failure(Exception("Error al obtener producto: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Vaciar carrito completo
     */
    suspend fun vaciarCarrito(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val carrito = obtenerCarrito().getOrNull() ?: emptyList()
            var success = true
            
            // Eliminar todos los productos uno por uno
            for (item in carrito) {
                val result = eliminarProducto(item.id)
                if (result.isFailure) {
                    success = false
                }
            }
            
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al vaciar carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 