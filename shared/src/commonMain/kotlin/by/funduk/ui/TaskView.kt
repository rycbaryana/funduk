package by.funduk.ui

import by.funduk.model.Rank
import by.funduk.model.Status
import by.funduk.model.Tag
import kotlinx.serialization.Serializable

@Serializable
data class TaskView(
    val id: Int,
    val name: String,
    val rank: Rank,
    val tags: List<Tag> = listOf(),
    val solvedCount: Int = 0,
    val userStatus: Status? = null
)
