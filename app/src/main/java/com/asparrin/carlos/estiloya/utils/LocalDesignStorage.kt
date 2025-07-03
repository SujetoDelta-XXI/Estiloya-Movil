package com.asparrin.carlos.estiloya.utils

import android.content.Context
import android.util.Log
import com.asparrin.carlos.estiloya.data.model.CustomDesign
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalDesignStorage {
    private const val PREFS_NAME = "estiloya_disenos"
    private const val TAG = "LocalDesignStorage"
    
    private fun getKeyForUser(userId: String) = "disenos_usuario_$userId"

    fun guardarDiseno(context: Context, userId: String, diseno: CustomDesign) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val lista = obtenerDisenos(context, userId).toMutableList()
        lista.add(diseno)
        val json = gson.toJson(lista)
        prefs.edit().putString(getKeyForUser(userId), json).apply()
        
        Log.d(TAG, "Diseño guardado para usuario $userId. Total diseños: ${lista.size}")
        Log.d(TAG, "Diseño guardado: ID=${diseno.id}, Descripción=${diseno.descripcion}")
    }

    fun obtenerDisenos(context: Context, userId: String): List<CustomDesign> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(getKeyForUser(userId), null)
        val disenos: List<CustomDesign> = if (json != null) {
            try {
                val type = object : TypeToken<List<CustomDesign>>() {}.type
                Gson().fromJson<List<CustomDesign>>(json, type)
            } catch (e: Exception) {
                Log.e(TAG, "Error al parsear diseños: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
        
        Log.d(TAG, "Diseños cargados para usuario $userId: ${disenos.size} diseños")
        return disenos
    }

    fun limpiarDisenos(context: Context, userId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(getKeyForUser(userId)).apply()
        Log.d(TAG, "Diseños limpiados para usuario $userId")
    }
} 