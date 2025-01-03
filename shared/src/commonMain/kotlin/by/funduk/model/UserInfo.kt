package by.funduk.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val login: String,
    val realName: String? = null,
    val birthDate: LocalDate? = null,
    val about: String? = null,
    val solvedCount: Int = 0,
    val rank: Rank = Rank.Calf
)
