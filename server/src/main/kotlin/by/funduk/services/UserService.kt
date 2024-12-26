package by.funduk.services

import by.funduk.db.Users
import by.funduk.db.query
import by.funduk.model.Rank
import by.funduk.model.User
import by.funduk.model.UserInfo
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
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

    suspend fun getUserInfo(userId: Int): UserInfo? = query {
        Users.selectAll().where { Users.id eq userId }.mapNotNull {
            UserInfo(
                it[Users.realName],
                it[Users.birthDate]?.toKotlinLocalDate(),
                it[Users.about],
                it[Users.solvedCount],
                Rank.entries[it[Users.rating]]
            )
        }.singleOrNull()
    }

    suspend fun getUserInfo(username: String): UserInfo? = query {
        Users.selectAll().where { Users.username eq username }.mapNotNull {
            UserInfo(
                it[Users.realName],
                it[Users.birthDate]?.toKotlinLocalDate(),
                it[Users.about],
                it[Users.solvedCount],
                Rank.entries[it[Users.rating]]
            )
        }.singleOrNull()
    }

    suspend fun updateUserInfo(userId: Int, info: UserInfo) = query {
        Users.update(where = {Users.id eq userId}) {
            with (info) {
                it[Users.realName] = realName
                it[Users.birthDate] = birthDate?.toJavaLocalDate()
                it[Users.about] = about
                it[Users.solvedCount] = solvedCount
                it[rating] = rank.ordinal
            }
        }
    }
}