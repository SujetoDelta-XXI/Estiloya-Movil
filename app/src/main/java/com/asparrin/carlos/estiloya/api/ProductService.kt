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
  @GET("usuario/productos")
  suspend fun listAll(): Response<PaginatedResponse<Producto>>

  /**
   * Ofertas del día - devuelve array directo
   */
  @GET("usuario/productos/ofertas-dia")
  suspend fun getOfertasDelDia(): Response<List<Producto>>

  /**
   * Ofertas de la semana - devuelve array directo
   */
  @GET("usuario/productos/ofertas-semana")
  suspend fun getOfertasDeLaSemana(): Response<List<Producto>>

  /**
   * Productos más vendidos - devuelve array directo
   */
  @GET("usuario/productos/mas-vendidos")
  suspend fun getProductosMasVendidos(): Response<List<Producto>>

  /**
   * Nuevos productos - devuelve array directo
   */
  @GET("usuario/productos/nuevos")
  suspend fun getNuevosProductos(): Response<List<Producto>>

  // Endpoints alternativos que devuelven PaginatedResponse (por si acaso)
  @GET("usuario/productos/ofertas-dia-paginated")
  suspend fun getOfertasDelDiaPaginated(): Response<PaginatedResponse<Producto>>

  @GET("usuario/productos/ofertas-semana-paginated")
  suspend fun getOfertasDeLaSemanaPaginated(): Response<PaginatedResponse<Producto>>

  @GET("usuario/productos/mas-vendidos-paginated")
  suspend fun getProductosMasVendidosPaginated(): Response<PaginatedResponse<Producto>>

  @GET("usuario/productos/nuevos-paginated")
  suspend fun getNuevosProductosPaginated(): Response<PaginatedResponse<Producto>>
}
