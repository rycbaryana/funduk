package by.funduk

import kotlin.test.Test
import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
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
}