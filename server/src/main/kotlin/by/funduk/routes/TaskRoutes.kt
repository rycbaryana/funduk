package by.funduk.routes

import by.funduk.model.Task
import by.funduk.services.TaskService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes(service: TaskService) {
    route("/tasks") {
        get {
            call.respond(service.allTasks())
        }
        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val task = service.get(id)
            call.respond(task ?: HttpStatusCode.NotFound)
        }
        post {
            val task: Task = call.receive()
            call.application.environment.log.info(task.toString())
            service.add(task)
            call.respond(HttpStatusCode.Created)
        }
        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val deleted = service.delete(id)
            call.respond(
                if (deleted) {
                    HttpStatusCode.OK
                } else {
                    HttpStatusCode.NotFound
                }
            )
        }
    }
}