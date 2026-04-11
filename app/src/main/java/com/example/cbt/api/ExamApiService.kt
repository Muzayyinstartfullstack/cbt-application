package com.example.cbt.api

import com.example.cbt.model.*
import retrofit2.Response
import retrofit2.http.*

interface ExamApiService {

    // ==================== AUTHENTICATION ====================
    @POST("/auth/v1/token?grant_type=password")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    // ==================== EXAM ====================
    @GET("exams/check-token/{token}")
    suspend fun checkExamToken(
        @Header("Authorization") authHeader: String,
        @Path("token") tokenUjian: String
    ): Response<ExamResponse>

    @GET("exams/{exam_id}")
    suspend fun getExamDetail(
        @Header("Authorization") authHeader: String,
        @Path("exam_id") examId: String
    ): Response<ExamResponse>

    // ==================== QUESTIONS ====================
    @GET("exams/{exam_id}/questions")
    suspend fun getQuestions(
        @Header("Authorization") authHeader: String,
        @Path("exam_id") examId: String
    ): Response<QuestionListResponse>

    @GET("exams/{exam_id}/questions/{question_id}")
    suspend fun getQuestionDetail(
        @Header("Authorization") authHeader: String,
        @Path("exam_id") examId: String,
        @Path("question_id") questionId: String
    ): Response<Question>

    // ==================== ANSWERS ====================
    @POST("exams/{exam_id}/answers")
    suspend fun submitAnswer(
        @Header("Authorization") authHeader: String,
        @Path("exam_id") examId: String,
        @Body answerRequest: AnswerRequest
    ): Response<AnswerResponse>

    @GET("exams/{exam_id}/answers")
    suspend fun getExamAnswers(
        @Header("Authorization") authHeader: String,
        @Path("exam_id") examId: String
    ): Response<List<AnswerResponse>>

    @PUT("exams/{exam_id}/answers/{question_id}")
    suspend fun updateAnswer(
        @Header("Authorization") authHeader: String,
        @Path("exam_id") examId: String,
        @Path("question_id") questionId: String,
        @Body answerRequest: AnswerRequest
    ): Response<AnswerResponse>

    // ==================== EXAM RESULTS ====================
    @POST("exams/{exam_id}/submit")
    suspend fun submitExam(
        @Header("Authorization") authHeader: String,
        @Path("exam_id") examId: String,
        @Body resultRequest: ExamResultRequest
    ): Response<ExamResultResponse>

    @GET("exam-results")
    suspend fun getExamHistory(
        @Header("Authorization") authHeader: String,
        @Query("student_id") studentId: String
    ): Response<ExamHistoryResponse>

    @GET("exam-results/{result_id}")
    suspend fun getExamResult(
        @Header("Authorization") authHeader: String,
        @Path("result_id") resultId: String
    ): Response<ExamResultResponse>

    @GET("exam-results/subject/{subject}")
    suspend fun getExamHistoryBySubject(
        @Header("Authorization") authHeader: String,
        @Query("student_id") studentId: String,
        @Path("subject") subject: String
    ): Response<ExamHistoryResponse>
}