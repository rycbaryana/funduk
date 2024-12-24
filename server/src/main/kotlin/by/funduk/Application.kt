package by.funduk

import by.funduk.db.*
import by.funduk.model.*
import by.funduk.routes.taskRoutes
import by.funduk.plugins.configureDatabase
import by.funduk.plugins.configureSwagger
import by.funduk.routes.authRoutes
import by.funduk.routes.submitRoutes
import by.funduk.services.*
import by.funduk.utils.extractUserId
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
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
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

object AuthConfig {
    lateinit var secret: String
    lateinit var issuer: String
    lateinit var audience: String

    init {
        secret = "secret"
        issuer = "AuthService"
        audience = "UserService"
    }
}

fun Application.module() {
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
            verifier(AuthService.verifier)
            validate { credential ->
                AuthService.validate(credential)
            }
        }
    }
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }

    configureSwagger()
    val database = configureDatabase()

    transaction(database) {
        SchemaUtils.drop(Tasks, Users, Tags, TasksTags, Submissions, Comments, RefreshTokens)
        SchemaUtils.create(Tasks, Users, Tags, TasksTags, Submissions, Comments, RefreshTokens)
    }

    populateDatabase()

    routing {
        get("/ping") {
            call.respondText("SHUT UP!!! STOP BOTHERING ME FOR NO REASON!!! I'M TRYING TO WORK!")
            NotificationService.notify(
                1, 1, CommentMessage(
                    Comment(
                        taskId = 1, userId = 1, content = "Good!", postTime = Clock.System.now().toLocalDateTime(
                            TimeZone.UTC
                        )
                    )
                )
            )
        }
        route("/api") {
            taskRoutes()
            submitRoutes()
        }
        webSocket("/notifications/{task_id}") {
            val taskId = call.parameters["task_id"]!!.toInt()
            val userId = 1
            val client = NotificationClient(taskId, userId, this)
            NotificationService.subscribe(client)
            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        call.application.environment.log.info("Got message from client (userId = $userId, taskId = $taskId): ${frame.readText()}")
                    }
                }
            }.onFailure {
                call.application.environment.log.error("WebSocket exception: ${it.localizedMessage}")
            }.also {
                NotificationService.unsubscribe(client)
            }
        }

        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                principal?.let {
                    val userId = extractUserId(it.payload)
                    call.respondText {
                        "Hello, ${UserService.findByUserId(userId)?.username}"
                    }
                } ?: call.respond(HttpStatusCode.Unauthorized)
            }

        }
        authRoutes()
    }
}

private fun CoroutineScope.populateDatabase() =
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
        UserService.apply {
            addUser("vlad", "pumpum")
        }
    }

