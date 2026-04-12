package com.example.cbt.api

import com.example.cbt.model.ExamResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Data class untuk body request (Samakan dengan yang di Ktor)
data class TokenRequest(val token: String)

interface ExamApiService {
    // Ubah dari @GET jadi @POST sesuai kodingan Routing.kt tadi
    @POST("exams/check-token")
    suspend fun checkExamToken(
        @Body request: TokenRequest
    ): Response<ExamResponse>
}