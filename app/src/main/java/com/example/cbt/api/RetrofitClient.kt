package com.example.cbt.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.R.attr.level

object RetrofitClient {
    private const val BASE_URL = "https://fhfwbhujnzoecmobqumi.supabase.co/auth/login"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZoZndiaHVqbnpvZWNtb2JxdW1pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAyMzQyNDMsImV4cCI6MjA4NTgxMDI0M30.6Qv2l-e28yp2h69HhyfqXDqcvWkf0mPuaNJPO-h0aOQ"

    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            // Tambahkan interceptor untuk Header Supabase
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
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