package com.asparrin.carlos.estiloya.utils

import android.content.Context
import com.asparrin.carlos.estiloya.data.model.Usuario

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("estiloya_prefs", Context.MODE_PRIVATE)

    fun guardarUsuario(usuario: Usuario) {
        with(prefs.edit()) {
            putString("nombre", usuario.nombre)
            putString("correo", usuario.correo)
            putBoolean("logueado", true)
            apply()
        }
    }

    fun obtenerNombre(): String? = prefs.getString("nombre", null)

    fun estaLogueado(): Boolean = prefs.getBoolean("logueado", false)

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }

    fun saveDarkModePref(isDark: Boolean) {
        prefs.edit().putBoolean("pref_dark_mode", isDark).apply()
    }
    fun isDarkModePref(): Boolean = prefs.getBoolean("pref_dark_mode", false)
}
