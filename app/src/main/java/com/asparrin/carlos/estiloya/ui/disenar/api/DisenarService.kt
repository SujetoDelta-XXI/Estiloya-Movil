package com.asparrin.carlos.estiloya.ui.disenar.api

import com.asparrin.carlos.estiloya.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DisenarService {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp solo con logging
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Instancia Retrofit apuntando a Stability AI
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.stability.ai/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Implementación de la API
    val api: DisenarApi = retrofit.create(DisenarApi::class.java)
}
