package com.example.cbt.api

import com.example.cbt.model.*
import retrofit2.Response
import retrofit2.http.*

data class TokenRequest(val token: String)

interface ExamApiService {

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("exams/check-token")
    suspend fun checkExamToken(@Body request: TokenRequest): Response<ExamResponse>

    @GET("exams/{exam_id}")
    suspend fun getExamDetail(@Path("exam_id") examId: String): Response<ExamResponse>

    @GET("exams/{exam_id}/questions")
    suspend fun getQuestions(@Path("exam_id") examId: String): Response<QuestionListResponse>

    @GET("exams/{exam_id}/questions/{question_id}")
    suspend fun getQuestionDetail(
        @Path("exam_id") examId: String,
        @Path("question_id") questionId: String
    ): Response<Question>

    @POST("exams/{exam_id}/answers")
    suspend fun submitAnswer(
        @Path("exam_id") examId: String,
        @Body answerRequest: AnswerRequest
    ): Response<AnswerResponse>

    @GET("exams/{exam_id}/answers")
    suspend fun getExamAnswers(@Path("exam_id") examId: String): Response<List<AnswerResponse>>

    @PUT("exams/{exam_id}/answers/{question_id}")
    suspend fun updateAnswer(
        @Path("exam_id") examId: String,
        @Path("question_id") questionId: String,
        @Body answerRequest: AnswerRequest
    ): Response<AnswerResponse>

    @POST("exams/{exam_id}/submit")
    suspend fun submitExam(
        @Path("exam_id") examId: String,
        @Body resultRequest: ExamResultRequest
    ): Response<ExamResultResponse>

    @GET("exam-results")
    suspend fun getExamHistory(@Query("student_id") studentId: String): Response<ExamHistoryResponse>

    @GET("exam-results/{result_id}")
    suspend fun getExamResult(@Path("result_id") resultId: String): Response<ExamResultResponse>

    @GET("exam-results/subject/{subject}")
    suspend fun getExamHistoryBySubject(
        @Query("student_id") studentId: String,
        @Path("subject") subject: String
    ): Response<ExamHistoryResponse>
}