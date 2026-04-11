import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    val id: Int,
    val title: String,
    val description: String?,
    val subject_id: Int
)