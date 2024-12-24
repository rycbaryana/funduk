package by.funduk.services

import by.funduk.db.Users
import by.funduk.db.query
import by.funduk.model.User
import org.jetbrains.exposed.sql.*

object UserService {
    suspend fun addUser(username: String, hashedPassword: String): User {
        val id = query {
            Users.insertAndGetId {
                it[Users.username] = username
                it[password] = hashedPassword
            }.value
        }
        return User(id, username, hashedPassword)
    }

    suspend fun findByUsername(username: String): User? {
        return query {
            Users.selectAll().where { Users.username eq username }.mapNotNull {
                User(
                    it[Users.id].value,
                    it[Users.username],
                    it[Users.password]
                )
            }.singleOrNull()
        }
    }

    suspend fun findByUserId(userId: Int): User? {
        return query {
            Users.selectAll().where { Users.id eq userId }.mapNotNull {
                User(
                    it[Users.id].value,
                    it[Users.username],
                    it[Users.password]
                )
            }.singleOrNull()
        }
    }
}