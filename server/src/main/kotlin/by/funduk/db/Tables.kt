package by.funduk.db

import org.jetbrains.exposed.dao.id.IntIdTable

object Tasks : IntIdTable("tasks") {
    val name = varchar("name", 50)
    val statement = text("statement")
}

object Users : IntIdTable("users") {
    val username = varchar("username", 50)
    val password = varchar("password", 255)
}