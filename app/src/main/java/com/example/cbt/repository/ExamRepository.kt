package com.example.cbt.data.repository



import android.content.Context

import android.content.SharedPreferences

import com.example.cbt.database.SupabaseClient

import com.example.cbt.data.model.*

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext

import java.time.Instant



class ExamRepository(private val context: Context) {



    private val supabase = SupabaseClient.client

    private val prefs: SharedPreferences = context.getSharedPreferences("cbt_prefs", Context.MODE_PRIVATE)



    // ==================== AUTH ====================

    suspend fun loginWithNis(nis: String, password: String): Result<Profile> {
        return try {
            val email = "$nis@sekolah.local"
            
            // Coba login dulu
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            } catch (e: Exception) {
                // Jika login gagal karena user belum terdaftar, coba register
                if (e.message?.contains("invalid_credentials") == true || 
                    e.message?.contains("user_not_found") == true) {
                    try {
                        supabase.auth.signUpWith(Email) {
                            this.email = email
                            this.password = password
                        }
                        // Setelah register, login lagi
                        supabase.auth.signInWith(Email) {
                            this.email = email
                            this.password = password
                        }
                    } catch (signupError: Exception) {
                        // Jika register juga gagal (misal: user sudah ada), lempar error asli
                        throw e
                    }
                } else {
                    throw e
                }
            }

            val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: ""
            prefs.edit().putString("user_id", userId).apply()

            // Ambil profile
            val profiles = supabase.postgrest["profiles"]
                .select {
                    filter { eq("nis", nis) }
                }
                .decodeList<Profile>()

            val profile = profiles.firstOrNull() ?: run {
                // Auto-create profile kalau tidak ditemukan
                val newProfile = Profile(
                    id = userId,
                    email = email,
                    nis = nis,
                    fullName = "Siswa $nis",
                    tanggalLahir = password, // Password format YYYYMMDD
                    role = "siswa"
                )
                // Insert ke database
                supabase.postgrest["profiles"].insert(newProfile)
                newProfile
            }

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * CUSTOM LOGIN - Pure NIS tanpa email
     * Login menggunakan NIS dan password (tanggal_lahir) langsung dari tabel profiles
     */
    suspend fun loginWithNisCustom(nis: String, password: String): Result<Profile> {
        return try {
            // 1. Cari profile berdasarkan NIS
            val profiles = supabase.postgrest["profiles"]
                .select {
                    filter { eq("nis", nis) }
                }
                .decodeList<Profile>()

            val profile = profiles.firstOrNull()
                ?: return Result.failure(Exception("NIS tidak ditemukan"))

            // 2. Verifikasi password (tanggal_lahir)
            // Password format: YYYYMMDD
            val storedPassword = profile.tanggalLahir
            if (storedPassword != password) {
                return Result.failure(Exception("Password salah"))
            }

            // 3. Buat custom session (tanpa Supabase Auth)
            val userId = profile.id
            prefs.edit()
                .putString("user_id", userId)
                .putString("nis", nis)
                .putBoolean("custom_auth", true)
                .apply()

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Register custom dengan NIS
     */
    suspend fun registerWithNisCustom(nis: String, fullName: String, tanggalLahir: String): Result<Profile> {
        return try {
            // Cek apakah NIS sudah terdaftar
            val existing = supabase.postgrest["profiles"]
                .select {
                    filter { eq("nis", nis) }
                }
                .decodeList<Profile>()

            if (existing.isNotEmpty()) {
                return Result.failure(Exception("NIS sudah terdaftar"))
            }

            // Generate ID baru
            val newId = java.util.UUID.randomUUID().toString()
            val email = "$nis@sekolah.local" // Tetap simpan email untuk kompatibilitas

            val newProfile = Profile(
                id = newId,
                email = email,
                nis = nis,
                fullName = fullName,
                tanggalLahir = tanggalLahir, // Ini juga jadi password
                passwordHash = null,
                role = "siswa"
            )

            supabase.postgrest["profiles"].insert(newProfile)

            Result.success(newProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        // Clear Supabase Auth session
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {
            // Ignore error jika tidak ada session Supabase
        }
        // Clear custom session
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        // Cek Supabase Auth session
        val supabaseSession = supabase.auth.currentSessionOrNull() != null
        // Cek custom auth session
        val customSession = prefs.getBoolean("custom_auth", false) && 
                           prefs.getString("user_id", null) != null
        return supabaseSession || customSession
    }

    suspend fun getCurrentUserId(): String? {
        return supabase.auth.currentSessionOrNull()?.user?.id ?: prefs.getString("user_id", null)

    }



    // ==================== PROFILE ====================

    suspend fun getProfile(userId: String): Result<Profile> {

        return try {

            val profiles = supabase.postgrest["profiles"]
                .select {
                    filter { eq("id", userId) }
                }
                .decodeList<Profile>()

            val profile = profiles.firstOrNull() ?: throw Exception("Profile not found")

            Result.success(profile)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    // ==================== EXAMS ====================

    suspend fun getActiveExams(): Result<List<ExamWithDetails>> {

        return try {

            val exams = supabase.postgrest["exams_with_details"]
                .select {
                    filter { eq("status", "active") }
                }
                .decodeList<ExamWithDetails>()

            Result.success(exams)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    suspend fun getUpcomingExams(): Result<List<ExamWithDetails>> {

        return try {

            val exams = supabase.postgrest["exams_with_details"]
                .select {
                    filter { eq("status", "ongoing") }
                }
                .decodeList<ExamWithDetails>()

            Result.success(exams)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    suspend fun getExamByToken(token: String): Result<ExamWithDetails> {

        return try {

            val exams = supabase.postgrest["exams_with_details"]
                .select {
                    filter { eq("access_token", token.uppercase()) }
                }
                .decodeList<ExamWithDetails>()

            val exam = exams.firstOrNull() ?: throw Exception("Token invalid")

            Result.success(exam)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    suspend fun getUserStatistics(userId: String): Result<UserStatistics> {

        return try {

            // Ambil semua sesi yang sudah selesai (completed_at != null)

            val sessions = supabase.postgrest["exam_sessions"]
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<ExamSession>()
                .filter { it.completedAt != null }

            val completed = sessions.size
            val avgScore = if (completed > 0) sessions.map { it.score ?: 0 }.average() else 0.0
            val remedial = sessions.count { (it.score ?: 0) < 70 }

            Result.success(UserStatistics(completed, avgScore, remedial))

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    // ==================== SESSIONS ====================

    suspend fun startNewExamSession(examId: Int, userId: String): Result<ExamSession> {

        return try {

            val session = supabase.postgrest["exam_sessions"]
                .insert(
                    mapOf(
                        "exam_id" to examId,
                        "user_id" to userId,
                        "started_at" to Instant.now().toString()
                    )
                )
                .decodeSingle<ExamSession>()

            Result.success(session)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    suspend fun submitAnswer(sessionId: Int, questionId: Int, answerOptionId: Int): Result<Unit> {

        return try {

            supabase.postgrest["user_answers"]
                .upsert(
                    mapOf(
                        "session_id" to sessionId,
                        "question_id" to questionId,
                        "answer_option_id" to answerOptionId,
                        "created_at" to Instant.now().toString()
                    )
                )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    suspend fun completeExam(sessionId: Int, score: Int): Result<Unit> {

        return try {

            supabase.postgrest["exam_sessions"]
                .update(
                    mapOf(
                        "completed_at" to Instant.now().toString(),
                        "score" to score
                    )
                )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    // ==================== QUESTIONS ====================

    suspend fun getQuestionsWithOptions(examId: Int): Result<Pair<List<Question>, Map<Int, List<AnswerOption>>>> {

        return try {

            val questions = supabase.postgrest["questions"]
                .select {
                    filter { eq("exam_id", examId) }
                }
                .decodeList<Question>()

            val optionsMap = mutableMapOf<Int, List<AnswerOption>>()

            for (q in questions) {

                val options = supabase.postgrest["answer_options"]
                    .select {
                        filter { eq("question_id", q.id) }
                    }
                    .decodeList<AnswerOption>()

                optionsMap[q.id] = options

            }

            Result.success(Pair(questions, optionsMap))

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    // ==================== HISTORY ====================

    suspend fun getExamHistory(userId: String): Result<List<ExamSessionWithStatus>> {

        return try {

            val sessions = supabase.postgrest["exam_sessions_with_status"]
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<ExamSessionWithStatus>()
                .filter { it.completedAt != null }

            Result.success(sessions)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }



    suspend fun getExamResult(sessionId: String): Result<ExamResultDetail> {
        return try {
            // Ambil session
            val session = supabase.postgrest["exam_sessions"]
                .select {
                    filter { eq("id", sessionId) }
                }
                .decodeSingle<ExamSession>()

            // Ambil exam detail terpisah
            val exam = supabase.postgrest["exams"]
                .select {
                    filter { eq("id", session.examId) }
                }
                .decodeSingleOrNull<ExamWithDetails>()

            val total = exam?.totalQuestions ?: 0
            val score = session.score ?: 0
            val correct = if (total > 0) ((score / 100.0) * total).toInt() else 0

            Result.success(
                ExamResultDetail(
                    examSession = session,
                    totalQuestions = total,
                    correctAnswers = correct,
                    score = score.toDouble(),
                    completedAt = session.completedAt ?: "",
                    createdAt = session.createdAt,
                    durationMinutes = exam?.durationMinutes
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



// Data class tambahan (masukkan ke ApiModel.kt atau di sini)

data class ExamResultDetail(

    val examSession: ExamSession,

    val totalQuestions: Int,

    val correctAnswers: Int,

    val score: Double,

    val completedAt: String,

    val createdAt: String,

    val durationMinutes: Int?

)



data class UserStatistics(

    val completedExams: Int,

    val averageScore: Double,

    val remedialCount: Int

)

}