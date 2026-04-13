package com.example.cbt.model
import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    val id: Int,
    val title: String,
    val description: String? = null,
    val start_time: String,
    val end_time: String,
    val duration_minutes: Int,
    val shuffle_questions: Boolean = true,
    val shuffle_options: Boolean = true,
    val subject_id: Int? = null,
    val created_at: String? = null
)