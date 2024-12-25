package by.funduk.api

import by.funduk.model.*
import by.funduk.ui.SubmissionView
import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json

enum class RegisterResult {
    AlreadyExist,
    Registered,
    UnknownError
}

object AuthenticateApi {
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

    suspend fun logIn(username: String, password: String) {
        val rep = client.post(kApiAddress) {
            url {
                appendPathSegments("auth", "login")
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(username, password))
            }
        }

        println(rep)
        val coockies = client.cookies   ("http://127.0.0.1:8081")
        println(coockies)
    }
}