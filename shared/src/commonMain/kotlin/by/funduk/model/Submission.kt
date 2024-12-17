package by.funduk.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Serializable
data class Submission(
    val id: Int? = null,
    val taskId: Int,
    val userId: Int,
    val submitTime: LocalDateTime,
    val code: String,
    val language: Language,
    val testInfo: TestInfo = TestInfo(),
)

@Serializable
data class RawSubmission(
    val taskId: Int,
    val userId: Int,
    val code: String,
    val language: Language
)