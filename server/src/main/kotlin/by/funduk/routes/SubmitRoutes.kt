package by.funduk.routes

import by.funduk.model.Submission
import by.funduk.services.SubmitService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.submitRoutes() {
    route("/submit") {
        post {
            val submission: Submission = call.receive()
            SubmitService.submitAndTest(submission)
            call.respond(HttpStatusCode.OK)
        }
    }
}