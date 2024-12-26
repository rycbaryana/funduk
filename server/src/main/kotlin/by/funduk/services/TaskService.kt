package by.funduk.services

import by.funduk.db.Tags
import by.funduk.db.Tags.name
import by.funduk.db.Tasks
import by.funduk.db.TasksTags
import by.funduk.db.query
import by.funduk.model.Task
import by.funduk.model.Rank
import by.funduk.model.Tag
import by.funduk.ui.TaskView
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object TaskService {
    suspend fun allTasks(): List<Task> {
        return query {
            Tasks.selectAll()
                .map {
                    Task(
                        it[Tasks.id].value,
                        it[Tasks.name], it[Tasks.statement],
                        Rank.entries[it[Tasks.rank]],
                        getTags(it[Tasks.id].value),
                        it[Tasks.solvedCount],
                        it[Tasks.samples]?.toList(),
                        it[Tasks.notes]
                    )
                }
        }
    }

    suspend fun getViews(count: Int, offset: Int = 0): List<TaskView> = query {
        Tasks.selectAll().limit(count).offset(offset.toLong()).map {
            TaskView(
                it[Tasks.id].value,
                it[Tasks.name], Rank.entries[it[Tasks.rank]], getTags(it[Tasks.id].value)
            )
        }
    }


    suspend fun add(task: Task): Int = query {
        val taskId = Tasks.insertAndGetId {
            it[name] = task.name
            it[statement] = task.statement
            it[rank] = task.rank.ordinal
            it[solvedCount] = task.solvedCount
            it[samples] = task.samples?.toTypedArray()
            it[notes] = task.notes
        }
        task.tags.forEach { tag ->
            val tagId = Tags.select(Tags.id).where { name eq tag.name }
                .singleOrNull()?.get(Tags.id) ?: Tags.insertAndGetId {
                it[name] = tag.name
            }
            TasksTags.insert {
                it[TasksTags.taskId] = taskId
                it[TasksTags.tagId] = tagId
            }
        }
        taskId.value
    }

    suspend fun get(id: Int): Task? = query {
        Tasks.selectAll()
            .where { Tasks.id eq id }
            .map {
                Task(
                    it[Tasks.id].value,
                    it[Tasks.name],
                    it[Tasks.statement],
                    Rank.entries[it[Tasks.rank]],
                    getTags(it[Tasks.id].value),
                    it[Tasks.solvedCount],
                    it[Tasks.samples]?.toList(),
                    it[Tasks.notes]
                )
            }
            .singleOrNull()
    }

    private suspend fun getTags(taskId: Int): List<Tag> =
        query {
            TasksTags
                .innerJoin(Tags)
                .selectAll().where { TasksTags.taskId eq taskId }
                .map { Tag.valueOf(it[name]) }
        }

    suspend fun delete(id: Int): Boolean {
        return query {
            Tasks.deleteWhere { Tasks.id eq id }
        } > 0
    }
}