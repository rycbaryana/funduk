package by.funduk.routes

import by.funduk.model.Submission
import by.funduk.model.RawSubmission
import by.funduk.services.SubmitService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.*

fun Route.submitRoutes() {
    route("/submit") {
        post {
            val rawSubmission: RawSubmission = call.receive()
            val submission = Submission(
                null,
                rawSubmission.taskId,
                rawSubmission.userId,
                Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Moscow")),
                rawSubmission.code,
                rawSubmission.language
            )
            println(submission)
            val id = SubmitService.submitAndTest(submission)
            call.respond(HttpStatusCode.OK, id)
        }
    }

    route("/submission") {
        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val submission = SubmitService.getSubmission(id)
            if (submission == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(HttpStatusCode.OK, submission)
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