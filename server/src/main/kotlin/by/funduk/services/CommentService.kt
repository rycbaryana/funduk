package by.funduk.services

import by.funduk.db.Comments
import by.funduk.db.query
import by.funduk.model.Comment
import io.ktor.websocket.*
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.insertAndGetId
import java.util.*

object CommentService {
    
    suspend fun add(comment: Comment): Int {
        val commentId = query {
            Comments.insertAndGetId {
                it[taskId] = comment.taskId
                it[userId] = comment.userId
                it[postTime] = comment.postTime.toJavaLocalDateTime()
                it[likes] = comment.likes
                it[dislikes] = comment.dislikes
            }
        }.value
        NotificationService.notifyAll(comment.taskId)
        return commentId
    }

}