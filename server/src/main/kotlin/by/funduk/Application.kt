package by.funduk

import by.funduk.db.Tags
import by.funduk.db.Tasks
import by.funduk.db.TasksTags
import by.funduk.db.Users
import by.funduk.model.Rank
import by.funduk.model.Tag
import by.funduk.model.Task
import by.funduk.routes.taskRoutes
import by.funduk.plugins.configureDatabase
import by.funduk.plugins.configureSwagger
import by.funduk.routes.authRoutes
import by.funduk.services.TaskService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

object AuthConfig {
    lateinit var secret: String
    lateinit var issuer: String
    lateinit var audience: String
    fun loadConfig() {
        secret = "secret"
        issuer = "AuthService"
        audience = "UserService"
    }
}

fun Application.module() {
    AuthConfig.loadConfig()
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Post)
    }
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging) {
        format { call ->
            val status = call.response.status()
            "${status?.value ?: "NA"} ${status?.description ?: "NA"} - ${call.request.httpMethod.value} ${call.request.uri}"
        }
    }
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                AuthConfig.run {
                    JWT.require(Algorithm.HMAC256(secret)).withIssuer(issuer).withAudience(audience).build()
                }
            )
            validate { credential ->
                if (credential.payload.subject.toIntOrNull() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
    configureSwagger()
    val database = configureDatabase()

    transaction(database) {
        SchemaUtils.create(Tasks, Users, Tags, TasksTags)
    }


    launch {
        TaskService.apply {
            add(
                Task(
                    name = "Hello, world!",
                    statement = "Print \"Hello, world!\" to the standard output.",
                    rank = Rank.Calf,
                    tags = listOf(Tag.Greedy, Tag.TwoSAT)

                )
            )
            add(
                Task(
                    name = "x^3",
                    statement = "Write a program that takes an integer input x and prints the result of x ^ 3 to the standard output.",
                    rank = Rank.Cow,
                    tags = listOf(Tag.DP, Tag.BinSearch)
                )
            )
            add(
                Task(
                    name = "Alice and Bob",
                    statement = "Alice and Bob each have 2 apples. How many apples in total they possess?",
                    rank = Rank.MediumRare,
                    tags = listOf(Tag.DS, Tag.FFT)

                )
            )
        }
    }


    routing {
        get("/ping") {
            call.respondText("pong")
        }
        route("/api") {
            taskRoutes()
        }
        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                call.respondText("Hello, $username")
            }

        }
        authRoutes()
        singlePageApplication {
            useResources = true
            defaultPage = "index.html"
        }
    }

}