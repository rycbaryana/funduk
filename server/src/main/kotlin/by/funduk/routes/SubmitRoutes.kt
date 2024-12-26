package by.funduk.routes

import by.funduk.api.SubmitRequest
import by.funduk.model.Submission
import by.funduk.services.SubmitService
import by.funduk.utils.extractUserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.*

fun Route.submitRoutes() {

    authenticate("auth-jwt") {
        post("/submit") {
            val submitRequest: SubmitRequest = call.receive()
            val submission = Submission(
                null,
                submitRequest.taskId,
                extractUserId(call.principal<JWTPrincipal>()!!.payload),
                Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Moscow")),
                submitRequest.code,
                submitRequest.language
            )
            val id = SubmitService.submitAndTest(submission)
            call.respond(HttpStatusCode.OK, id)
        }
    }

    route("/submission") {
        authenticate("auth-jwt") {
            get("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val submission = SubmitService.getSubmission(id)
                if (submission == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else if (submission.userId != extractUserId(call.principal<JWTPrincipal>()!!.payload)) {
                    call.respond(HttpStatusCode.Forbidden)
                } else {
                    call.respond(HttpStatusCode.OK, submission)
                }
            }
        }


        route("/views") {
            get {
                val taskId = call.request.queryParameters["taskId"]?.toInt()
                val userId = call.request.queryParameters["userId"]?.toInt()
                val count = call.request.queryParameters["count"]?.toInt() ?: Int.MAX_VALUE
                val offset = call.request.queryParameters["offset"]?.toInt() ?: 0

                if (taskId == null || userId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    val views = SubmitService.getSubmissionViews(taskId, userId, count, offset)
                    call.respond(views)
                }
            }

            get("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val view = SubmitService.getSubmissionView(id)
                call.respond(view ?: HttpStatusCode.NotFound)
            }
        }

    }

}