// src/main/java/com/asparrin/carlos/estiloya/ui/disenar/api/DisenarApi.kt

package com.asparrin.carlos.estiloya.ui.disenar.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

// — Request Models —

data class GeminiPart(
    val text: String
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiGenerationConfig(
    val responseModalities: List<String>
)

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig
)

// — Response Models —

data class GeminiResponse(
    @SerializedName("candidates")
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    val content: GeminiResponseContent
)

data class GeminiResponseContent(
    val parts: List<GeminiPartResponse>
)

data class GeminiPartResponse(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

data class GeminiInlineData(
    val mimeType: String?,
    val data: String?   // aquí cae tu Base64
)

interface DisenarApi {
    @Headers("Content-Type: application/json")
    @POST
    suspend fun generarImagen(
        @Url url: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}
