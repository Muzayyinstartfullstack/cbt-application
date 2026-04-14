package com.example.cbt.model
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// ENUM role — sesuai: CREATE TYPE user_role AS ENUM (...)
enum class UserRole {
    @SerialName("student") STUDENT,
    @SerialName("teacher") TEACHER,
    @SerialName("admin")   ADMIN
}

// Tabel: profiles
@Serializable
data class Profile(
    val id: Int = 0,

    // nisnip VARCHAR(20) UNIQUE NOT NULL
    val nisnip: String = "",

    // full_name VARCHAR(100) NOT NULL
    @SerialName("full_name")
    val fullName: String = "",

    // role user_role NOT NULL DEFAULT 'student'
    val role: String = "student",

    // class_name VARCHAR(50)
    @SerialName("class_name")
    val className: String? = null,

    // birth_date DATE NOT NULL
    @SerialName("birth_date")
    val birthDate: String = "",

    // password TIDAK diambil — jangan expose ke client
    // created_at TIMESTAMP DEFAULT NOW()
    @SerialName("created_at")
    val createdAt: String = ""
)