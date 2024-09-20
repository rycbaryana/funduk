package by.funduk

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

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