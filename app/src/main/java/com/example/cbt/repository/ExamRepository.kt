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
    }

    // ==================== AUTH ====================
    suspend fun login(nis: String, password: String): Result<LoginResponse> =
        withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(nis = nis, password = password)
                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginData = response.body()!!
                    // Simpan token dan data user
                    saveAuthData(
                        token = loginData.accessToken,
                        studentId = loginData.studentId,
                        studentName = loginData.studentName,
                        nis = loginData.nis
                    )
                    Result.success(loginData)
                } else {
                    Result.failure(Exception("Login gagal: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }

    fun getJwtToken(): String {
        return sharedPreferences.getString(KEY_JWT_TOKEN, "") ?: ""
    }

    fun getStudentId(): String {
        return sharedPreferences.getString(KEY_STUDENT_ID, "") ?: ""
    }

    fun getStudentName(): String {
        return sharedPreferences.getString(KEY_STUDENT_NAME, "") ?: ""
    }

    fun getNis(): String {
        return sharedPreferences.getString(KEY_NIS, "") ?: ""
    }

    fun isLoggedIn(): Boolean {
        return getJwtToken().isNotEmpty()
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
    suspend fun checkExamToken(token: String): Result<ExamResponse> =
        withContext(Dispatchers.IO) {
            try {
                val jwtToken = getJwtToken()
                val response = apiService.checkExamToken("Bearer $jwtToken", token)

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

    suspend fun getExamDetail(examId: String): Result<ExamResponse> =
        withContext(Dispatchers.IO) {
            try {
                val jwtToken = getJwtToken()
                val response = apiService.getExamDetail("Bearer $jwtToken", examId)

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
    suspend fun getQuestions(examId: String): Result<List<Question>> =
        withContext(Dispatchers.IO) {
            try {
                val jwtToken = getJwtToken()
                val response = apiService.getQuestions("Bearer $jwtToken", examId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.data)
                } else {
                    Result.failure(Exception("Gagal mengambil daftar soal"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getQuestionDetail(examId: String, questionId: String): Result<Question> =
        withContext(Dispatchers.IO) {
            try {
                val jwtToken = getJwtToken()
                val response = apiService.getQuestionDetail("Bearer $jwtToken", examId, questionId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal mengambil detail soal"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ==================== ANSWERS ====================
    suspend fun submitAnswer(
        examId: String,
        questionId: String,
        nomorSoal: Int,
        jawaban: String,
        isBookmarked: Boolean = false
    ): Result<AnswerResponse> = withContext(Dispatchers.IO) {
        try {
            val jwtToken = getJwtToken()
            val request = AnswerRequest(
                examId = examId,
                questionId = questionId,
                nomorSoal = nomorSoal,
                jawaban = jawaban,
                isBookmarked = isBookmarked
            )
            val response = apiService.submitAnswer("Bearer $jwtToken", examId, request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal menyimpan jawaban"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExamAnswers(examId: String): Result<List<AnswerResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val jwtToken = getJwtToken()
                val response = apiService.getExamAnswers("Bearer $jwtToken", examId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Gagal mengambil jawaban"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun updateAnswer(
        examId: String,
        questionId: String,
        jawaban: String,
        isBookmarked: Boolean = false
    ): Result<AnswerResponse> = withContext(Dispatchers.IO) {
        try {
            val jwtToken = getJwtToken()
            val request = AnswerRequest(
                examId = examId,
                questionId = questionId,
                nomorSoal = 0, // Tidak digunakan saat update
                jawaban = jawaban,
                isBookmarked = isBookmarked
            )
            val response = apiService.updateAnswer("Bearer $jwtToken", examId, questionId, request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal mengupdate jawaban"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== EXAM RESULTS ====================
    suspend fun submitExam(
        examId: String,
        totalSoal: Int,
        jumlahTerjawab: Int,
        jumlahBenar: Int,
        scorePercentage: Double,
        waktuTempuhDetik: Long
    ): Result<ExamResultResponse> = withContext(Dispatchers.IO) {
        try {
            val jwtToken = getJwtToken()
            val studentId = getStudentId()
            val status = if (scorePercentage >= 70) "PASSED" else "FAILED"

            val request = ExamResultRequest(
                examId = examId,
                studentId = studentId,
                totalSoal = totalSoal,
                jumlahTerjawab = jumlahTerjawab,
                jumlahBenar = jumlahBenar,
                scorePercentage = scorePercentage,
                waktuTempuhDetik = waktuTempuhDetik,
                status = status
            )

            val response = apiService.submitExam("Bearer $jwtToken", examId, request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal submit ujian"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExamHistory(): Result<List<ExamResultResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val jwtToken = getJwtToken()
                val studentId = getStudentId()
                val response = apiService.getExamHistory("Bearer $jwtToken", studentId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.data)
                } else {
                    Result.failure(Exception("Gagal mengambil riwayat ujian"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getExamHistoryBySubject(subject: String): Result<List<ExamResultResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val jwtToken = getJwtToken()
                val studentId = getStudentId()
                val response = apiService.getExamHistoryBySubject("Bearer $jwtToken", studentId, subject)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.data)
                } else {
                    Result.failure(Exception("Gagal mengambil riwayat ujian"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}