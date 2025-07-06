package com.asparrin.carlos.estiloya.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

  // Celular - Wifi - Casa:
  // private const val BASE_URL = "http://192.168.18.2:8080/api/"

  // Para emulador Android
  private const val BASE_URL = "http://192.168.18.2:8080/api/"

  // Para dispositivo físico (ajustar IP según tu red)
  // private const val BASE_URL = "http://192.168.18.2:8080/api/"

  // Cliente HTTP con autenticación para endpoints protegidos
  private fun createAuthenticatedClient(context: Context): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }
    
    return OkHttpClient.Builder()
      .addInterceptor(AuthInterceptor(context))
      .addInterceptor(loggingInterceptor)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()
  }
  
  // Cliente HTTP sin autenticación para endpoints públicos
  private fun createPublicClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }
    
    return OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()
  }

  private fun createRetrofit(context: Context): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(createAuthenticatedClient(context))
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  // Servicio de autenticación (usa cliente con interceptor que maneja endpoints públicos)
  fun createAuthService(context: Context): AuthService {
    val retrofit = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(createAuthenticatedClient(context))
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    
    return retrofit.create(AuthService::class.java)
  }

  // Servicios que requieren autenticación
  fun createProductService(context: Context): ProductService {
    val retrofit = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(createAuthenticatedClient(context))
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    
    return retrofit.create(ProductService::class.java)
  }

  fun createCategoriaService(context: Context): CategoriaService {
    val retrofit = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(createAuthenticatedClient(context))
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    
    return retrofit.create(CategoriaService::class.java)
  }

  fun createCarritoService(context: Context): CarritoService {
    return createRetrofit(context).create(CarritoService::class.java)
  }

  fun createCustomDesignService(context: Context): CustomDesignService {
    val retrofit = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(createAuthenticatedClient(context))
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    
    return retrofit.create(CustomDesignService::class.java)
  }
} 