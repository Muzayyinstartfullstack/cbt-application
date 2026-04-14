package com.example.cbt.repository

import com.example.cbt.database.SupabaseClient
import com.example.cbt.model.Exam
import com.example.cbt.model.Subject
import com.example.cbt.model.TopicGroup
import io.github.jan.supabase.postgrest.from

class Repository {

    suspend fun getSubjects(): List<Subject> {
        return SupabaseClient.client
            .from("subjects")
            .select()
            .decodeList<Subject>()
    }

    suspend fun getExams(subjectId: String): List<Exam> {
        return SupabaseClient.client
            .from("exams")
            .select {
                filter {
                    eq("subject_id", subjectId)
                }
            }
            .decodeList<Exam>()
    }

    suspend fun getTopics(subjectId: String): List<TopicGroup> {
        return SupabaseClient.client
            .from("topic_groups")
            .select {
                filter {
                    eq("subject_id", subjectId)
                }
            }
            .decodeList<TopicGroup>()
    }
}