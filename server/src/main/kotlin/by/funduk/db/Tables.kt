package by.funduk.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Tasks : IntIdTable("tasks") {
    val name = varchar("name", 50)
    val statement = text("statement")
    val rank = integer("rank")
    val solvedCount = integer("solved").default(0)
}

object Tags : IntIdTable("tags") {
    val name = varchar("name", 32)
}

object TasksTags: Table("tasks_tags") {
    val taskId = reference("task_id", Tasks, onDelete = ReferenceOption.CASCADE)
    val tagId = reference("tag_id", Tags, onDelete = ReferenceOption.CASCADE)
}

object Users : IntIdTable("users") {
    val username = varchar("username", 50)
    val password = varchar("password", 255)
}

object Submissions : IntIdTable("submissions") {
    val taskId = Submissions.reference("task_id", Tasks)
    val userId = Submissions.reference("user_id", Users)
    val submitTime = datetime("submit_time")
    val code = text("code")
    val language = varchar("language", 16)
    val status = varchar("status", 8)
    val time = integer("time").default(0)
    val memory = integer("memory").default(0)
}

object Comments : IntIdTable("comments") {
    val taskId = Comments.reference("task_id", Tasks, onDelete = ReferenceOption.CASCADE)
    val userId = Comments.reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val content = text("content")
    val postTime = datetime("post_time")
    val likes = integer("likes").default(0)
    val dislikes = integer("dislikes").default(0)
}