package com.example.cbt.model
import kotlinx.serialization.Serializable

@Serializable
data class QuestionOption(
    val id: Int,
    val question_id: Int,
    val option_label: String,
    val option_text: String,
    val is_correct: Boolean = false
)