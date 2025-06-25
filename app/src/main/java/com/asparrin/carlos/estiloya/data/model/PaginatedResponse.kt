// PaginatedResponse.kt
package com.asparrin.carlos.estiloya.data.model

/**
 * Wrapper genérico para respuestas paginadas de Spring Data.
 * Sólo incluye los campos que vayas a necesitar.
 */
data class PaginatedResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val size: Int
)
