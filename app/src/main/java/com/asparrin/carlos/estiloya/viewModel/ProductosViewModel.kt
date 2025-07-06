// ProductosViewModel.kt
package com.asparrin.carlos.estiloya.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asparrin.carlos.estiloya.api.ApiClient
import com.asparrin.carlos.estiloya.api.ProductService
import com.asparrin.carlos.estiloya.data.model.PaginatedResponse
import com.asparrin.carlos.estiloya.data.model.Producto
import com.asparrin.carlos.estiloya.data.mock.MockProducto
import kotlinx.coroutines.launch

class ProductosViewModel : ViewModel() {

    private val _productos = MutableLiveData<List<Producto>>()
    val productos: LiveData<List<Producto>> = _productos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> = _totalPages

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage

    fun cargarProductos(context: Context, page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val productService = ApiClient.createProductService(context)
                val response = productService.listAll()
                
                if (response.isSuccessful) {
                    val paginatedResponse = response.body()
                    if (paginatedResponse != null) {
                        _productos.value = paginatedResponse.content
                        _totalPages.value = paginatedResponse.totalPages
                        _currentPage.value = page
                        Log.d("ProductosViewModel", "Productos cargados: ${paginatedResponse.content.size}")
                    } else {
                        _productos.value = emptyList()
                        Log.w("ProductosViewModel", "Respuesta vacía del servidor")
                    }
                } else {
                    // Fallback a datos mock
                    val mockProductos = MockProducto.getListaProductos()
                    _productos.value = mockProductos
                    _totalPages.value = 1
                    _currentPage.value = 0
                    Log.d("ProductosViewModel", "Usando datos mock: ${mockProductos.size} productos")
                }
            } catch (e: Exception) {
                // Fallback a datos mock en caso de error
                val mockProductos = MockProducto.getListaProductos()
                _productos.value = mockProductos
                _totalPages.value = 1
                _currentPage.value = 0
                _error.value = "Error al cargar productos: ${e.message}"
                Log.e("ProductosViewModel", "Error al cargar productos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
