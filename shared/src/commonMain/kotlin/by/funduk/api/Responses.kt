package by.funduk.api

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(val accessToken: String)