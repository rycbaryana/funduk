package by.funduk.routes

import by.funduk.model.UserInfo
import by.funduk.services.SubmitService
import by.funduk.services.TaskService
import by.funduk.services.UserService
import by.funduk.utils.extractUserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.userRoutes() {
    authenticate("auth-jwt") {
        get("/me") {
            val principal = call.principal<JWTPrincipal>()
            principal?.let {
                val userId = extractUserId(it.payload)
                call.respond(HttpStatusCode.OK, userId)
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }

    route("/user/{id}") {
        get("/info") {
            val id = call.parameters["id"]!!.toInt()
            val info = UserService.getUserInfo(id)
            info?.let {
                call.respond(HttpStatusCode.OK, it)
            } ?: call.respond(HttpStatusCode.NotFound)
        }
        authenticate("auth-jwt") {
            post("/info") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = extractUserId(principal.payload)
                val id = call.parameters["id"]!!.toInt()
                if (id == userId) {
                    val info: UserInfo = call.receive()
                    UserService.updateUserInfo(id, info)
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Forbidden)
                }
            }
        }

        get("/submissions") {
            val id = call.parameters["id"]!!.toInt()
            val user = UserService.findByUserId(id)
            if (user != null) {
                val count = call.request.queryParameters["count"]?.toInt() ?: Int.MAX_VALUE
                val offset = call.request.queryParameters["offset"]?.toInt() ?: 0
                val submissions = SubmitService.getUserSubmissionViews(id, count, offset)
                call.respond(HttpStatusCode.OK, submissions)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        get("/startedTasks") {
            val id = call.parameters["id"]!!.toInt()
            val user = UserService.findByUserId(id)
            if (user != null) {
                val tasks = TaskService.getTasksUserStarted(id)
                call.respond(HttpStatusCode.OK, tasks)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/avatar") {

        }

    }

}