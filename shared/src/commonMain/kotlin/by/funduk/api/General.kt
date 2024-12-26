package by.funduk.api

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonEncoder

const val kServerAddress = "http://127.0.0.1:8080"
const val kApiAddress = "$kServerAddress/api"
const val kServerWebSocketAddress = "ws://127.0.0.1:8080"

val client = HttpClient {
    install(HttpCookies)
    install(WebSockets) {
        pingInterval = 5_000
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    install(ContentNegotiation) {
        json()
    }
}
