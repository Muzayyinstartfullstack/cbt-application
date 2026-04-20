package com.example.cbt.data.model



import com.google.gson.annotations.SerializedName

import java.util.Date



data class Subject(

    val id: Int,

    val name: String,

    val description: String?,

    @SerializedName("image_url") val imageUrl: String?,

    @SerializedName("created_at") val createdAt: String

)



data class Topic(

    val id: Int,

    @SerializedName("subject_id") val subjectId: Int,

    val name: String,

    @SerializedName("created_at") val createdAt: String

)



data class AnswerOption(

    val id: Int,

    @SerializedName("question_id") val questionId: Int,

    @SerializedName("option_text") val optionText: String,

    @SerializedName("is_correct") val isCorrect: Boolean,

    @SerializedName("created_at") val createdAt: String

)



data class Question(

    val id: Int,

    @SerializedName("exam_id") val examId: Int,

    @SerializedName("topic_id") val topicId: Int?,

    @SerializedName("question_text") val questionText: String,

    @SerializedName("image_url") val imageUrl: String?,

    val poin: Int,

    @SerializedName("created_at") val createdAt: String,

    @SerializedName("answer_options") val answerOptions: List<AnswerOption> = emptyList()

)



data class Exam(

    val id: Int,

    val title: String,

    val description: String?,

    @SerializedName("subject_id") val subjectId: Int,

    @SerializedName("start_time") val startTime: String,

    @SerializedName("end_time") val endTime: String,

    @SerializedName("duration_minutes") val durationMinutes: Int,

    @SerializedName("shuffle_questions") val shuffleQuestions: Boolean,

    @SerializedName("shuffle_options") val shuffleOptions: Boolean,

    @SerializedName("image_url") val imageUrl: String?,

    @SerializedName("access_token") val accessToken: String,

    @SerializedName("created_at") val createdAt: String

)



data class ExamWithDetails(

    val id: Int,

    val title: String,

    val description: String?,

    @SerializedName("subject_id") val subjectId: Int,

    @SerializedName("start_time") val startTime: String,

    @SerializedName("end_time") val endTime: String,

    @SerializedName("duration_minutes") val durationMinutes: Int,

    @SerializedName("shuffle_questions") val shuffleQuestions: Boolean,

    @SerializedName("shuffle_options") val shuffleOptions: Boolean,

    @SerializedName("image_url") val imageUrl: String?,

    @SerializedName("access_token") val accessToken: String,

    @SerializedName("created_at") val createdAt: String,

    @SerializedName("total_questions") val totalQuestions: Int,

    val status: String

)



data class ExamSession(

    val id: Int,

    @SerializedName("exam_id") val examId: Int,

    @SerializedName("user_id") val userId: String,

    @SerializedName("started_at") val startedAt: String,

    @SerializedName("completed_at") val completedAt: String?,

    val score: Int?,

    @SerializedName("created_at") val createdAt: String

)



data class ExamSessionWithStatus(

    val exam: Exam? = null,

    val id: Int,

    @SerializedName("exam_id") val examId: Int,

    @SerializedName("user_id") val userId: String,

    @SerializedName("started_at") val startedAt: String,

    @SerializedName("completed_at") val completedAt: String?,

    val score: Int?,

    @SerializedName("created_at") val createdAt: String,

    val status: String

)



data class UserStatistics(

    val completedExams: Int,

    val averageScore: Double,

    val remedialCount: Int

)



data class UserAnswer(

    val id: Int,

    @SerializedName("session_id") val sessionId: Int,

    @SerializedName("question_id") val questionId: Int,

    @SerializedName("answer_option_id") val answerOptionId: Int,

    @SerializedName("created_at") val createdAt: String

)



// Request/Response khusus

data class JoinExamRequest(

    @SerializedName("access_token") val accessToken: String

)



data class SubmitAnswerRequest(

    @SerializedName("session_id") val sessionId: Int,

    @SerializedName("question_id") val questionId: Int,

    @SerializedName("answer_option_id") val answerOptionId: Int

)



data class CompleteExamRequest(

    @SerializedName("session_id") val sessionId: Int,

    val score: Int

)