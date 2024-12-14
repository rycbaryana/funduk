package by.funduk.ui

import by.funduk.model.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SubmissionView(
    val id: Int? = null,
    val taskId: Int,
    val userId: Int,
    val taskName: String,
    val userName: String,
    val submitTime: LocalDateTime,
    val language: Language,
    val testInfo: TestInfo? = null,
)