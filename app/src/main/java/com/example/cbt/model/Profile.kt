package com.example.cbt.model
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: Int,
    val nisnip: String,
    val full_name: String,
    val role: String,
    val class_name: String? = null,
    val birth_date: String,
    val created_at: String? = null
)
