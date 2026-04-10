package com.example.cbt.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.R.attr.level

object RetrofitClient {
    // Ganti dengan IP address server Anda
    // 10.0.2.2 = localhost dari emulator (jika pakai emulator Android default)
    // 192.168.1.64 = IP lokal machine (untuk device fisik atau emulator yang di-bridge)
    private const val BASE_URL = "http://192.168.1.64:8080/"

    // HTTP Client dengan logging dan timeout
    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Ubah ke NONE untuk production
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val instance: ExamApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .build()
            .create(ExamApiService::class.java)
    }
}