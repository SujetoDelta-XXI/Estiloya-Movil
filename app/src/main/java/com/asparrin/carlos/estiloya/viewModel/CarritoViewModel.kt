package com.asparrin.carlos.estiloya.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asparrin.carlos.estiloya.api.ApiClient
import com.asparrin.carlos.estiloya.api.CarritoService
import com.asparrin.carlos.estiloya.api.CarritoRepository
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
    
    private var carritoRepository: CarritoRepository? = null
    
    /**
     * Carga el carrito actual del usuario
     */
    fun cargarCarrito(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Inicializar repository si es necesario
                if (carritoRepository == null) {
                    val carritoService = ApiClient.createCarritoService(context)
                    carritoRepository = CarritoRepository(carritoService)
                }
                
                val result = carritoRepository!!.obtenerCarrito()
                if (result.isSuccess) {
                    val items = result.getOrNull() ?: emptyList()
                    _carritoItems.value = items
                    Log.d("CarritoViewModel", "Carrito cargado: ${items.size} items")
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al cargar el carrito"
                    Log.e("CarritoViewModel", "Error al cargar carrito", result.exceptionOrNull())
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
                // Inicializar repository si es necesario
                if (carritoRepository == null) {
                    val carritoService = ApiClient.createCarritoService(context)
                    carritoRepository = CarritoRepository(carritoService)
                }
                
                val result = carritoRepository!!.agregarProducto(productoId, cantidad)
                if (result.isSuccess) {
                    val carritoResponse = result.getOrNull()
                    if (carritoResponse != null && carritoResponse.success) {
                        _carritoItems.value = carritoResponse.data ?: emptyList()
                        _successMessage.value = "Producto agregado al carrito"
                        Log.d("CarritoViewModel", "Producto agregado: ${carritoResponse.data?.size ?: 0} items")
                        
                        // Actualizar resumen automáticamente
                        if (carritoResponse.data?.isNotEmpty() == true) {
                            cargarResumenCompra(context, false)
                        } else {
                            // Si el carrito está vacío, limpiar resumen
                            _resumenCompra.value = null
                        }
                    }
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al agregar producto"
                    Log.e("CarritoViewModel", "Error al agregar producto", result.exceptionOrNull())
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
                // Inicializar repository si es necesario
                if (carritoRepository == null) {
                    val carritoService = ApiClient.createCarritoService(context)
                    carritoRepository = CarritoRepository(carritoService)
                }
                
                val result = carritoRepository!!.actualizarCantidad(productoId, nuevaCantidad)
                if (result.isSuccess) {
                    val carritoResponse = result.getOrNull()
                    if (carritoResponse != null && carritoResponse.success) {
                        _carritoItems.value = carritoResponse.data ?: emptyList()
                        _successMessage.value = "Cantidad actualizada"
                        Log.d("CarritoViewModel", "Cantidad actualizada: ${carritoResponse.data?.size ?: 0} items")
                        
                        // Actualizar resumen automáticamente
                        if (carritoResponse.data?.isNotEmpty() == true) {
                            cargarResumenCompra(context, false)
                        } else {
                            // Si el carrito está vacío, limpiar resumen
                            _resumenCompra.value = null
                        }
                    }
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al actualizar cantidad"
                    Log.e("CarritoViewModel", "Error al actualizar cantidad", result.exceptionOrNull())
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
                // Inicializar repository si es necesario
                if (carritoRepository == null) {
                    val carritoService = ApiClient.createCarritoService(context)
                    carritoRepository = CarritoRepository(carritoService)
                }
                
                val result = carritoRepository!!.eliminarProducto(itemId)
                if (result.isSuccess) {
                    val carritoResponse = result.getOrNull()
                    if (carritoResponse != null && carritoResponse.success) {
                        _carritoItems.value = carritoResponse.data ?: emptyList()
                        _successMessage.value = "Producto eliminado del carrito"
                        Log.d("CarritoViewModel", "Producto eliminado: ${carritoResponse.data?.size ?: 0} items")
                        
                        // Actualizar resumen automáticamente
                        if (carritoResponse.data?.isNotEmpty() == true) {
                            cargarResumenCompra(context, false)
                        } else {
                            // Si el carrito está vacío, limpiar resumen
                            _resumenCompra.value = null
                        }
                    }
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al eliminar producto"
                    Log.e("CarritoViewModel", "Error al eliminar producto", result.exceptionOrNull())
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
    fun cargarResumenCompra(context: Context, mostrarLoading: Boolean = true) {
        viewModelScope.launch {
            if (mostrarLoading) {
                _isLoading.value = true
            }
            _error.value = null
            
            try {
                // Inicializar repository si es necesario
                if (carritoRepository == null) {
                    val carritoService = ApiClient.createCarritoService(context)
                    carritoRepository = CarritoRepository(carritoService)
                }
                
                val result = carritoRepository!!.obtenerResumen()
                if (result.isSuccess) {
                    val resumen = result.getOrNull()
                    if (resumen != null) {
                        _resumenCompra.value = ResumenCompraResponse(true, "Resumen obtenido", resumen)
                        Log.d("CarritoViewModel", "Resumen cargado: ${resumen.subtotal}")
                    }
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al cargar resumen"
                    Log.e("CarritoViewModel", "Error al cargar resumen", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CarritoViewModel", "Error al cargar resumen", e)
            } finally {
                if (mostrarLoading) {
                    _isLoading.value = false
                }
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
                // Inicializar repository si es necesario
                if (carritoRepository == null) {
                    val carritoService = ApiClient.createCarritoService(context)
                    carritoRepository = CarritoRepository(carritoService)
                }
                
                val result = carritoRepository!!.finalizarCompra()
                if (result.isSuccess) {
                    val finalizarResponse = result.getOrNull()
                    if (finalizarResponse != null && finalizarResponse.success) {
                        _successMessage.value = "Compra finalizada exitosamente"
                        // Limpiar carrito después de finalizar
                        _carritoItems.value = emptyList()
                        _resumenCompra.value = null
                        Log.d("CarritoViewModel", "Compra finalizada: ${finalizarResponse.ordenId}")
                    }
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al finalizar compra"
                    Log.e("CarritoViewModel", "Error al finalizar compra", result.exceptionOrNull())
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
                // Inicializar repository si es necesario
                if (carritoRepository == null) {
                    val carritoService = ApiClient.createCarritoService(context)
                    carritoRepository = CarritoRepository(carritoService)
                }
                
                val result = carritoRepository!!.vaciarCarrito()
                if (result.isSuccess) {
                    _carritoItems.value = emptyList()
                    _resumenCompra.value = null
                    _successMessage.value = "Carrito vaciado exitosamente"
                    Log.d("CarritoViewModel", "Carrito vaciado")
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al vaciar carrito"
                    Log.e("CarritoViewModel", "Error al vaciar carrito", result.exceptionOrNull())
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