package by.funduk.api

import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*

object AuthenticationApi {
    suspend fun register(username: String, password: String) = client.post(kApiAddress) {
        url {
            appendPathSegments("auth", "register")
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(username, password))
        }
    }

    suspend fun me(access: String) = client.get(kApiAddress) {
        url {
            appendPathSegments("me")
        }
        headers.append(HttpHeaders.Authorization, "Bearer $access")
    }
}