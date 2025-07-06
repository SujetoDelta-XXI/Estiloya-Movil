package com.asparrin.carlos.estiloya.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asparrin.carlos.estiloya.api.AuthRepository
import com.asparrin.carlos.estiloya.data.model.*
import com.asparrin.carlos.estiloya.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(private val context: Context) : ViewModel() {
    
    companion object {
        private const val TAG = "AuthViewModel"
    }
    
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
                    if (response.success && response.jwt != null) {
                        if (response.requiere2FA) {
                            // Guardar token temporal para 2FA
                            sessionManager.saveTemporaryToken(response.jwt)
                            // Guardar información del usuario si está disponible
                            if (response.user != null) {
                                sessionManager.saveUser(response.user)
                            }
                            _authState.value = AuthState.REQUIRES_2FA
                        } else {
                            // Login completo
                            sessionManager.saveAuthToken(response.jwt)
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
                    Log.d(TAG, "Login response recibida - success: ${response.success}, requiere2FA: ${response.requiere2FA}")
                    Log.d(TAG, "Login response - jwt: ${response.jwt?.take(50)}...")
                    
                    if (response.success && response.jwt != null) {
                        if (response.requiere2FA) {
                            // Guardar token temporal para 2FA
                            Log.d(TAG, "Guardando token temporal para 2FA: ${response.jwt.take(50)}...")
                            sessionManager.saveTemporaryToken(response.jwt)
                            
                            // Verificar que se guardó correctamente
                            val savedToken = sessionManager.getTemporaryToken()
                            Log.d(TAG, "Token temporal guardado y verificado: ${savedToken?.take(50)}...")
                            
                            // Guardar información del usuario si está disponible
                            if (response.user != null) {
                                sessionManager.saveUser(response.user)
                            }
                            _authState.value = AuthState.REQUIRES_2FA
                        } else {
                            // Login completo
                            Log.d(TAG, "Guardando token de autenticación: ${response.jwt.take(50)}...")
                            sessionManager.saveAuthToken(response.jwt)
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
                    if (response.success && response.jwt != null) {
                        if (response.requiere2FA) {
                            sessionManager.saveTemporaryToken(response.jwt)
                            _authState.value = AuthState.REQUIRES_2FA
                        } else {
                            sessionManager.saveAuthToken(response.jwt)
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
    fun verify2FA(correoPrincipal: String, code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            try {
                Log.d(TAG, "=== VERIFY 2FA DEBUG ===")
                Log.d(TAG, "Correo principal: $correoPrincipal")
                Log.d(TAG, "Código: $code")
                Log.d(TAG, "Token temporal disponible: ${sessionManager.getTemporaryToken() != null}")
                Log.d(TAG, "Token temporal: ${sessionManager.getTemporaryToken()?.take(50)}...")
                Log.d(TAG, "Token auth disponible: ${sessionManager.getAuthToken() != null}")
                Log.d(TAG, "=== END VERIFY 2FA DEBUG ===")
                
                Log.d(TAG, "Intentando verificar código 2FA para correo principal: $correoPrincipal")
                authRepository.verify2FA(correoPrincipal, code)
                    .onSuccess { response ->
                        Log.d(TAG, "✅ Verificación 2FA exitosa")
                        Log.d(TAG, "Response: success=${response.success}, token=${response.token?.take(50)}...")
                        _twoFactorResult.value = response
                        
                        // Si hay token, considerar la verificación exitosa
                        if (response.token != null) {
                            Log.d(TAG, "Guardando token final de autenticación")
                            sessionManager.saveAuthToken(response.token)
                            if (response.user != null) {
                                sessionManager.saveUser(response.user)
                            }
                            // Limpiar token temporal ya que ahora tenemos el token final
                            sessionManager.clearTemporaryToken()

                            _authState.value = AuthState.AUTHENTICATED
                        } else {
                            Log.e(TAG, "❌ Verificación 2FA falló: no hay token en la respuesta")
                            _errorMessage.value = "Error en la verificación 2FA: no se recibió token"
                        }
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "❌ Error en verificación 2FA", exception)
                        _errorMessage.value = exception.message ?: "Error en la verificación 2FA"
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception en verify2FA", e)
                _errorMessage.value = "Error en la verificación 2FA"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun registerAlternativeEmail(correo: String, alternativeEmail: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            try {
                Log.d(TAG, "Intentando registrar email alternativo: $alternativeEmail")
                authRepository.registerAlternativeEmail(correo, alternativeEmail)
                    .onSuccess { response ->
                        Log.d(TAG, "✅ Email alternativo registrado exitosamente en backend")
                        Log.d(TAG, "Response: success=${response.isSuccess}, message=${response.message}")
                        _registerEmailResult.value = response
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "❌ Error al registrar email alternativo en backend", exception)
                        _errorMessage.value = exception.message ?: "Error al registrar email alternativo"
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception en registerAlternativeEmail", e)
                _errorMessage.value = "Error al registrar email alternativo"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun send2FACode() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            try {
                Log.d(TAG, "Intentando enviar código 2FA")
                // El backend obtiene la información del usuario del token JWT
                authRepository.send2FACode() // El backend usa el token para obtener info del usuario
                    .onSuccess { response ->
                        Log.d(TAG, "✅ Código 2FA enviado exitosamente")
                        _sendCodeResult.value = response
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "❌ Error al enviar código 2FA", exception)
                        val errorMessage = exception.message ?: "Error desconocido"
                        
                        // Si hay error del backend, mostrar el mensaje
                        Log.e(TAG, "❌ Error del backend al enviar código 2FA: $errorMessage")
                        _errorMessage.value = errorMessage
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception en send2FACode", e)
                _errorMessage.value = "Error al enviar código 2FA"
            } finally {
                _isLoading.value = false
            }
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
    fun checkAuthState() {
        val authToken = sessionManager.getAuthToken()
        val user = sessionManager.getUser()
        
        if (authToken != null && user != null) {
            // Usuario completamente autenticado
            _authState.value = AuthState.AUTHENTICATED
        } else {
            // Usuario no autenticado (incluye procesos incompletos)
            _authState.value = AuthState.NOT_AUTHENTICATED
        }
    }
    
    fun logout() {
        // Limpiar toda la sesión
        sessionManager.clearSession()
        _authState.value = AuthState.NOT_AUTHENTICATED
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

