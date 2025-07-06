package com.asparrin.carlos.estiloya.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asparrin.carlos.estiloya.api.ApiClient
import com.asparrin.carlos.estiloya.api.ProductService
import com.asparrin.carlos.estiloya.data.model.Producto
import com.asparrin.carlos.estiloya.data.mock.MockProducto
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {

    // LiveData para las diferentes categorías de productos
    private val _ofertasDelDia = MutableLiveData<List<Producto>>()
    val ofertasDelDia: LiveData<List<Producto>> = _ofertasDelDia

    private val _ofertasDeLaSemana = MutableLiveData<List<Producto>>()
    val ofertasDeLaSemana: LiveData<List<Producto>> = _ofertasDeLaSemana

    private val _productosMasVendidos = MutableLiveData<List<Producto>>()
    val productosMasVendidos: LiveData<List<Producto>> = _productosMasVendidos

    private val _nuevosProductos = MutableLiveData<List<Producto>>()
    val nuevosProductos: LiveData<List<Producto>> = _nuevosProductos

    // Estados de carga
    private val _isLoadingOfertasDia = MutableLiveData<Boolean>()
    val isLoadingOfertasDia: LiveData<Boolean> = _isLoadingOfertasDia

    private val _isLoadingOfertasSemana = MutableLiveData<Boolean>()
    val isLoadingOfertasSemana: LiveData<Boolean> = _isLoadingOfertasSemana

    private val _isLoadingMasVendidos = MutableLiveData<Boolean>()
    val isLoadingMasVendidos: LiveData<Boolean> = _isLoadingMasVendidos

    private val _isLoadingNuevos = MutableLiveData<Boolean>()
    val isLoadingNuevos: LiveData<Boolean> = _isLoadingNuevos

    // Estados de error
    private val _errorOfertasDia = MutableLiveData<String?>()
    val errorOfertasDia: LiveData<String?> = _errorOfertasDia

    private val _errorOfertasSemana = MutableLiveData<String?>()
    val errorOfertasSemana: LiveData<String?> = _errorOfertasSemana

    private val _errorMasVendidos = MutableLiveData<String?>()
    val errorMasVendidos: LiveData<String?> = _errorMasVendidos

    private val _errorNuevos = MutableLiveData<String?>()
    val errorNuevos: LiveData<String?> = _errorNuevos

    // Estados de filtros
    private val _filtroDescuentoActivo = MutableLiveData<Boolean>(false)
    val filtroDescuentoActivo: LiveData<Boolean> = _filtroDescuentoActivo

    private val _filtroNuevoActivo = MutableLiveData<Boolean>(false)
    val filtroNuevoActivo: LiveData<Boolean> = _filtroNuevoActivo

    // Productos filtrados
    private val _productosFiltrados = MutableLiveData<List<Producto>>()
    val productosFiltrados: LiveData<List<Producto>> = _productosFiltrados



    // Lista temporal de todos los productos para filtrado
    private var todosLosProductos: List<Producto> = emptyList()

    /**
     * Carga las ofertas del día con fallback a datos mock
     */
    fun cargarOfertasDelDia(context: Context) {
        viewModelScope.launch {
            _isLoadingOfertasDia.value = true
            _errorOfertasDia.value = null
            
            try {
                val productService = ApiClient.createProductService(context)
                val response = productService.getOfertasDelDia()
                Log.d("HomeViewModel", "Ofertas del día - HTTP ${response.code()}")
                
                if (response.isSuccessful) {
                    val productos = response.body() ?: emptyList()
                    _ofertasDelDia.value = productos
                    Log.d("HomeViewModel", "Ofertas del día cargadas: ${productos.size} productos")
                } else {
                    // Fallback a datos mock
                    val mockProductos = MockProducto.getListaProductos().filter { it.descuentoPorcentajeCalculado > 0 }
                    _ofertasDelDia.value = mockProductos
                    Log.d("HomeViewModel", "Usando datos mock para ofertas del día: ${mockProductos.size} productos")
                }
            } catch (e: Exception) {
                // Fallback a datos mock en caso de error
                val mockProductos = MockProducto.getListaProductos().filter { it.descuentoPorcentajeCalculado > 0 }
                _ofertasDelDia.value = mockProductos
                Log.d("HomeViewModel", "Error en API, usando datos mock para ofertas del día: ${mockProductos.size} productos")
                Log.e("HomeViewModel", "Excepción al cargar ofertas del día", e)
            } finally {
                _isLoadingOfertasDia.value = false
            }
        }
    }

    /**
     * Carga las ofertas de la semana con fallback a datos mock
     */
    fun cargarOfertasDeLaSemana(context: Context) {
        viewModelScope.launch {
            _isLoadingOfertasSemana.value = true
            _errorOfertasSemana.value = null
            
            try {
                val productService = ApiClient.createProductService(context)
                val response = productService.getOfertasDeLaSemana()
                Log.d("HomeViewModel", "Ofertas de la semana - HTTP ${response.code()}")
                
                if (response.isSuccessful) {
                    val productos = response.body() ?: emptyList()
                    _ofertasDeLaSemana.value = productos
                    Log.d("HomeViewModel", "Ofertas de la semana cargadas: ${productos.size} productos")
                } else {
                    // Fallback a datos mock
                    val mockProductos = MockProducto.getListaProductos().take(3)
                    _ofertasDeLaSemana.value = mockProductos
                    Log.d("HomeViewModel", "Usando datos mock para ofertas de la semana: ${mockProductos.size} productos")
                }
            } catch (e: Exception) {
                // Fallback a datos mock en caso de error
                val mockProductos = MockProducto.getListaProductos().take(3)
                _ofertasDeLaSemana.value = mockProductos
                Log.d("HomeViewModel", "Error en API, usando datos mock para ofertas de la semana: ${mockProductos.size} productos")
                Log.e("HomeViewModel", "Excepción al cargar ofertas de la semana", e)
            } finally {
                _isLoadingOfertasSemana.value = false
            }
        }
    }

    /**
     * Carga los productos más vendidos con fallback a datos mock
     */
    fun cargarProductosMasVendidos(context: Context) {
        viewModelScope.launch {
            _isLoadingMasVendidos.value = true
            _errorMasVendidos.value = null
            
            try {
                val productService = ApiClient.createProductService(context)
                val response = productService.getProductosMasVendidos()
                Log.d("HomeViewModel", "Productos más vendidos - HTTP ${response.code()}")
                
                if (response.isSuccessful) {
                    val productos = response.body() ?: emptyList()
                    _productosMasVendidos.value = productos
                    Log.d("HomeViewModel", "Productos más vendidos cargados: ${productos.size} productos")
                } else {
                    // Fallback a datos mock
                    val mockProductos = MockProducto.getListaProductos().take(4)
                    _productosMasVendidos.value = mockProductos
                    Log.d("HomeViewModel", "Usando datos mock para productos más vendidos: ${mockProductos.size} productos")
                }
            } catch (e: Exception) {
                // Fallback a datos mock en caso de error
                val mockProductos = MockProducto.getListaProductos().take(4)
                _productosMasVendidos.value = mockProductos
                Log.d("HomeViewModel", "Error en API, usando datos mock para productos más vendidos: ${mockProductos.size} productos")
                Log.e("HomeViewModel", "Excepción al cargar productos más vendidos", e)
            } finally {
                _isLoadingMasVendidos.value = false
            }
        }
    }

    /**
     * Carga los nuevos productos con fallback a datos mock
     */
    fun cargarNuevosProductos(context: Context) {
        viewModelScope.launch {
            _isLoadingNuevos.value = true
            _errorNuevos.value = null
            
            try {
                val productService = ApiClient.createProductService(context)
                val response = productService.getNuevosProductos()
                Log.d("HomeViewModel", "Nuevos productos - HTTP ${response.code()}")
                
                if (response.isSuccessful) {
                    val productos = response.body() ?: emptyList()
                    _nuevosProductos.value = productos
                    Log.d("HomeViewModel", "Nuevos productos cargados: ${productos.size} productos")
                } else {
                    // Fallback a datos mock
                    val mockProductos = MockProducto.getListaProductos().takeLast(3)
                    _nuevosProductos.value = mockProductos
                    Log.d("HomeViewModel", "Usando datos mock para nuevos productos: ${mockProductos.size} productos")
                }
            } catch (e: Exception) {
                // Fallback a datos mock en caso de error
                val mockProductos = MockProducto.getListaProductos().takeLast(3)
                _nuevosProductos.value = mockProductos
                Log.d("HomeViewModel", "Error en API, usando datos mock para nuevos productos: ${mockProductos.size} productos")
                Log.e("HomeViewModel", "Excepción al cargar nuevos productos", e)
            } finally {
                _isLoadingNuevos.value = false
            }
        }
    }

    /**
     * Carga todos los productos para filtrado
     */
    fun cargarTodosLosProductos(context: Context) {
        viewModelScope.launch {
            try {
                val productService = ApiClient.createProductService(context)
                val response = productService.listAll()
                if (response.isSuccessful) {
                    todosLosProductos = response.body()?.content ?: emptyList()
                } else {
                    // Fallback a datos mock
                    todosLosProductos = MockProducto.getListaProductos()
                }
                aplicarFiltros()
            } catch (e: Exception) {
                // Fallback a datos mock en caso de error
                todosLosProductos = MockProducto.getListaProductos()
                aplicarFiltros()
                Log.e("HomeViewModel", "Error al cargar todos los productos", e)
            }
        }
    }

    /**
     * Activa/desactiva el filtro de descuento
     */
    fun toggleFiltroDescuento() {
        val nuevoEstado = !(_filtroDescuentoActivo.value ?: false)
        _filtroDescuentoActivo.value = nuevoEstado
        aplicarFiltros()
    }

    /**
     * Activa/desactiva el filtro de productos nuevos
     */
    fun toggleFiltroNuevo() {
        val nuevoEstado = !(_filtroNuevoActivo.value ?: false)
        _filtroNuevoActivo.value = nuevoEstado
        aplicarFiltros()
    }

    /**
     * Aplica los filtros activos a la lista de productos
     */
    private fun aplicarFiltros() {
        var productosFiltrados = todosLosProductos

        // Aplicar filtro de descuento
        if (_filtroDescuentoActivo.value == true) {
            productosFiltrados = productosFiltrados.filter { it.descuentoPorcentajeCalculado > 0 }
        }

        // Aplicar filtro de productos nuevos (menos de una semana)
        if (_filtroNuevoActivo.value == true) {
            productosFiltrados = productosFiltrados.filter { producto ->
                // Para datos mock, consideramos los últimos 3 productos como "nuevos"
                // En una implementación real, esto se basaría en la fecha de creación
                val indice = todosLosProductos.indexOf(producto)
                indice >= todosLosProductos.size - 3
            }
        }

        _productosFiltrados.value = productosFiltrados
        Log.d("HomeViewModel", "Filtros aplicados: ${productosFiltrados.size} productos")
    }

    /**
     * Limpia todos los filtros
     */
    fun limpiarFiltros() {
        _filtroDescuentoActivo.value = false
        _filtroNuevoActivo.value = false
        _productosFiltrados.value = todosLosProductos
    }

    /**
     * Carga todos los datos del home
     */
    fun cargarTodosLosDatos(context: Context) {
        cargarOfertasDelDia(context)
        cargarOfertasDeLaSemana(context)
        cargarProductosMasVendidos(context)
        cargarNuevosProductos(context)
        cargarTodosLosProductos(context) // Para filtros
    }

    /**
     * Limpia los errores
     */
    fun limpiarErrores() {
        _errorOfertasDia.value = null
        _errorOfertasSemana.value = null
        _errorMasVendidos.value = null
        _errorNuevos.value = null
    }
} 