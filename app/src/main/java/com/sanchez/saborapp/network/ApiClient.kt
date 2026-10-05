package com.sanchez.saborapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 apunta al localhost (XAMPP) de tu PC desde el Emulador de Android
    // Si usas celular físico conectado por Wi-Fi, pon la IP de tu PC: ej. "http://192.168.1.15/saborapp_api/"
    private const val BASE_URL = "http://192.168.10.106/saborapp_api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
