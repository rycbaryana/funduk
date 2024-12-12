package by.funduk.routes

import by.funduk.AuthConfig
import by.funduk.services.UserService
import by.funduk.utils.checkPassword
import by.funduk.utils.hashPassword
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)

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
                val token = JWT.create().withSubject(user.id.toString()).withClaim("username", user.username)
                    .withIssuer(AuthConfig.issuer)
                    .withAudience(AuthConfig.audience).sign(
                        Algorithm.HMAC256(AuthConfig.secret)
                    )
                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }

    }
}