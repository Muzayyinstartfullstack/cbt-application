package com.example.cbt.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Tabel: topic_groups
@Serializable
data class TopicGroup(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // subject_id uuid REFERENCES subjects(id)
    @SerialName("subject_id")
    val subjectId: String = "",

    // topic_name VARCHAR(100) NOT NULL
    @SerialName("topic_name")
    val topicName: String = "",

    // created_at TIMESTAMP DEFAULT NOW()
    @SerialName("created_at")
    val createdAt: String = ""
)

// Tabel: result_topic_stats
@Serializable
data class ResultTopicStat(
    // id uuid PRIMARY KEY DEFAULT gen_random_uuid()
    val id: String = "",

    // session_id uuid REFERENCES exam_sessions(id)
    @SerialName("session_id")
    val sessionId: String = "",

    // topic_id uuid REFERENCES topic_groups(id) ON DELETE SET NULL
    @SerialName("topic_id")
    val topicId: String? = null,

    // correct INT NOT NULL
    val correct: Int = 0,

    // total INT NOT NULL
    val total: Int = 0,

    // percentage NUMERIC(5,2)
    val percentage: Double? = null,

    // created_at TIMESTAMP DEFAULT NOW()
    @SerialName("created_at")
    val createdAt: String = "",

    // Relasi embed opsional — untuk join dengan topic_groups
    @SerialName("topic_groups")
    val topicGroup: TopicGroup? = null
)