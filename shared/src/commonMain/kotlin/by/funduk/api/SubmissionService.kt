package by.funduk.api

import by.funduk.model.*
import by.funduk.ui.SubmissionView
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json

object SubmissionApi {
    suspend fun getSubmissionViews(
        taskId: Int,
        userId: Int,
        count: Int = Int.MAX_VALUE,
        offset: Int = 0
    ): List<SubmissionView> =
        client.get(kApiAddress) {
            url {
                appendPathSegments("submission", "views")
                parameters.append("taskId", taskId.toString())
                parameters.append("userId", userId.toString())
                parameters.append("count", count.toString())
                parameters.append("offset", offset.toString())
            }
        }.body()

    suspend fun initWebSocket(id: Int, onReceive: (Message) -> Unit) =
        client.webSocket("$kServerAddress/notifications/$id") {
            for (message in incoming) {
                when (message) {
                    is Frame.Text -> {
                        val receivedMessage = Json.decodeFromString<Message>(message.readText())
                        onReceive(receivedMessage)
                    }
                    else -> {}
                }
            }
        }

    suspend fun getSubmissionView(id: Int): SubmissionView =
        client.get(kApiAddress) {
            url {
                appendPathSegments("submission", "views", id.toString())
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