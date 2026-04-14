package com.example.cbt.repository

import com.example.cbt.database.SupabaseClient
import com.example.cbt.model.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ExamRepository {

    private val db   = SupabaseClient.client
    private val auth = SupabaseClient.client.auth

    // ─── AUTH ─────────────────────────────────────────────────────────────────

    suspend fun login(nisnip: String, password: String): Result<Profile> {
        return try {
            val virtualEmail = "${nisnip}@cbt.internal"

            auth.signInWith(io.github.jan.supabase.auth.providers.builtin.Email) {
                this.email    = virtualEmail
                this.password = password
            }

            val profile = db.from("profiles")
                .select(Columns.ALL) {
                    filter { eq("nisnip", nisnip) }
                }
                .decodeSingle<Profile>()

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            // abaikan error saat logout
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUserOrNull() != null
    }

    suspend fun getProfile(userId: String): Result<Profile> {
        return try {
            val email = auth.currentUserOrNull()?.email ?: ""
            val nisnip = email.substringBefore("@")

            val profile = db.from("profiles")
                .select(Columns.ALL) {
                    filter { eq("nisnip", nisnip) }
                }
                .decodeSingle<Profile>()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── EXAM ─────────────────────────────────────────────────────────────────

    suspend fun getExamDetail(examId: String): Result<Exam> {
        return try {
            val exam = db.from("exams")
                .select(Columns.ALL) {
                    filter { eq("id", examId) }
                }
                .decodeSingle<Exam>()
            Result.success(exam)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAvailableExams(profileIdOrUserId: String): Result<List<Exam>> {
        return try {
            // Coba ambil dari exam_participants. 
            // Kadang profile_id di tabel ini merujuk ke auth.uid (String) atau profiles.id (Int)
            // Di sini kita berasumsi relasi sudah benar di Supabase.
            val participants = db.from("exam_participants")
                .select(Columns.raw("exam_id, exams(*)")) {
                    filter {
                        // Gunakan or jika tidak yakin mana yang dipakai (Int vs UUID string)
                        // Tapi biasanya salah satu. Kita coba eq dulu.
                        eq("profile_id", profileIdOrUserId)
                    }
                }
                .decodeList<ExamParticipant>()

            val exams = participants.mapNotNull { it.exams }
            Result.success(exams)
        } catch (e: Exception) {
            // Fallback: Jika join gagal (biasanya karena RLS), coba ambil exams saja yang active
            // Ini cadangan jika tabel participants bermasalah permission-nya
            try {
                val now = java.time.Instant.now().toString()
                val activeExams = db.from("exams")
                    .select(Columns.ALL) {
                        filter {
                            lte("start_time", now)
                            gte("end_time", now)
                        }
                    }
                    .decodeList<Exam>()
                Result.success(activeExams)
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUpcomingExams(): Result<List<Exam>> {
        return try {
            val now = java.time.Instant.now().toString()
            val exams = db.from("exams")
                .select(Columns.ALL) {
                    filter {
                        gt("start_time", now)
                    }
                    order("start_time", Order.ASCENDING)
                }
                .decodeList<Exam>()
            Result.success(exams)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── SESSION ──────────────────────────────────────────────────────────────

    suspend fun getOrCreateSession(
        examId: String,
        profileId: String
    ): Result<ExamSession> {
        return try {
            // Cek apakah sesi sudah ada
            val existing = db.from("exam_sessions")
                .select(Columns.ALL) {
                    filter {
                        eq("exam_id",    examId)
                        eq("profile_id", profileId)
                    }
                }
                .decodeList<ExamSession>()

            if (existing.isNotEmpty()) {
                return Result.success(existing.first())
            }

            // Buat sesi baru
            val session = db.from("exam_sessions")
                .insert(
                    buildJsonObject {
                        put("exam_id",    examId)
                        put("profile_id", profileId)
                        put("status",     "ongoing")
                        put("start_time", java.time.Instant.now().toString())
                    }
                ) {
                    select(Columns.ALL)
                }
                .decodeSingle<ExamSession>()

            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessionById(sessionId: String): Result<ExamSession> {
        return try {
            val session = db.from("exam_sessions")
                .select(Columns.ALL) {
                    filter { eq("id", sessionId) }
                }
                .decodeSingle<ExamSession>()
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── QUESTIONS ────────────────────────────────────────────────────────────

    suspend fun getQuestions(examId: String): Result<List<Question>> {
        return try {
            val exam = getExamDetail(examId).getOrThrow()

            val questions = db.from("questions")
                .select(
                    Columns.raw(
                        "id, question_text, image_url, topic_id, subject_id, " +
                                "question_options(id, option_label, option_text, is_correct)"
                    )
                ) {
                    filter { eq("subject_id", exam.subjectId) }
                }
                .decodeList<Question>()

            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessionQuestions(sessionId: String): Result<List<SessionQuestion>> {
        return try {
            val sessionQuestions = db.from("session_questions")
                .select(
                    Columns.raw(
                        "id, question_order, question_id, " +
                                "questions(" +
                                "  id, question_text, image_url, topic_id, subject_id, " +
                                "  question_options(id, option_label, option_text, is_correct)" +
                                ")"
                    )
                ) {
                    filter { eq("session_id", sessionId) }
                    order("question_order", Order.ASCENDING)
                }
                .decodeList<SessionQuestion>()
            Result.success(sessionQuestions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertSessionQuestions(
        sessionId: String,
        questions: List<Question>
    ): Result<Unit> {
        return try {
            val payload = questions.mapIndexed { index, q ->
                buildJsonObject {
                    put("session_id",     sessionId)
                    put("question_id",    q.id)
                    put("question_order", index + 1)
                }
            }
            db.from("session_questions").insert(payload)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── ANSWERS ──────────────────────────────────────────────────────────────

    suspend fun upsertAnswer(
        sessionQuestionId: String,
        chosenOptionId: String,
        isCorrect: Boolean
    ): Result<Unit> {
        return try {
            db.from("session_answers")
                .upsert(
                    buildJsonObject {
                        put("session_question_id", sessionQuestionId)
                        put("chosen_option_id",    chosenOptionId)
                        put("is_correct",          isCorrect)
                        put("answered_at",         java.time.Instant.now().toString())
                    }
                ) {
                    onConflict = "session_question_id"
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnswersBySession(sessionId: String): Result<List<SessionAnswer>> {
        return try {
            val answers = db.from("session_answers")
                .select(
                    Columns.raw(
                        "id, chosen_option_id, is_correct, answered_at, " +
                                "session_questions!inner(id, session_id)"
                    )
                ) {
                    filter { eq("session_questions.session_id", sessionId) }
                }
                .decodeList<SessionAnswer>()
            Result.success(answers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── SUBMIT ───────────────────────────────────────────────────────────────

    suspend fun submitExam(
        sessionId: String,
        totalQuestions: Int,
        jumlahBenar: Int,
        score: Double
    ): Result<ExamResult> {
        return try {
            val now = java.time.Instant.now().toString()

            // 1. Update exam_sessions
            db.from("exam_sessions")
                .update(
                    buildJsonObject {
                        put("status",   "submitted")
                        put("end_time", now)
                        put("score",    score)
                    }
                ) {
                    filter { eq("id", sessionId) }
                }

            // 2. Insert exam_results
            val result = db.from("exam_results")
                .insert(
                    buildJsonObject {
                        put("session_id",      sessionId)
                        put("total_questions", totalQuestions)
                        put("correct_answers", jumlahBenar)
                        put("score",           score)
                    }
                ) {
                    select(Columns.ALL)
                }
                .decodeSingle<ExamResult>()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── HISTORY ──────────────────────────────────────────────────────────────

    suspend fun getExamHistory(profileId: String): Result<List<ExamSession>> {
        return try {
            val sessions = db.from("exam_sessions")
                .select(Columns.ALL) {
                    filter {
                        eq("profile_id", profileId)
                        neq("status",    "not_started")
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<ExamSession>()
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExamResult(sessionId: String): Result<ExamResult> {
        return try {
            val result = db.from("exam_results")
                .select(Columns.ALL) {
                    filter { eq("session_id", sessionId) }
                }
                .decodeSingle<ExamResult>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── SUBJECTS & TOPICS ────────────────────────────────────────────────────

    suspend fun getAllSubjects(): Result<List<Subject>> {
        return try {
            val subjects = db.from("subjects")
                .select(Columns.ALL)
                .decodeList<Subject>()
            Result.success(subjects)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopics(subjectId: String): Result<List<TopicGroup>> {
        return try {
            val topics = db.from("topic_groups")
                .select(Columns.ALL) {
                    filter { eq("subject_id", subjectId) }
                }
                .decodeList<TopicGroup>()
            Result.success(topics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── TOPIC STATS ──────────────────────────────────────────────────────────

    suspend fun saveTopicStats(stats: List<ResultTopicStat>): Result<Unit> {
        return try {
            val payload = stats.map { stat ->
                buildJsonObject {
                    put("session_id", stat.sessionId)
                    put("topic_id",   stat.topicId)
                    put("correct",    stat.correct)
                    put("total",      stat.total)
                    put("percentage", stat.percentage)
                }
            }
            db.from("result_topic_stats").insert(payload)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopicStats(sessionId: String): Result<List<ResultTopicStat>> {
        return try {
            val stats = db.from("result_topic_stats")
                .select(Columns.ALL) {
                    filter { eq("session_id", sessionId) }
                }
                .decodeList<ResultTopicStat>()
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}