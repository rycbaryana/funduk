package by.funduk.api

import by.funduk.model.Language
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)

@Serializable
data class RefreshRequest(val refreshToken: String)

@Serializable
data class SubmitRequest(
    val taskId: Int,
    val code: String,
    val language: Language
)