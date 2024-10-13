package by.funduk.services

import by.funduk.db.Tasks
import by.funduk.db.query
import by.funduk.model.Task
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object TaskService {
    suspend fun allTasks(): List<Task> {
        return query {
            Tasks.selectAll().map { Task(it[Tasks.id].value, it[Tasks.name], it[Tasks.statement]) }
        }
    }

    suspend fun add(task: Task): Int = query {
        Tasks.insert {
            it[name] = task.name
            it[statement] = task.statement
        }[Tasks.id].value
    }

    suspend fun get(id: Int): Task? {
        return query {
            Tasks.selectAll()
                .where { Tasks.id eq id }
                .map { Task(it[Tasks.id].value, it[Tasks.name], it[Tasks.statement]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, task: Task): Boolean {
        return query {
            Tasks.update({ Tasks.id eq id }) {
                it[name] = task.name
                it[statement] = task.statement
            }
        } > 0
    }

    suspend fun delete(id: Int): Boolean {
        return query {
            Tasks.deleteWhere { Tasks.id eq id }
        } > 0
    }
}