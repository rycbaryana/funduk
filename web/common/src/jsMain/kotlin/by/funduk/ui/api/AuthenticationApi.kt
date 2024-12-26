package by.funduk.ui.api

import by.funduk.api.*
import io.ktor.http.*
import js.promise.Promise
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import kotlin.js.json

suspend fun AuthenticationApi.logIn(username: String, password: String): String? {
    val token: AccessTokenResponse? = window.fetch(
        "$kApiAddress/auth/login",
        RequestInit(
            method = "POST",
            body = Json.encodeToString(AuthRequest(username, password)),
            credentials = RequestCredentials.INCLUDE,
            headers = json(
                "Content-Type" to "application/json",
                "Accept" to "application/json"
            )
        )
    ).then { response ->
        if (response.ok) {
            response.text()
        } else {
            Promise.reject(Exception("bad request"))
        }
    }.then {
        Json.decodeFromString<AccessTokenResponse>(it as String)
    }.catch {
        null
    }.await()

    return token?.accessToken
}

suspend fun AuthenticationApi.logOut() {
    val token: AccessTokenResponse? = window.fetch(
        "$kApiAddress/auth/logout",
        RequestInit(
            method = "POST",
            credentials = RequestCredentials.INCLUDE,
            headers = json(
                "Content-Type" to "application/json",
                "Accept" to "application/json"
            )
        )
    ).then { response ->
        if (response.ok) {
            response.text()
        } else {
            Promise.reject(Exception("bad request"))
        }
    }.then {
        Json.decodeFromString<AccessTokenResponse>(it as String)
    }.catch {
        null
    }.await()
}

suspend fun AuthenticationApi.Me(): Int? {
    val id: Int? = window.fetch(
        "$kApiAddress/me",
        RequestInit(
            method = "Get",
            credentials = RequestCredentials.INCLUDE,
            headers = json(
                "Content-Type" to "application/json",
                "Accept" to "application/json"
            )
        )
    ).then { response ->
        if (response.ok) {
            response.text()
        } else {
            Promise.reject(Exception("bad request"))
        }
    }.then {
        Json.decodeFromString<Int>(it as String)
    }.catch {
        null
    }.await()

    return id
}

suspend fun AuthenticationApi.refresh(): String? {
    val token: AccessTokenResponse? = window.fetch(
        "$kApiAddress/auth/refresh",
        RequestInit(
            method = "GET",
            credentials = RequestCredentials.INCLUDE,
            headers = json(
                "Content-Type" to "application/json",
                "Accept" to "application/json"
            )
        )
    ).then { response ->
        if (response.ok) {
            response.text()
        } else {
            Promise.reject(Exception("bad request"))
        }
    }.then {
        Json.decodeFromString<AccessTokenResponse>(it as String)
    }.catch {
        null
    }.await()

    return token?.accessToken
}