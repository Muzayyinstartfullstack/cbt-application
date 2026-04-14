package com.example.cbt.api

import com.example.cbt.model.*
import retrofit2.Response
import retrofit2.http.*

interface ExamApiService {

    // ==================== AUTH ====================
    // POST /auth/login → tabel: users (cari by nisn_nip, cocokkan password_hash)
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // ==================== EXAMS ====================
    // GET /exams/{id} → tabel: ujian
    @GET("exams/{id}")
    suspend fun getExamDetail(@Path("id") examId: String): Response<ExamResponse>

    // GET /exams/check-token/{token} → tabel: ujian (cari by kolom token)
    @GET("exams/check-token/{token}")
    suspend fun checkExamToken(@Path("token") token: String): Response<ExamResponse>

    // ==================== QUESTIONS ====================
    // GET /questions/exam/{idUjian} → tabel: soal (join opsi_jawaban)
    @GET("questions/exam/{idUjian}")
    suspend fun getQuestions(@Path("idUjian") examId: String): Response<List<Question>>

    // GET /questions/{id} → tabel: soal + opsi_jawaban
    @GET("questions/{id}")
    suspend fun getQuestionDetail(@Path("id") questionId: String): Response<Question>

    // ==================== ATTEMPTS ====================
    // POST /attempts/start → tabel: attempt (buat sesi pengerjaan)
    @POST("attempts/start")
    suspend fun startAttempt(@Body request: StartAttemptRequest): Response<AttemptResponse>

    // POST /attempts/{id}/answer → tabel: student_answer (simpan jawaban)
    @POST("attempts/{id}/answer")
    suspend fun submitAnswer(
        @Path("id") attemptId: String,
        @Body answerRequest: AnswerRequest
    ): Response<StudentAnswerResponse>

    // POST /attempts/{id}/submit → tabel: attempt (ubah status jadi submitted)
    @POST("attempts/{id}/submit")
    suspend fun submitAttempt(@Path("id") attemptId: String): Response<AttemptResponse>

    // GET /attempts/{id} → tabel: attempt (detail satu attempt)
    @GET("attempts/{id}")
    suspend fun getAttemptDetail(@Path("id") attemptId: String): Response<AttemptResponse>

    // GET /attempts/my → tabel: attempt (list milik user yang login)
    @GET("attempts/my")
    suspend fun getMyAttempts(): Response<List<AttemptResponse>>

    // GET /attempts/{id}/answers → tabel: student_answer (semua jawaban per sesi)
    @GET("attempts/{id}/answers")
    suspend fun getAttemptAnswers(@Path("id") attemptId: String): Response<List<AnswerResponse>>

    // GET /attempts/exam/{idUjian} → tabel: attempt (list per ujian, untuk admin/pengawas)
    @GET("attempts/exam/{idUjian}")
    suspend fun getAttemptsByExam(@Path("idUjian") examId: String): Response<List<AttemptResponse>>
}