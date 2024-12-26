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
    ) = client.get(kApiAddress) {
        url {
            appendPathSegments("submission", "views")
            parameters.append("taskId", taskId.toString())
            parameters.append("userId", userId.toString())
            parameters.append("count", count.toString())
            parameters.append("offset", offset.toString())
        }
    }

    suspend fun connectToTaskWebSocket(id: Int, onReceive: (Message) -> Unit) =
        client.webSocket("$kServerWebSocketAddress/notifications/$id") {
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

    fun disconnectFromTaskWebSocket(id: Int, onReceive: (Message) -> Unit) = client.close()

    suspend fun getSubmissionView(id: Int) = client.get(kApiAddress) {
        url {
            appendPathSegments("submission", "views", id.toString())
        }
    }

    suspend fun submit(access: String, submission: SubmitRequest) = client.post(kApiAddress) {
        url {
            appendPathSegments("submit")
            contentType(ContentType.Application.Json)
            setBody(submission)
        }
        headers.append(HttpHeaders.Authorization, "Bearer $access")
    }


    suspend fun getSubmission(access: String, id: Int) =
        client.get(kApiAddress) {
            url {
                appendPathSegments("submission", id.toString())
            }
            headers.append(HttpHeaders.Authorization, "Bearer $access")
        }

}