package com.asparrin.carlos.estiloya.ui.disenar.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import com.google.gson.annotations.SerializedName

data class TextPrompt(
    val text: String,
    val weight: Float = 1.0f
)

data class StabilityImageRequest(
    @SerializedName("text_prompts")
    val textPrompts: List<TextPrompt>,
    val cfg_scale: Float = 7.0f,
    val height: Int = 1024,
    val width: Int = 1024,
    val samples: Int = 1
    // puedes añadir `val steps: Int = 50` si quieres controlar el número de pasos
)

data class StabilityImageResponse(
    val artifacts: List<Artifact>
) {
    data class Artifact(
        val base64: String,
        @SerializedName("finishReason")
        val finishReason: String,
        val seed: Long
    )
}

interface DisenarApi {
    @POST("v1alpha/generation/stable-diffusion-v1-6/text-to-image")
    suspend fun generarImagen(
        @Header("Authorization") apiKey: String = "Bearer sk-T3h6J10LqZSvSo5NdPafwShBYjAR7VKrZTFpNy8vHnazjRIh",
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: StabilityImageRequest
    ): StabilityImageResponse
}
