import kotlinx.serialization.Serializable

@Serializable
data class TopicGroup(
    val id: Int,
    val topic_name: String,
    val subject_id: Int
)