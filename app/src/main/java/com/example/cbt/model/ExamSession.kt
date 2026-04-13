package com.example.cbt.model
import kotlinx.serialization.Serializable

@Serializable
data class ExamSession(
    val id: Int? = null,
    val profile_id: Int,
    val exam_id: Int,
    val start_time: String? = null,
    val end_time: String? = null,
    val status: String = "not_started",
    val score: Double? = null
)

