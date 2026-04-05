package com.example.cbt.api

import com.example.cbt.model.ExamResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ExamApiService {
    @GET("exams/check-token/{token}")
    suspend fun checkExamToken(
        @Header("Authorization") authHeader: String,
        @Path("token") tokenUjian: String
    ): Response<ExamResponse>
}