package by.funduk.api

import by.funduk.model.Task
import by.funduk.ui.TaskView
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object TasksApi {
    suspend fun getTaskViews(count: Int, offset: Int) = client.get(kApiAddress) {
        url {
            appendPathSegments("task", "views")
            parameters.append("count", count.toString())
            parameters.append("offset", offset.toString())
        }
    }

    suspend fun getTask(id: Int) = client.get(kApiAddress) {
        url {
            appendPathSegments("task", id.toString())
        }
    }
}