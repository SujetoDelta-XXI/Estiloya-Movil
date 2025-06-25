// ProductService.kt
package com.asparrin.carlos.estiloya.api

import com.asparrin.carlos.estiloya.data.model.PaginatedResponse
import com.asparrin.carlos.estiloya.data.model.Producto
import retrofit2.Response
import retrofit2.http.GET

interface ProductService {
  /**
   * Ahora devolvemos PaginatedResponse<Producto> en lugar de List<Producto>.
   */
  @GET("productos")
  suspend fun listAll(): Response<PaginatedResponse<Producto>>
}
