package by.funduk.api

import by.funduk.model.*
import by.funduk.ui.SubmissionView
import by.funduk.ui.TaskView
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.LocalDateTime

object SubmissionApi {
    suspend fun getSubmissionViews(taskId: Int, userId: Int, count: Int = Int.MAX_VALUE, offset: Int = 0): List<SubmissionView> =
        client.get(kApiAddress) {
            url {
                appendPathSegments("submission", "views")
                parameters.append("taskId", taskId.toString())
                parameters.append("userId", userId.toString())
                parameters.append("count", count.toString())
                parameters.append("offset", offset.toString())
            }
        }.body()

    suspend fun submit(submission: RawSubmission): Int? {
        val rep = client.post(kApiAddress) {
            url {
                appendPathSegments("submit")
                contentType(ContentType.Application.Json)
                setBody(submission)
            }
        }

        if (rep.status != HttpStatusCode.OK) {
            return null
        }

        return rep.body<Int>()
    }

    suspend fun getSubmission(id: Int): Submission? =
        client.get(kApiAddress) {
            url {
                appendPathSegments("submission", id.toString())
            }
        }.let {
            if (it.status != HttpStatusCode.OK) {
                return null
            } else {
                return it.body<Submission>()
            }
        }
}