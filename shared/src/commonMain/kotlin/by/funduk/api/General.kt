package by.funduk.api

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonEncoder

const val kApiAddress = "http://127.0.0.1:8080/api"
const val kServerWebSocketAddress = "ws://127.0.0.1:8080"

val client = HttpClient {
    install(WebSockets) {
        pingInterval = 5_000
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    install(ContentNegotiation) {
        json()
    }
    install(HttpCookies)
}