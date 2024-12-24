package by.funduk.routes

import by.funduk.api.AccessTokenResponse
import by.funduk.api.AuthRequest
import by.funduk.services.AuthService
import by.funduk.services.UserService
import by.funduk.utils.checkPassword
import by.funduk.utils.hashPassword
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val REFRESH_TOKEN_COOKIE = "refresh_token"

fun Route.authRoutes() {
    route("/auth") {
        post("/register") {
            val request: AuthRequest = call.receive()
            val hashedPassword = hashPassword(request.password)
            if (UserService.findByUsername(request.username) != null) {
                call.respond(
                    HttpStatusCode.Conflict, "Oops! That username is already in use. Try a different one."
                )
            } else {
                UserService.addUser(request.username, hashedPassword)
                call.respond(
                    HttpStatusCode.Created, "User registered successfully"
                )
            }
        }
        post("/login") {
            val request: AuthRequest = call.receive()
            val user = UserService.findByUsername(request.username)
            if (user != null && checkPassword(request.password, user.password)) {
                val access = AuthService.createAccessToken(user.id!!)
                val refresh = AuthService.createRefreshToken(user.id!!)
                call.response.cookies.append(REFRESH_TOKEN_COOKIE, refresh, httpOnly = true)
                call.respond(HttpStatusCode.OK, AccessTokenResponse(access))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }
        post("/refresh") {
            val refreshToken = call.request.cookies[REFRESH_TOKEN_COOKIE]
            val tokens = refreshToken?.let { AuthService.refreshToken(it) }
            tokens?.let {
                val (access, refresh) = it
                call.response.cookies.append(REFRESH_TOKEN_COOKIE, refresh, httpOnly = true)
                call.respond(HttpStatusCode.OK, AccessTokenResponse(access))
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}