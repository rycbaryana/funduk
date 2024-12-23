package by.funduk.services

import by.funduk.model.Message
import io.ktor.server.websocket.*
import io.ktor.util.logging.*
import io.ktor.websocket.*
import java.util.*

data class NotificationClient(
    val taskId: Int, val userId: Int, val session: DefaultWebSocketServerSession
)

internal val Logger = KtorSimpleLogger("NotificationLogger");

object NotificationService {
    private var subscribers = Collections.synchronizedMap<Int, MutableList<NotificationClient>>(mutableMapOf())

    fun subscribe(client: NotificationClient) {
        subscribers.getOrPut(client.taskId) {
            mutableListOf()
        } += client
        Logger.info("New subscriber: $client")
    }

    fun unsubscribe(client: NotificationClient) {
        subscribers[client.taskId]?.remove(client)?.let {
            if (it) {
                Logger.info("Delete subscriber: $client")
            }
        }
    }

    suspend fun notify(taskId: Int, userId: Int, message: Message) {
        val client = subscribers[taskId]?.firstOrNull {
            it.userId == userId
        }
        client?.session?.sendSerialized(message)
            ?: Logger.info("Notification client (userId = ${userId}, taskId = ${taskId}) not found!")
    }

    suspend fun notifyAll(taskId: Int) {
        subscribers[taskId]?.forEach {
            it.session.send(Frame.Text("All Notification!"))
        }
    }

}