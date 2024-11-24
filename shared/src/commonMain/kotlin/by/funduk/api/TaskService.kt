package by.funduk.api

import by.funduk.model.Task
import by.funduk.ui.TaskView
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object TasksApi {
    suspend fun getTasksViews(count: Int, offset: Int): List<TaskView> =
        client.get(kApiAddress) {
            url {
                appendPathSegments("tasks", "views")
                parameters.append("count", count.toString())
                parameters.append("offset", offset.toString())
            }
        }.body()

    suspend fun getTask(id: Int): Task =
        client.get(kApiAddress) {
            url {
                appendPathSegments("tasks", id.toString())
            }
        }.body()
}