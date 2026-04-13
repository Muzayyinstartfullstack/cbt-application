package com.example.cbt.model
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Int,
    val question_text: String,
    val image_url: String? = null,
    val subject_id: Int? = null,
    val topic_id: Int? = null
)