package com.example.cbt.model

data class LoginRequest(val nisnNip: String, val password: String)

data class LoginResponse(
    val token: String,
    val id: Int,
    val nama: String,
    val role: String
)

data class QuestionListResponse(val data: List<Question>)

data class Question(
    val id: String,
    val nomor: Int,
    val pertanyaan: String,
    val opsiA: String,
    val opsiB: String,
    val opsiC: String,
    val opsiD: String,
    val opsiE: String,
    val jawaban: String? = null
)

data class AnswerRequest(
    val examId: String,
    val questionId: String,
    val nomorSoal: Int,
    val jawaban: String,
    val isBookmarked: Boolean
)

data class AnswerResponse(
    val success: Boolean,
    val message: String? = null
)

data class ExamResultRequest(
    val examId: String,
    val studentId: String,
    val totalSoal: Int,
    val jumlahTerjawab: Int,
    val jumlahBenar: Int,
    val scorePercentage: Double,
    val waktuTempuhDetik: Long,
    val status: String
)

data class ExamResultResponse(
    val id: String,
    val examTitle: String,
    val scorePercentage: Double,
    val tanggalUjian: String,
    val waktuTempuhDetik: Int,
    val status: String,
    val passingGrade: Double? = null,
    val durasiMenit: Int? = null
)

data class Exam(
    val id: String,
    val subjectId: String,
    val subjectName: String,
    val title: String,
    val description: String?,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val passingGrade: Double? = null
)

data class ExamListResponse(val data: List<Exam>)
data class ExamHistoryResponse(val data: List<ExamResultResponse>)

data class ExamResponse(
    val id: String,
    val judul: String,
    val durasi: Int,
    val totalSoal: Int,
    val status: String
)