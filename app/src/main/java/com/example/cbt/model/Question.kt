package com.example.cbt.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Tabel: questions
@Serializable
data class Question(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // subject_id uuid REFERENCES subjects(id) ON DELETE CASCADE
    @SerialName("subject_id")
    val subjectId: String = "",

    // topic_id uuid REFERENCES topic_groups(id) ON DELETE SET NULL
    @SerialName("topic_id")
    val topicId: String? = null,

    // question_text TEXT NOT NULL
    @SerialName("question_text")
    val questionText: String = "",

    // image_url TEXT
    @SerialName("image_url")
    val imageUrl: String? = null,

    // created_at TIMESTAMP DEFAULT NOW()
    @SerialName("created_at")
    val createdAt: String = "",

    // Relasi embed — untuk nested select dengan question_options
    @SerialName("question_options")
    val options: List<QuestionOption> = emptyList(),

    // Relasi embed opsional
    val subjects: Subject? = null,

    @SerialName("topic_groups")
    val topicGroup: TopicGroup? = null
)

// Tabel: question_options
@Serializable
data class QuestionOption(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // question_id uuid REFERENCES questions(id) ON DELETE CASCADE
    @SerialName("question_id")
    val questionId: String = "",

    // option_label VARCHAR(5) NOT NULL — A/B/C/D
    @SerialName("option_label")
    val optionLabel: String = "",

    // option_text TEXT NOT NULL
    @SerialName("option_text")
    val optionText: String = "",

    // is_correct BOOLEAN NOT NULL DEFAULT FALSE
    @SerialName("is_correct")
    val isCorrect: Boolean = false
)

// Tabel: session_questions
@Serializable
data class SessionQuestion(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // session_id uuid REFERENCES exam_sessions(id) ON DELETE CASCADE
    @SerialName("session_id")
    val sessionId: String = "",

    // question_id uuid REFERENCES questions(id) ON DELETE CASCADE
    @SerialName("question_id")
    val questionId: String = "",

    // question_order INT NOT NULL
    @SerialName("question_order")
    val questionOrder: Int = 0,

    // Relasi embed — soal lengkap beserta opsi jawaban
    val questions: Question? = null
)

// Tabel: session_answers
@Serializable
data class SessionAnswer(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // session_question_id uuid REFERENCES session_questions(id) ON DELETE CASCADE
    @SerialName("session_question_id")
    val sessionQuestionId: String = "",

    // chosen_option_id uuid REFERENCES question_options(id)
    @SerialName("chosen_option_id")
    val chosenOptionId: String? = null,

    // is_correct BOOLEAN
    @SerialName("is_correct")
    val isCorrect: Boolean? = null,

    // answered_at TIMESTAMP DEFAULT NOW()
    @SerialName("answered_at")
    val answeredAt: String = "",

    // Relasi embed opsional
    @SerialName("session_questions")
    val sessionQuestion: SessionQuestion? = null,

    @SerialName("question_options")
    val chosenOption: QuestionOption? = null
)