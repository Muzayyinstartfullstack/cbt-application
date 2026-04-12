package com.example.cbt.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val nisnNip: String, val password: String)
data class LoginResponse(val token: String, val id: Int, val nama: String, val role: String)

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}