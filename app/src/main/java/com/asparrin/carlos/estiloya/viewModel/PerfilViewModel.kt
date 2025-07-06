package com.asparrin.carlos.estiloya.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asparrin.carlos.estiloya.api.ApiClient
import com.asparrin.carlos.estiloya.data.model.Usuario
import com.asparrin.carlos.estiloya.data.model.UpdateMeRequest
import com.asparrin.carlos.estiloya.utils.SessionManager
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {
    
    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    private val _isEditing = MutableLiveData<Boolean>(false)
    val isEditing: LiveData<Boolean> = _isEditing
    
    /**
     * Cargar datos del usuario desde SessionManager si están disponibles
     */
    fun cargarDatosLocales(context: Context) {
        val sessionManager = SessionManager(context)
        if (sessionManager.estaLogueado()) {
            val usuarioLocal = sessionManager.getUser()
            if (usuarioLocal != null) {
                _usuario.value = usuarioLocal
            }
        }
    }
    
    /**
     * Cargar datos del usuario
     */
    fun cargarUsuario(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val authService = ApiClient.createAuthService(context)
                val response = authService.getProfile()
                
                if (response.isSuccessful) {
                    val profileResponse = response.body()
                    if (profileResponse?.success == true && profileResponse.user != null) {
                        _usuario.value = profileResponse.user
                        Log.d("PerfilViewModel", "Usuario cargado: ${profileResponse.user.nombre}")
                    } else {
                        _error.value = "No se pudieron cargar los datos del usuario"
                    }
                } else {
                    _error.value = "Error al cargar perfil: ${response.code()}"
                    Log.e("PerfilViewModel", "Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("PerfilViewModel", "Error al cargar usuario", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Actualizar datos del usuario
     */
    fun actualizarUsuario(context: Context, nombre: String, apellidos: String, telefono: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            
            try {
                val authService = ApiClient.createAuthService(context)
                val request = UpdateMeRequest(nombre, apellidos, telefono)
                val response = authService.updateMe(request)
                
                if (response.isSuccessful) {
                    val usuarioActualizado = response.body()
                    if (usuarioActualizado != null) {
                        _usuario.value = usuarioActualizado
                        _successMessage.value = "Perfil actualizado exitosamente"
                        _isEditing.value = false
                        
                        // Actualizar datos en SessionManager
                        val sessionManager = SessionManager(context)
                        sessionManager.saveUser(usuarioActualizado)
                        
                        Log.d("PerfilViewModel", "Usuario actualizado: ${usuarioActualizado.nombre}")
                    } else {
                        _error.value = "No se pudo actualizar el perfil"
                    }
                } else {
                    _error.value = "Error al actualizar perfil: ${response.code()}"
                    Log.e("PerfilViewModel", "Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("PerfilViewModel", "Error al actualizar usuario", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Cambiar modo de edición
     */
    fun toggleEdicion() {
        _isEditing.value = !(_isEditing.value ?: false)
    }
    
    /**
     * Cancelar edición
     */
    fun cancelarEdicion() {
        _isEditing.value = false
    }
    
    /**
     * Limpiar mensajes
     */
    fun limpiarMensajes() {
        _error.value = null
        _successMessage.value = null
    }
} 