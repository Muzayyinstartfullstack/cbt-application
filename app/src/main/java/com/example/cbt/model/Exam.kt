package com.example.cbt.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ENUM status — sesuai: CREATE TYPE session_status AS ENUM (...)
enum class SessionStatus {
    @SerialName("not_started") NOT_STARTED,
    @SerialName("ongoing")     ONGOING,
    @SerialName("submitted")   SUBMITTED,
    @SerialName("expired")     EXPIRED
}

// Tabel: exams
@Serializable
data class Exam(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // subject_id uuid REFERENCES subjects(id) ON DELETE CASCADE
    @SerialName("subject_id")
    val subjectId: String = "",

    // title VARCHAR(100) NOT NULL
    val title: String = "",

    // description TEXT
    val description: String? = null,

    // start_time TIMESTAMP NOT NULL
    @SerialName("start_time")
    val startTime: String = "",

    // end_time TIMESTAMP NOT NULL
    @SerialName("end_time")
    val endTime: String = "",

    // duration_minutes INT NOT NULL
    @SerialName("duration_minutes")
    val durationMinutes: Int = 0,

    // shuffle_questions BOOLEAN DEFAULT TRUE
    @SerialName("shuffle_questions")
    val shuffleQuestions: Boolean = true,

    // shuffle_options BOOLEAN DEFAULT TRUE
    @SerialName("shuffle_options")
    val shuffleOptions: Boolean = true,

    // created_at TIMESTAMP DEFAULT NOW()
    @SerialName("created_at")
    val createdAt: String = "",

    // Relasi embed opsional — untuk join dengan subjects
    val subjects: Subject? = null
)

// Tabel: exam_participants
@Serializable
data class ExamParticipant(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // exam_id uuid REFERENCES exams(id) ON DELETE CASCADE
    @SerialName("exam_id")
    val examId: String = "",

    // profile_id uuid REFERENCES profiles(id) ON DELETE CASCADE
    @SerialName("profile_id")
    val profileId: String = "",

    // Relasi embed opsional
    val exams: Exam? = null,
    val profiles: Profile? = null
)

// Tabel: exam_sessions
@Serializable
data class ExamSession(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // exam_id uuid REFERENCES exams(id) ON DELETE CASCADE
    @SerialName("exam_id")
    val examId: String = "",

    // profile_id uuid REFERENCES profiles(id) ON DELETE CASCADE
    @SerialName("profile_id")
    val profileId: String = "",

    // start_time TIMESTAMP
    @SerialName("start_time")
    val startTime: String? = null,

    // end_time TIMESTAMP
    @SerialName("end_time")
    val endTime: String? = null,

    // status session_status DEFAULT 'not_started'
    val status: String = "not_started",

    // score NUMERIC(5,2)
    val score: Double? = null,

    // created_at TIMESTAMP DEFAULT NOW()
    @SerialName("created_at")
    val createdAt: String = "",

    // Relasi embed opsional
    val exams: Exam? = null
)

// Tabel: exam_results
@Serializable
data class ExamResult(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // session_id uuid REFERENCES exam_sessions(id) ON DELETE CASCADE
    @SerialName("session_id")
    val sessionId: String = "",

    // total_questions INT NOT NULL
    @SerialName("total_questions")
    val totalQuestions: Int = 0,

    // correct_answers INT NOT NULL
    @SerialName("correct_answers")
    val correctAnswers: Int = 0,

    // score NUMERIC(5,2) NOT NULL
    val score: Double = 0.0,

    // created_at TIMESTAMP DEFAULT NOW()
    @SerialName("created_at")
    val createdAt: String = "",

    // Relasi embed opsional
    @SerialName("exam_sessions")
    val examSession: ExamSession? = null
)