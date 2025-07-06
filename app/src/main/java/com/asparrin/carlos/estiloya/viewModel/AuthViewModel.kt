package com.asparrin.carlos.estiloya.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asparrin.carlos.estiloya.api.AuthRepository
import com.asparrin.carlos.estiloya.data.model.*
import com.asparrin.carlos.estiloya.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(private val context: Context) : ViewModel() {
    
    private val authRepository = AuthRepository(context)
    private val sessionManager = SessionManager(context)
    
    // LiveData para estados de autenticación
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    // Estados específicos para diferentes operaciones
    private val _loginResult = MutableLiveData<AuthResponse>()
    val loginResult: LiveData<AuthResponse> = _loginResult
    
    private val _registerResult = MutableLiveData<AuthResponse>()
    val registerResult: LiveData<AuthResponse> = _registerResult
    
    private val _twoFactorResult = MutableLiveData<TwoFactorResponse>()
    val twoFactorResult: LiveData<TwoFactorResponse> = _twoFactorResult
    
    private val _profileResult = MutableLiveData<ProfileResponse>()
    val profileResult: LiveData<ProfileResponse> = _profileResult
    
    private val _sendCodeResult = MutableLiveData<SendCodeResponse>()
    val sendCodeResult: LiveData<SendCodeResponse> = _sendCodeResult
    
    private val _registerEmailResult = MutableLiveData<RegisterEmailResponse>()
    val registerEmailResult: LiveData<RegisterEmailResponse> = _registerEmailResult
    
    // Métodos de autenticación básica
    fun register(nombre: String, apellidos: String, correo: String, contraseña: String, telefono: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.register(nombre, apellidos, correo, contraseña, telefono)
                .onSuccess { response ->
                    _registerResult.value = response
                    if (response.success && response.token != null) {
                        // Guardar token temporal si requiere 2FA
                        if (response.requires2FA) {
                            sessionManager.saveTemporaryToken(response.token)
                        } else {
                            // Login completo
                            sessionManager.saveAuthToken(response.token)
                            sessionManager.saveUser(response.user)
                            _authState.value = AuthState.AUTHENTICATED
                        }
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error en el registro"
                }
            
            _isLoading.value = false
        }
    }
    
    fun login(correo: String, contraseña: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.login(correo, contraseña)
                .onSuccess { response ->
                    _loginResult.value = response
                    if (response.success && response.token != null) {
                        if (response.requires2FA) {
                            // Guardar token temporal para 2FA
                            sessionManager.saveTemporaryToken(response.token)
                            _authState.value = AuthState.REQUIRES_2FA
                        } else {
                            // Login completo
                            sessionManager.saveAuthToken(response.token)
                            sessionManager.saveUser(response.user)
                            _authState.value = AuthState.AUTHENTICATED
                        }
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error en el login"
                }
            
            _isLoading.value = false
        }
    }
    
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.loginWithGoogle(idToken)
                .onSuccess { response ->
                    if (response.success && response.token != null) {
                        if (response.requires2FA) {
                            sessionManager.saveTemporaryToken(response.token)
                            _authState.value = AuthState.REQUIRES_2FA
                        } else {
                            sessionManager.saveAuthToken(response.token)
                            sessionManager.saveUser(response.user)
                            _authState.value = AuthState.AUTHENTICATED
                        }
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error en login con Google"
                }
            
            _isLoading.value = false
        }
    }
    
    // Métodos de 2FA
    fun verify2FA(correo: String, code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.verify2FA(correo, code)
                .onSuccess { response ->
                    _twoFactorResult.value = response
                    if (response.success && response.token != null) {
                        // Login completo después de 2FA
                        sessionManager.saveAuthToken(response.token)
                        sessionManager.saveUser(response.user)
                        _authState.value = AuthState.AUTHENTICATED
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error en la verificación 2FA"
                }
            
            _isLoading.value = false
        }
    }
    
    fun registerAlternativeEmail(correo: String, alternativeEmail: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.registerAlternativeEmail(correo, alternativeEmail)
                .onSuccess { response ->
                    _registerEmailResult.value = response
                    if (response.success) {
                        // Email alternativo registrado exitosamente
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error al registrar email alternativo"
                }
            
            _isLoading.value = false
        }
    }
    
    fun send2FACode(correo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.send2FACode(correo)
                .onSuccess { response ->
                    _sendCodeResult.value = response
                    if (response.success) {
                        // Código enviado exitosamente
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error al enviar código 2FA"
                }
            
            _isLoading.value = false
        }
    }
    
    // Métodos de recuperación de contraseña
    fun forgotPassword(correo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.forgotPassword(correo)
                .onSuccess { response ->
                    if (response.success) {
                        // Email de recuperación enviado
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error al solicitar recuperación"
                }
            
            _isLoading.value = false
        }
    }
    
    fun resetPassword(correo: String, token: String, contraseña: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.resetPassword(correo, token, contraseña)
                .onSuccess { response ->
                    if (response.success) {
                        // Contraseña restablecida exitosamente
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error al restablecer contraseña"
                }
            
            _isLoading.value = false
        }
    }
    
    // Método de prueba de conexión
    fun testConnection() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.testConnection()
                .onSuccess { response ->
                    if (response.success) {
                        // Conexión exitosa
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error en prueba de conexión"
                }
            
            _isLoading.value = false
        }
    }
    
    // Métodos de perfil de usuario
    fun getProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.getProfile()
                .onSuccess { response ->
                    _profileResult.value = response
                    if (response.success && response.user != null) {
                        sessionManager.saveUser(response.user)
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error al obtener perfil"
                }
            
            _isLoading.value = false
        }
    }
    
    fun updateProfile(nombre: String, apellidos: String, telefono: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            authRepository.updateProfile(nombre, apellidos, telefono)
                .onSuccess { response ->
                    _profileResult.value = response
                    if (response.success && response.user != null) {
                        sessionManager.saveUser(response.user)
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Error al actualizar perfil"
                }
            
            _isLoading.value = false
        }
    }
    
    // Métodos de gestión de sesión
    fun logout() {
        sessionManager.clearSession()
        _authState.value = AuthState.NOT_AUTHENTICATED
    }
    
    fun checkAuthState() {
        val token = sessionManager.getAuthToken()
        val user = sessionManager.getUser()
        
        if (token != null && user != null) {
            _authState.value = AuthState.AUTHENTICATED
        } else {
            _authState.value = AuthState.NOT_AUTHENTICATED
        }
    }
    
    fun clearError() {
        _errorMessage.value = ""
    }
}

// Estados de autenticación
enum class AuthState {
    NOT_AUTHENTICATED,
    AUTHENTICATED,
    REQUIRES_2FA,
    LOADING
}

