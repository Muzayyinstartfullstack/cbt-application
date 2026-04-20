package com.example.cbt.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Profile(
    val id: String = "",
    val email: String = "",
    val nis: String = "",
    @SerialName("full_name") 
    val fullName: String = "",
    @SerialName("tanggal_lahir") 
    val tanggalLahir: String = "",
    @SerialName("password_hash")
    val passwordHash: String? = null, // Untuk custom NIS auth
    val role: String = "siswa"
)