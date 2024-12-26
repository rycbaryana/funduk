package by.funduk

import by.funduk.db.*
import by.funduk.model.*
import by.funduk.routes.taskRoutes
import by.funduk.plugins.configureDatabase
import by.funduk.plugins.configureSwagger
import by.funduk.routes.authRoutes
import by.funduk.routes.submitRoutes
import by.funduk.routes.userRoutes
import by.funduk.services.*
import by.funduk.utils.hashPassword
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
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
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
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
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
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
        SchemaUtils.drop(Tasks, Users, Tags, TasksTags, Submissions, Comments, RefreshTokens, TestCases)
        SchemaUtils.create(Tasks, Users, Tags, TasksTags, Submissions, Comments, RefreshTokens, TestCases)
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
            authRoutes()
            userRoutes()
        }
        webSocket("/notifications/{task_id}") {
            val taskId = call.parameters["task_id"]!!.toInt()

            val frame = incoming.receive() as Frame.Text
            val message = Json.decodeFromString<UserMessage>(frame.readText())

            val userId = message.userId

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
                    tags = listOf(Tag.Greedy, Tag.TwoSAT),
                    notes = "Welcome!"
                )
            ).let {
                TestService.apply {
                    addTestCase(it, TestCase("", "Hello, world!"))
                }
            }
            add(
                Task(
                    name = "x^3",
                    statement = "Write a program that takes an integer input x and prints the result of x ^ 3 to the standard output.",
                    rank = Rank.Cow,
                    tags = listOf(Tag.DP, Tag.BinSearch),
                    samples = listOf(TestCase("2", "8"), TestCase("-1", "-1"))
                )
            ).let {
                TestService.apply {
                    addTestCase(it, TestCase("1", "1"))
                    addTestCase(it, TestCase("3", "27"))
                    addTestCase(it, TestCase("-2", "-8"))
                    addTestCase(it, TestCase("-3", "-27"))
                }
            }
            add(
                Task(
                    name = "Alice and Bob",
                    statement = "Alice has x apples and Bob has y apples. How many apples in total they possess?",
                    rank = Rank.MediumRare,
                    tags = listOf(Tag.DS, Tag.FFT),
                    notes = "Try using addition",
                    samples = listOf(TestCase("2 2", "4"))
                )
            ).let {
                TestService.apply {
                    addTestCase(it, TestCase("1 2", "3"))
                    addTestCase(it, TestCase("2 2", "4"))
                    addTestCase(it, TestCase("1000 20000", "21000"))
                    addTestCase(it, TestCase("30 0", "30"))
                }
            }
        }
        UserService.apply {
            addUser("vlad", hashPassword("1234Debil$"))
            addUser("kirill", hashPassword("1234Debil$"))
        }
    }

