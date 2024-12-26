package by.funduk.ui.api

import by.funduk.api.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.date.*
import io.ktor.utils.io.*
import js.promise.Promise
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import kotlin.js.json
import kotlinx.browser.localStorage
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

var accessToken: String? = null

fun GetFromLocalStorage(): String? = localStorage.getItem("$kServerAddress/access")
fun setToLocalStorage(value: String) = localStorage.setItem("$kServerAddress/access", value)

class DummyResponse(
    private val statusCode: HttpStatusCode,
) :
    HttpResponse() {
    @InternalAPI
    override val content: ByteReadChannel
        get() = ByteReadChannel("")

    override val call: HttpClientCall
        get() = HttpClientCall(HttpClient())

    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext

    override val headers: Headers
        get() = Headers.Empty

    override val requestTime: GMTDate
        get() = GMTDate()

    override val responseTime: GMTDate
        get() = GMTDate()

    override val status: HttpStatusCode
        get() = statusCode

    override val version: HttpProtocolVersion
        get() = HttpProtocolVersion(name = "HTTP", major = 1, minor = 1)
}

suspend fun InitPage() {
    accessToken = GetFromLocalStorage()
    println("getting from local storage $accessToken")
    if (accessToken == null) {
        val token = AuthenticationApi.refresh()
        println("refresh $token")
        accessToken = token
        if (token != null) {
            setToLocalStorage(token)
        }
    }
}

suspend fun withAuth(block: suspend (String) -> HttpResponse): HttpResponse {
    var token = accessToken
    if (token == null) {
        return DummyResponse(HttpStatusCode.Unauthorized)
    }

    val rep = block(token)

    println(rep)
    return when (rep.status) {
        HttpStatusCode.Unauthorized -> {
            token = AuthenticationApi.refresh()
            println("refresh $token")
            accessToken = token
            if (token == null) {
                return DummyResponse(HttpStatusCode.Unauthorized)
            }
            println("setting to local storage $token")
            setToLocalStorage(token)
            block(token)
        }

        else -> {
            rep
        }
    }
}