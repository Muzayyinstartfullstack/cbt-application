package com.example.cbt.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.cbt.api.ExamApiService
import com.example.cbt.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExamRepository(private val apiService: ExamApiService, context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CBT_PREF", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_STUDENT_ID = "student_id"
        private const val KEY_STUDENT_NAME = "student_name"
        private const val KEY_NIS = "nis"
        private const val KEY_ATTEMPT_ID = "attempt_id"
    }

    // ==================== AUTH ====================
    suspend fun login(nis: String, password: String): Result<LoginResponse> =
        withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(nisnNip = nis, password = password)
                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginData = response.body()!!
                    saveAuthData(
                        token = loginData.token,
                        studentId = loginData.id,   // id dari backend adalah String (UUID)
                        studentName = loginData.nama,
                        nis = nis
                    )
                    Result.success(loginData)
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "NISN/NIP atau password salah"
                        404 -> "User tidak ditemukan"
                        500 -> "Server error, coba lagi nanti"
                        else -> "Login gagal: ${response.code()}"
                    }
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }

    fun getJwtToken(): String = sharedPreferences.getString(KEY_JWT_TOKEN, "") ?: ""
    fun getStudentId(): String = sharedPreferences.getString(KEY_STUDENT_ID, "") ?: ""
    fun getStudentName(): String = sharedPreferences.getString(KEY_STUDENT_NAME, "") ?: ""
    fun getNis(): String = sharedPreferences.getString(KEY_NIS, "") ?: ""
    fun isLoggedIn(): Boolean = getJwtToken().isNotEmpty()
    fun getCurrentAttemptId(): String = sharedPreferences.getString(KEY_ATTEMPT_ID, "") ?: ""

    fun saveAttemptId(attemptId: String) {
        sharedPreferences.edit().putString(KEY_ATTEMPT_ID, attemptId).apply()
    }

    private fun saveAuthData(token: String, studentId: String, studentName: String, nis: String) {
        sharedPreferences.edit().apply {
            putString(KEY_JWT_TOKEN, token)
            putString(KEY_STUDENT_ID, studentId)
            putString(KEY_STUDENT_NAME, studentName)
            putString(KEY_NIS, nis)
            apply()
        }
    }

    // ==================== EXAM ====================
    // GET /exams → tabel: ujian (list semua ujian yang tersedia)
    suspend fun getAvailableExams(): Result<List<ExamResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAvailableExams()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal mengambil daftar ujian"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Validasi token ujian → GET /exams/check-token/{token} → tabel: ujian
    suspend fun checkExamToken(token: String): Result<ExamResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.checkExamToken(token)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Token JWT tidak valid"
                        404 -> "Kode ujian tidak ditemukan"
                        else -> "Error: ${response.code()}"
                    }
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Detail ujian → GET /exams/{id} → tabel: ujian
    suspend fun getExamDetail(examId: String): Result<ExamResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getExamDetail(examId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal mengambil detail ujian"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ==================== QUESTIONS ====================
    // GET /questions/exam/{idUjian} → tabel: soal + opsi_jawaban
    suspend fun getQuestions(examId: String): Result<List<Question>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getQuestions(examId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal mengambil daftar soal"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // GET /questions/{id} → tabel: soal + opsi_jawaban
    suspend fun getQuestionDetail(questionId: String): Result<Question> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getQuestionDetail(questionId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal mengambil detail soal"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ==================== ATTEMPTS ====================
    // POST /attempts/start → tabel: attempt (mulai sesi ujian)
    suspend fun startAttempt(idUjian: String, deviceInfo: String? = null): Result<AttemptResponse> =
        withContext(Dispatchers.IO) {
            try {
                val request = StartAttemptRequest(idUjian = idUjian, deviceInfo = deviceInfo)
                val response = apiService.startAttempt(request)
                if (response.isSuccessful && response.body() != null) {
                    val attempt = response.body()!!
                    saveAttemptId(attempt.id)
                    Result.success(attempt)
                } else {
                    Result.failure(Exception("Gagal memulai ujian: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // POST /attempts/{id}/answer → tabel: student_answer (simpan jawaban)
    suspend fun submitAnswer(
        attemptId: String,
        soalId: String,
        idOpsiPilihan: String? = null,
        teksJawaban: String? = null
    ): Result<StudentAnswerResponse> = withContext(Dispatchers.IO) {
        try {
            val request = AnswerRequest(
                soalId = soalId,
                idOpsiPilihan = idOpsiPilihan,
                teksJawaban = teksJawaban
            )
            val response = apiService.submitAnswer(attemptId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal menyimpan jawaban"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // POST /attempts/{id}/submit → tabel: attempt (kumpul ujian)
    suspend fun submitAttempt(attemptId: String): Result<AttemptResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.submitAttempt(attemptId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal submit ujian"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // GET /attempts/{id} → tabel: attempt
    suspend fun getAttemptDetail(attemptId: String): Result<AttemptResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAttemptDetail(attemptId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal mengambil detail attempt"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // GET /attempts/my → tabel: attempt (riwayat ujian siswa)
    suspend fun getMyAttempts(): Result<List<AttemptResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMyAttempts()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal mengambil riwayat ujian"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // GET /attempts/{id}/answers → tabel: student_answer (semua jawaban dalam satu sesi)
    suspend fun getAttemptAnswers(attemptId: String): Result<List<AnswerResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAttemptAnswers(attemptId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal mengambil jawaban"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    // ==================== BACKWARD COMPATIBILITY ====================
    suspend fun getExamHistory(): Result<ExamHistoryResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMyAttempts()
            if (response.isSuccessful && response.body() != null) {
                val attempts = response.body()!!
                val legacyHistory = attempts.map { attempt ->
                    // Calculate waktuTempuhDetik from waktuMulai and waktuKirim/waktuHabis
                    val waktuTempuh = calculateDurationSeconds(attempt.waktuMulai, attempt.waktuKirim ?: attempt.waktuHabis)
                    ExamResultResponse(
                        id = attempt.id,
                        examTitle = "Ujian #" + attempt.idUjian.takeLast(4),
                        scorePercentage = attempt.score,
                        tanggalUjian = attempt.waktuKirim ?: attempt.waktuMulai ?: "-",
                        waktuTempuhDetik = waktuTempuh,
                        status = attempt.status,
                        durasiMenit = null
                    )
                }
                Result.success(ExamHistoryResponse(legacyHistory))
            } else {
                val errBody = response.errorBody()?.string()
                Result.failure(Exception("Gagal memuat riwayat: ${response.code()} $errBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateDurationSeconds(start: String?, end: String?): Int {
        if (start == null || end == null) return 0
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val startTime = sdf.parse(start)
            val endTime = sdf.parse(end)
            ((endTime.time - startTime.time) / 1000).toInt().coerceAtLeast(0)
        } catch (_: Exception) { 0 }
    }

    suspend fun submitExam(
        examId: String,
        totalSoal: Int,
        jumlahTerjawab: Int,
        jumlahBenar: Int,
        scorePercentage: Double,
        waktuTempuhDetik: Long
    ): Result<ExamResultResponse> = withContext(Dispatchers.IO) {
        try {
            val attemptId = getCurrentAttemptId()
            val response = apiService.submitAttempt(attemptId)
            if (response.isSuccessful && response.body() != null) {
                val attempt = response.body()!!
                Result.success(ExamResultResponse(
                    id = attempt.id,
                    examTitle = "Hasil Ujian",
                    scorePercentage = attempt.score,
                    tanggalUjian = attempt.waktuKirim ?: "-",
                    waktuTempuhDetik = waktuTempuhDetik.toInt(),
                    status = attempt.status
                ))
            } else {
                Result.failure(Exception("Gagal submit"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}