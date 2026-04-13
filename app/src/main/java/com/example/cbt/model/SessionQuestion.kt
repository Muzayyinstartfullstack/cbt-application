package com.example.cbt.model
import kotlinx.serialization.Serializable

@Serializable
data class SessionQuestion(
    val id: Int? = null,
    val session_id: Int,
    val question_id: Int,
    val question_order: Int
)

