package by.funduk.api

import by.funduk.model.*
import by.funduk.ui.SubmissionView
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json

object UserApi {
    suspend fun getUserInfo(
        userId: Int
    ) = client.get(kApiAddress) {
        url {
            appendPathSegments("user", "$userId", "info")
        }
    }

    suspend fun setUserInfo(
        access: String,
        userId: Int,
        info: UserInfo
    ) = client.post(kApiAddress) {
        url {
            appendPathSegments("user", "$userId", "info")
            contentType(ContentType.Application.Json)
            setBody(info)
        }
        headers.append(HttpHeaders.Authorization, "Bearer $access")
    }

    suspend fun getSubmissions(id: Int, count: Int = Int.MAX_VALUE, offset: Int = 0) = client.get(kApiAddress) {
        url {
            appendPathSegments("user", "$id", "submissions")
            parameters.append("count", count.toString())
            parameters.append("offset", offset.toString())
        }
    }

}