package com.asparrin.carlos.estiloya.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asparrin.carlos.estiloya.api.ApiClient
import com.asparrin.carlos.estiloya.api.CarritoService
import com.asparrin.carlos.estiloya.data.model.*
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {
    
    private val _carritoItems = MutableLiveData<List<CarritoItem>>()
    val carritoItems: LiveData<List<CarritoItem>> = _carritoItems
    
    private val _resumenCompra = MutableLiveData<ResumenCompraResponse?>()
    val resumenCompra: LiveData<ResumenCompraResponse?> = _resumenCompra
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    /**
     * Carga el carrito actual del usuario
     */
    fun cargarCarrito(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val carritoService = ApiClient.createCarritoService(context)
                val response = carritoService.obtenerCarrito()
                
                if (response.isSuccessful) {
                    val items = response.body() ?: emptyList()
                    _carritoItems.value = items
                    Log.d("CarritoViewModel", "Carrito cargado: ${items.size} items")
                } else {
                    _error.value = "Error al cargar el carrito: ${response.code()}"
                    Log.e("CarritoViewModel", "Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CarritoViewModel", "Error al cargar carrito", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Agrega un producto al carrito
     */
    fun agregarProducto(context: Context, productoId: Long, cantidad: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            
            try {
                val carritoService = ApiClient.createCarritoService(context)
                val request = AgregarAlCarritoRequest(productoId, cantidad)
                val response = carritoService.agregarProducto(request)
                
                if (response.isSuccessful) {
                    val carritoResponse = response.body()
                    if (carritoResponse != null && carritoResponse.success) {
                        _carritoItems.value = carritoResponse.data ?: emptyList()
                        _successMessage.value = "Producto agregado al carrito"
                        Log.d("CarritoViewModel", "Producto agregado: ${carritoResponse.data?.size ?: 0} items")
                    }
                } else {
                    _error.value = "Error al agregar producto: ${response.code()}"
                    Log.e("CarritoViewModel", "Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CarritoViewModel", "Error al agregar producto", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    fun actualizarCantidad(context: Context, productoId: Long, nuevaCantidad: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            
            try {
                val carritoService = ApiClient.createCarritoService(context)
                val request = ActualizarCantidadRequest(productoId, nuevaCantidad)
                val response = carritoService.actualizarCantidad(request)
                
                if (response.isSuccessful) {
                    val carritoResponse = response.body()
                    if (carritoResponse != null && carritoResponse.success) {
                        _carritoItems.value = carritoResponse.data ?: emptyList()
                        _successMessage.value = "Cantidad actualizada"
                        Log.d("CarritoViewModel", "Cantidad actualizada: ${carritoResponse.data?.size ?: 0} items")
                    }
                } else {
                    _error.value = "Error al actualizar cantidad: ${response.code()}"
                    Log.e("CarritoViewModel", "Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CarritoViewModel", "Error al actualizar cantidad", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Elimina un producto del carrito
     */
    fun eliminarProducto(context: Context, itemId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            
            try {
                val carritoService = ApiClient.createCarritoService(context)
                val response = carritoService.eliminarProducto(itemId)
                
                if (response.isSuccessful) {
                    val carritoResponse = response.body()
                    if (carritoResponse != null && carritoResponse.success) {
                        _carritoItems.value = carritoResponse.data ?: emptyList()
                        _successMessage.value = "Producto eliminado del carrito"
                        Log.d("CarritoViewModel", "Producto eliminado: ${carritoResponse.data?.size ?: 0} items")
                    }
                } else {
                    _error.value = "Error al eliminar producto: ${response.code()}"
                    Log.e("CarritoViewModel", "Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CarritoViewModel", "Error al eliminar producto", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Carga el resumen de compra
     */
    fun cargarResumenCompra(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val carritoService = ApiClient.createCarritoService(context)
                val response = carritoService.obtenerResumen()
                
                if (response.isSuccessful) {
                    val resumen = response.body()
                    if (resumen != null && resumen.success) {
                        _resumenCompra.value = resumen
                        Log.d("CarritoViewModel", "Resumen cargado: ${resumen.data?.subtotal}")
                    }
                } else {
                    _error.value = "Error al cargar resumen: ${response.code()}"
                    Log.e("CarritoViewModel", "Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CarritoViewModel", "Error al cargar resumen", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Finaliza la compra
     */
    fun finalizarCompra(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            
            try {
                val carritoService = ApiClient.createCarritoService(context)
                val response = carritoService.finalizarCompra()
                
                if (response.isSuccessful) {
                    val finalizarResponse = response.body()
                    if (finalizarResponse != null && finalizarResponse.success) {
                        _successMessage.value = "Compra finalizada exitosamente"
                        // Limpiar carrito después de finalizar
                        _carritoItems.value = emptyList()
                        _resumenCompra.value = null
                        Log.d("CarritoViewModel", "Compra finalizada: ${finalizarResponse.ordenId}")
                    }
                } else {
                    _error.value = "Error al finalizar compra: ${response.code()}"
                    Log.e("CarritoViewModel", "Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CarritoViewModel", "Error al finalizar compra", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Vacía el carrito completo
     */
    fun vaciarCarrito(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            
            try {
                val carritoService = ApiClient.createCarritoService(context)
                val items = _carritoItems.value ?: emptyList()
                
                // Eliminar todos los items del carrito
                var todosEliminados = true
                for (item in items) {
                    val response = carritoService.eliminarProducto(item.id)
                    if (!response.isSuccessful) {
                        todosEliminados = false
                        break
                    }
                }
                
                if (todosEliminados) {
                    _carritoItems.value = emptyList()
                    _resumenCompra.value = null
                    _successMessage.value = "Carrito vaciado exitosamente"
                    Log.d("CarritoViewModel", "Carrito vaciado")
                } else {
                    _error.value = "Error al vaciar el carrito"
                    Log.e("CarritoViewModel", "Error al vaciar carrito")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CarritoViewModel", "Error al vaciar carrito", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Limpia los mensajes de error y éxito
     */
    fun limpiarMensajes() {
        _error.value = null
        _successMessage.value = null
    }
    
    /**
     * Calcula el total de items en el carrito
     */
    fun getTotalItems(): Int {
        return _carritoItems.value?.sumOf { it.cantidad } ?: 0
    }
    
    /**
     * Calcula el subtotal del carrito
     */
    fun getSubtotal(): Double {
        return _carritoItems.value?.sumOf { it.precio * it.cantidad } ?: 0.0
    }
} 