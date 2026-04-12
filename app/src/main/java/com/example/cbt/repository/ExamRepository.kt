package com.example.cbt.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.cbt.api.ExamApiService
import com.example.cbt.api.TokenRequest
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
                val request = LoginRequest(nisnNip = nis, password = password)
                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginData = response.body()!!
                    saveAuthData(
                        token = loginData.token,
                        studentId = loginData.id.toString(),
                        studentName = loginData.nama,
                        nis = ""
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

    fun getJwtToken(): String = sharedPreferences.getString(KEY_JWT_TOKEN, "") ?: ""
    fun getStudentId(): String = sharedPreferences.getString(KEY_STUDENT_ID, "") ?: ""
    fun getStudentName(): String = sharedPreferences.getString(KEY_STUDENT_NAME, "") ?: ""
    fun getNis(): String = sharedPreferences.getString(KEY_NIS, "") ?: ""
    fun isLoggedIn(): Boolean = getJwtToken().isNotEmpty()

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
                val response = apiService.checkExamToken(TokenRequest(token))
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
    suspend fun getQuestions(examId: String): Result<List<Question>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getQuestions(examId)
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
                val response = apiService.getQuestionDetail(examId, questionId)
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
            val request = AnswerRequest(examId, questionId, nomorSoal, jawaban, isBookmarked)
            val response = apiService.submitAnswer(examId, request)
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
                val response = apiService.getExamAnswers(examId)
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
            val request = AnswerRequest(examId, questionId, 0, jawaban, isBookmarked)
            val response = apiService.updateAnswer(examId, questionId, request)
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
            val studentId = getStudentId()
            val status = if (scorePercentage >= 70) "PASSED" else "FAILED"
            val request = ExamResultRequest(
                examId, studentId, totalSoal, jumlahTerjawab,
                jumlahBenar, scorePercentage, waktuTempuhDetik, status
            )
            val response = apiService.submitExam(examId, request)
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
                val response = apiService.getExamHistory(getStudentId())
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
                val response = apiService.getExamHistoryBySubject(getStudentId(), subject)
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