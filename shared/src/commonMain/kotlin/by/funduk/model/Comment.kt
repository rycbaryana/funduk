package by.funduk.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Int? = null,
    val taskId: Int,
    val userId: Int,
    val content: String,
    val postTime: LocalDateTime,
    val likes: Int = 0,
    val dislikes: Int = 0
)
