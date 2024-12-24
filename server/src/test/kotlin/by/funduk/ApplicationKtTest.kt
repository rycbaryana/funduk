package by.funduk

import by.funduk.api.AuthRequest
import kotlin.test.Test
import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.test.assertEquals

class ApplicationKtTest {
    @Test
    fun testPing() = testApplication {
        application {
            module()
        }
        val response = client.get("/ping")
        assertEquals(response.status, HttpStatusCode.OK)
        assertEquals(response.bodyAsText().lowercase(), "pong");
    }

    @Test
    fun testAuth() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies)
        }
        var response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(username = "leha", password = "secret"))
        }
        assertEquals(response.status, HttpStatusCode.Created)
    }
}