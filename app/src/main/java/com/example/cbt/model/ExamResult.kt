package com.example.cbt.model
import kotlinx.serialization.Serializable

@Serializable
data class ExamResult(
    val id: Int? = null,
    val session_id: Int,
    val total_questions: Int,
    val correct_answers: Int,
    val score: Double
)
