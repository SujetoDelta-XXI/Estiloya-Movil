package com.asparrin.carlos.estiloya.utils

import android.content.Context
import android.content.SharedPreferences
import com.asparrin.carlos.estiloya.data.model.Usuario
import com.google.gson.Gson

class SessionManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    companion object {
        private const val PREF_NAME = "EstiloyaSession"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_TEMPORARY_TOKEN = "temporary_token"
        private const val KEY_USER = "user"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    // Métodos para token de autenticación principal
    fun saveAuthToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }
    
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }
    
    fun clearAuthToken() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_AUTH_TOKEN)
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
    }
    
    // Métodos para token temporal (2FA)
    fun saveTemporaryToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_TEMPORARY_TOKEN, token)
        editor.apply()
    }
    
    fun getTemporaryToken(): String? {
        return sharedPreferences.getString(KEY_TEMPORARY_TOKEN, null)
    }
    
    fun clearTemporaryToken() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_TEMPORARY_TOKEN)
        editor.apply()
    }
    
    // Métodos para usuario
    fun saveUser(user: Usuario?) {
        val editor = sharedPreferences.edit()
        if (user != null) {
            val userJson = gson.toJson(user)
            editor.putString(KEY_USER, userJson)
        } else {
            editor.remove(KEY_USER)
        }
        editor.apply()
    }
    
    fun getUser(): Usuario? {
        val userJson = sharedPreferences.getString(KEY_USER, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, Usuario::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    
    fun clearUser() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USER)
        editor.apply()
    }
    
    // Métodos de estado de sesión
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && 
               getAuthToken() != null
    }
    
    fun isIn2FAFlow(): Boolean {
        return getTemporaryToken() != null
    }
    
    // Método para limpiar toda la sesión
    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
    
    // Método para obtener el token activo (principal o temporal)
    fun getActiveToken(): String? {
        return getAuthToken() ?: getTemporaryToken()
    }
    
    // Método para verificar si hay algún token disponible
    fun hasAnyToken(): Boolean {
        return getAuthToken() != null || getTemporaryToken() != null
    }
    
    // Métodos para compatibilidad con baseActivity
    fun estaLogueado(): Boolean {
        return isLoggedIn()
    }
    
    fun cerrarSesion() {
        clearSession()
    }
    
    fun isDarkModePref(): Boolean {
        return sharedPreferences.getBoolean("dark_mode", false)
    }
    
    fun saveDarkModePref(isDark: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("dark_mode", isDark)
        editor.apply()
    }
    
    // Métodos para obtener datos del usuario (compatibilidad)
    fun obtenerNombre(): String? {
        return getUser()?.nombre
    }
    
    fun obtenerCorreo(): String? {
        return getUser()?.correo
    }
}
