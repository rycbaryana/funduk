package by.funduk.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int? = null,
    val name: String,
    val statement: String,
    val rank: Rank,
    val tags: List<Tag> = listOf(),
    val solvedCount: Int = 0,
)
