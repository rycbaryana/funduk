package by.funduk.model

import kotlinx.serialization.Serializable

@Serializable
data class TestInfo(
    val status: Status = Status.Queued,
    var test: Int = 0,
    val time: Int = 0,
    val memory: Int = 0
)
