package com.asparrin.carlos.estiloya.api

import com.asparrin.carlos.estiloya.data.model.CustomDesign
import retrofit2.Response
import retrofit2.http.*

interface CustomDesignService {
    
    @POST("diseno/guardar-diseno")
    suspend fun guardarDiseno(@Body customDesign: CustomDesign): Response<CustomDesign>
    
    @GET("diseno/mis-disenos")
    suspend fun obtenerMisDisenos(): Response<List<CustomDesign>>
    
    @GET("diseno/todos")
    suspend fun obtenerTodosLosDisenos(): Response<List<CustomDesign>>
    
    @GET("diseno/{id}")
    suspend fun obtenerDisenoPorId(@Path("id") id: Long): Response<CustomDesign>
    
    @PUT("diseno/{id}/estado")
    suspend fun actualizarEstado(
        @Path("id") id: Long,
        @Body estado: Map<String, String>
    ): Response<CustomDesign>
} 