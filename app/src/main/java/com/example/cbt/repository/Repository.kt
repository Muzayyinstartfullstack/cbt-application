import io.github.jan.supabase.postgrest.from

class Repository {

    suspend fun getSubjects(): List<Subject> {
        return SupabaseClient.client
            .from("subjects")
            .select()
            .decodeList<Subject>()
    }

    suspend fun getExams(subjectId: Int): List<Exam> {
        return SupabaseClient.client
            .from("exams")
            .select {
                filter {
                    eq("subject_id", subjectId)
                }
            }
            .decodeList<Exam>()
    }

    suspend fun getTopics(subjectId: Int): List<TopicGroup> {
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