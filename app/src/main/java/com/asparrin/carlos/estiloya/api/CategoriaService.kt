package com.asparrin.carlos.estiloya.api

import com.asparrin.carlos.estiloya.data.model.Categoria
import retrofit2.Response
import retrofit2.http.GET

interface CategoriaService {
    @GET("usuario/categorias")
    suspend fun getCategorias(): Response<List<Categoria>>
} 