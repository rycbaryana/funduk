package by.funduk.internal.routes

import by.funduk.model.Task
import by.funduk.internal.services.TaskService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes() {
    route("/tasks") {
        get {
            call.respond(TaskService.allTasks())
        }
        get("/views") {
            val count = call.request.queryParameters["count"]?.toInt() ?: 10
            val offset = call.request.queryParameters["offset"]?.toInt() ?: 0
            val views = TaskService.getViews(count, offset)
            call.respond(views)
        }
        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val task = TaskService.get(id)
            if (task == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(HttpStatusCode.OK, task)
            }
        }
        post {
            val task: Task = call.receive()
            val taskId = TaskService.add(task)
            call.respond(HttpStatusCode.Created, taskId)
        }
        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val deleted = TaskService.delete(id)
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