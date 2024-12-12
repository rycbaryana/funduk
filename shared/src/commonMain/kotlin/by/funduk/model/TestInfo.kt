package by.funduk.model

import kotlinx.serialization.Serializable

@Serializable
data class TestInfo(
    val status: Status,
    val time: Int,
    val memory: Int
)
