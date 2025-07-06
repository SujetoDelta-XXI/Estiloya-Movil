package com.asparrin.carlos.estiloya.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.asparrin.carlos.estiloya.data.model.CustomDesign
import com.asparrin.carlos.estiloya.utils.LocalDesignStorage
import com.asparrin.carlos.estiloya.utils.SessionManager
import kotlinx.coroutines.launch

class CustomDesignViewModel(application: Application) : AndroidViewModel(application) {

    private val _disenos = MutableLiveData<List<CustomDesign>>()
    val disenos: LiveData<List<CustomDesign>> = _disenos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val context = getApplication<Application>().applicationContext
    private val sessionManager = SessionManager(context)

    private fun obtenerEmailUsuario(): String {
        return sessionManager.getUser()?.correo ?: ""
    }

    fun cargarMisDisenos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val userKey = obtenerEmailUsuario()
            Log.d("CustomDesignViewModel", "Cargando diseños para usuario: $userKey")
            if (userKey.isNotEmpty()) {
                val disenos = LocalDesignStorage.obtenerDisenos(context, userKey)
                _disenos.value = disenos
                Log.d("CustomDesignViewModel", "Diseños cargados: ${disenos.size}")
            } else {
                _disenos.value = emptyList()
                _error.value = "No hay usuario logueado."
                Log.e("CustomDesignViewModel", "No hay usuario logueado")
            }
            _isLoading.value = false
        }
    }

    fun guardarDiseno(descripcion: String, urlImagen: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val userKey = obtenerEmailUsuario()
            Log.d("CustomDesignViewModel", "Guardando diseño para usuario: $userKey")
            if (userKey.isNotEmpty()) {
                val nuevoDiseno = CustomDesign(
                    id = System.currentTimeMillis(), // ID único local
                    urlImagen = urlImagen,
                    descripcion = descripcion,
                    fechaCreacion = java.time.LocalDate.now().toString(),
                    estado = "pendiente"
                )
                LocalDesignStorage.guardarDiseno(context, userKey, nuevoDiseno)
                Log.d("CustomDesignViewModel", "Diseño guardado exitosamente")
                cargarMisDisenos()
            } else {
                _error.value = "No hay usuario logueado."
                Log.e("CustomDesignViewModel", "No hay usuario logueado para guardar diseño")
            }
            _isLoading.value = false
        }
    }

    fun limpiarError() {
        _error.value = null
    }
} 