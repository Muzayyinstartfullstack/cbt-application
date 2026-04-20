package com.example.cbt.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Ganti dengan IP PC lo (cek via ipconfig) atau URL ngrok
    private const val BASE_URL = "http://10.230.13.73:8080"

    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val token = TokenManager.getToken()
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .apply {
                        if (token != null) addHeader("Authorization", "Bearer $token")
                    }
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ExamApiService sudah tidak digunakan - kita pakai Supabase langsung
}