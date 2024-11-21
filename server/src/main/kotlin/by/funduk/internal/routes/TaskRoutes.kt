package by.funduk.internal.routes

import by.funduk.model.Task
import by.funduk.comunication.TaskViewBatch
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
        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val task = TaskService.get(id)
            call.respond(task ?: HttpStatusCode.NotFound)
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

    route("/task_views") {
        post {
            val batch: TaskViewBatch = call.receive()
            val views = TaskService.getViews(batch.count, batch.offset)
            call.respond(views)
        }
    }
}