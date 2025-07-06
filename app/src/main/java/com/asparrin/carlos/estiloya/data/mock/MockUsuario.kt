package com.asparrin.carlos.estiloya.data.mock

import com.asparrin.carlos.estiloya.data.model.Usuario

object MockUsuario {

    private val listaUsuarios = mutableListOf(
        Usuario(
            id = 1,
            nombre = "Carlos",
            apellidos= "Asparrin",
            correo = "carlos@tecsup.edu.pe",
            password = "123456",
            telefono = "987654321",
            rol = "USER"
        ),
        Usuario(
            id = 2,
            nombre = "Lucía",
            apellidos = "Quispe",
            correo = "lucia@tecsup.edu.pe",
            password = "lucia2025",
            telefono = "912345678",
            rol = "USER"
        )
    )

    fun validarLogin(correo: String, clave: String): Usuario? {
        return listaUsuarios.find { it.correo == correo && it.password == clave && it.rol == "USER" }
    }

    fun registrar(usuario: Usuario): Boolean {
        if (listaUsuarios.any { it.correo == usuario.correo }) return false
        val nuevoId = (listaUsuarios.maxOfOrNull { it.id } ?: 0) + 1
        listaUsuarios.add(usuario.copy(id = nuevoId))
        return true
    }

    fun getTodos(): List<Usuario> = listaUsuarios
}
