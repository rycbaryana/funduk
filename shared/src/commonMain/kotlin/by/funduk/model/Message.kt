package by.funduk.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Message

@Serializable
data class StatusMessage(val id: Int, val testInfo: TestInfo) : Message()

@Serializable
data class CommentMessage(val comment: Comment) : Message()

@Serializable
data class UserMessage(val userId: Int) : Message()