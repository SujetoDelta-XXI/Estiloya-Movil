// ProductosViewModel.kt
package com.asparrin.carlos.estiloya.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.asparrin.carlos.estiloya.api.ApiClient
import com.asparrin.carlos.estiloya.api.ProductService
import com.asparrin.carlos.estiloya.data.model.Producto
import kotlinx.coroutines.launch

class ProductosViewModel : ViewModel() {

  // LiveData que observará la UI
  private val _productos = MutableLiveData<List<Producto>>()
  val productos: LiveData<List<Producto>> = _productos

  // Instanciamos el servicio Retrofit
  private val productService: ProductService =
    ApiClient.retrofit.create(ProductService::class.java)

  /** Llama al endpoint y extrae sólo el .content */
  fun fetchProductos() {
    viewModelScope.launch {
      try {
        val response = productService.listAll()
        Log.d("VM", "HTTP ${response.code()} -> ${response.raw().request.url}")
        if (response.isSuccessful) {
          // Aquí extraemos la lista real
          val lista = response.body()?.content.orEmpty()
          _productos.value = lista
        } else {
          Log.e("VM", "Error al cargar productos: ${response.errorBody()?.string()}")
        }
      } catch (e: Exception) {
        Log.e("VM", "Excepción al llamar API", e)
      }
    }
  }
}
