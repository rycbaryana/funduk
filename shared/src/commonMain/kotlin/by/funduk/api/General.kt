package by.funduk.api

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

const val kApiAddress = "http://127.0.0.1:8080/api"

val client = HttpClient() {
    install(ContentNegotiation) {
        json()
    }
}