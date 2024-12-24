package by.funduk.api

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)

@Serializable
data class RefreshRequest(val refreshToken: String)