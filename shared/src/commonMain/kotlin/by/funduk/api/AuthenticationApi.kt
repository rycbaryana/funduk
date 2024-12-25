package by.funduk.api

import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*

enum class RegisterResult {
    AlreadyExist,
    Registered,
    UnknownError
}

object AuthenticationApi {
    suspend fun register(username: String, password: String): RegisterResult {
        val rep = client.post(kApiAddress) {
            url {
                appendPathSegments("auth", "register")
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(username, password))
            }
        }

        return when (rep.status) {
            HttpStatusCode.Conflict -> RegisterResult.AlreadyExist
            HttpStatusCode.Created -> RegisterResult.Registered
            else -> RegisterResult.UnknownError
        }
    }
}