package by.funduk

import by.funduk.db.Tasks
import by.funduk.db.Users
import by.funduk.routes.taskRoutes
import by.funduk.model.Task
import by.funduk.plugins.configureDatabase
import by.funduk.services.TaskService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    val database = configureDatabase()

    transaction(database) {
        SchemaUtils.create(Tasks)
        SchemaUtils.create(Users)
    }

    val taskService = TaskService(database)

    routing {
        staticResources("/", "/")
        get("/ping") {
            call.respondText("pong");
        }
        route("/api") {
            taskRoutes(taskService)
        }
    }

}