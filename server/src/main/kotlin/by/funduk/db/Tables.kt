package by.funduk.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Tasks : IntIdTable("tasks") {
    val name = varchar("name", 50)
    val statement = text("statement")
    val rank = integer("task_rank")
    val solvedCount = integer("rank").default(0)
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