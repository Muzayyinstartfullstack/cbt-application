package com.example.cbt.model

// ==================== AUTH ====================
// POST /auth/login → body: { nisnNip, password }
data class LoginRequest(val nisnNip: String, val password: String)

// Response dari backend: { token, id, nama, role }
data class LoginResponse(
    val token: String,
    val id: String,       // backend kirim id sebagai String (UUID)
    val nama: String,
    val role: String
)

// ==================== EXAM ====================
// GET /exams/{id} → tabel: ujian
// GET /exams/check-token/{token} → tabel: ujian (cari by token)
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

// ==================== QUESTIONS ====================
// GET /questions/exam/{idUjian} → tabel: soal + opsi_jawaban
data class AnswerOptionResponse(
    val id: String,
    val idSoal: String,
    val opsiText: String,
    val opsiOrder: Int,
    val isCorrect: Boolean
)

data class Question(
    val id: String,
    val idUjian: String,
    val tipeSoal: String,
    val teksSoal: String,
    val image: String?,
    val poin: Int,
    val opsiJawaban: List<AnswerOptionResponse> = emptyList()
) {
    // Helper untuk backward compatibility dengan kode UI lama
    val pertanyaan: String get() = teksSoal
    val nomor: Int get() = 0 // Akan diisi manual di Activity atau dihitung
    
    val opsiA: String get() = opsiJawaban.getOrNull(0)?.opsiText ?: ""
    val opsiB: String get() = opsiJawaban.getOrNull(1)?.opsiText ?: ""
    val opsiC: String get() = opsiJawaban.getOrNull(2)?.opsiText ?: ""
    val opsiD: String get() = opsiJawaban.getOrNull(3)?.opsiText ?: ""
    val opsiE: String get() = opsiJawaban.getOrNull(4)?.opsiText ?: ""
}

data class QuestionListResponse(val data: List<Question>)

// ==================== ATTEMPTS ====================
// POST /attempts/start → tabel: attempt
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

// POST /attempts/{id}/answer → tabel: student_answer
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

// POST /attempts/{id}/submit
// GET /attempts/{id}
// GET /attempts/my → tabel: attempt (list milik user)
// GET /attempts/exam/{idUjian} → tabel: attempt (per ujian)

// GET /attempts/{id}/answers → tabel: student_answer
data class AnswerResponse(
    val id: String,
    val attempId: String,
    val soalId: String,
    val idOpsiPilihan: String?,
    val teksJawaban: String?
)

// ==================== LEGACY (bisa dihapus jika tidak dipakai) ====================
// Model-model di bawah ini tidak cocok dengan backend saat ini,
// dipertahankan sementara agar tidak break aktivitas lain

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