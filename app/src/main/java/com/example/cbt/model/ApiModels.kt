package com.example.cbt.model

// File ini hanya untuk model REST API lama (Retrofit).
// Model Supabase sudah ada di:
//   Profile.kt, Subject.kt, TopicGroup.kt, Exam.kt, Question.kt

// ==================== AUTH ====================

data class LoginRequest(
    val nisnNip: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val id: String,
    val nama: String,
    val role: String
)

// ==================== EXAM (REST lama) ====================

data class ExamResponse(
    val id: String,
    val judul: String,
    val idMapel: String?,
    val durasi: Int,
    val totalSoal: Int,
    val startTime: String?,
    val endTime: String?,
    val status: String,
    val soalRandom: Boolean,
    val jawabanRandom: Boolean,
    val createdBy: String?
)

// ==================== QUESTIONS (REST lama) ====================

data class AnswerOptionResponse(
    val id: String,
    val idSoal: String,
    val opsiText: String,
    val opsiOrder: Int,
    val isCorrect: Boolean
)

// Diganti nama: Questions → QuestionApi (hindari bentrok dengan Question.kt)
data class QuestionApi(
    val id: String,
    val idUjian: String,
    val tipeSoal: String,
    val teksSoal: String,
    val image: String?,
    val poin: Int,
    val opsiJawaban: List<AnswerOptionResponse> = emptyList()
) {
    val pertanyaan: String get() = teksSoal
    val nomor: Int get() = 0
    val opsiA: String get() = opsiJawaban.getOrNull(0)?.opsiText ?: ""
    val opsiB: String get() = opsiJawaban.getOrNull(1)?.opsiText ?: ""
    val opsiC: String get() = opsiJawaban.getOrNull(2)?.opsiText ?: ""
    val opsiD: String get() = opsiJawaban.getOrNull(3)?.opsiText ?: ""
    val opsiE: String get() = opsiJawaban.getOrNull(4)?.opsiText ?: ""
}

data class QuestionListResponse(val data: List<QuestionApi>)

// ==================== ATTEMPTS ====================

data class StartAttemptRequest(
    val idUjian: String,
    val deviceInfo: String? = null
)

data class AttemptResponse(
    val id: String,
    val idUjian: String,
    val idUser: String,
    val status: String,
    val score: Double,
    val waktuMulai: String?,
    val waktuHabis: String?,
    val waktuKirim: String?,
    val deviceInfo: String?,
    val ipAddress: String?,
    val sedangBerlangsung: Boolean,
    val dikirim: Boolean
)

data class AnswerRequest(
    val soalId: String,
    val idOpsiPilihan: String? = null,
    val teksJawaban: String? = null
)

data class StudentAnswerResponse(
    val id: String,
    val attempId: String,
    val soalId: String,
    val idOpsiPilihan: String?,
    val teksJawaban: String?
)

data class AnswerResponse(
    val id: String,
    val attempId: String,
    val soalId: String,
    val idOpsiPilihan: String?,
    val teksJawaban: String?
)

// ==================== LEGACY ====================

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

// Diganti nama: Exams → ExamLegacy (hindari bentrok dengan Exam.kt)
data class ExamLegacy(
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

data class ExamListResponse(val data: List<ExamLegacy>)
data class ExamHistoryResponse(val data: List<ExamResultResponse>)