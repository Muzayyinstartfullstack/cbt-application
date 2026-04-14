package com.example.cbt.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Tabel: subjects
@Serializable
data class Subject(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // name VARCHAR(100) UNIQUE NOT NULL
    val name: String = "",

    // created_at TIMESTAMP DEFAULT NOW()
    @SerialName("created_at")
    val createdAt: String = ""
)