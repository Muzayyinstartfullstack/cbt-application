package com.example.cbt.model

data class ExamResponse(
    val id: String,
    val judul: String,
    val durasi: Int,
    val totalSoal: Int,
    val status: String
)