package by.funduk.model

import kotlinx.serialization.Serializable

enum class MessageType {
    Status,
    Comment
}

@Serializable
sealed class Message

@Serializable
data class StatusMessage(val status: Status, val test: Int = 0) : Message()

@Serializable
data class CommentMessage(val comment: Comment) : Message()