package com.example.cbt.model
import kotlinx.serialization.Serializable

@Serializable
data class SessionAnswer(
    val id: Int? = null,
    val session_question_id: Int,
    val chosen_option_id: Int,
    val is_correct: Boolean? = null
)