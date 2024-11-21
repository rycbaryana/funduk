import by.funduk.comunication.TaskViewBatch
import by.funduk.ui.general.Sizes
import web.dom.document
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div

import emotion.react.*

import by.funduk.ui.system.nav
import by.funduk.ui.system.bottom
import by.funduk.ui.system.taskBoard
import web.cssom.px
import web.cssom.*

import by.funduk.services.TaskService
import by.funduk.services.kApiAddress
import by.funduk.ui.TaskView
import by.funduk.ui.general.Counts
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*

var taskList: List<TaskView> = listOf()

val scope = MainScope()

val client = HttpClient(Js) {
    install(ContentNegotiation) {
        json()
    }
}

private val Archive = FC<Props> { _ ->
    div {
        css {
            padding = 0.px
            margin = 0.px
        }

        var tasks_views by useState<List<TaskView>>(listOf())

        scope.launch {
            val res: List<TaskView> = client.post("$kApiAddress/task_views") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(TaskViewBatch(Counts.TaskViewBatchSize, 0))
            }
        }

        // body
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                marginTop = Sizes.NavHeight + Sizes.MuchBiggerMargin
                marginBottom = Sizes.MuchBiggerMargin
                gap = Sizes.RegularGap
                width = 100.pct
                minHeight = 100.vh - Sizes.NavHeight - 2 * Sizes.MuchBiggerMargin
            }

            // taskboard

            taskBoard {
                tasks = tasks_views
            }

        }

        bottom {

        }

        nav {
        }
    }
}

fun start() {
    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(Archive.create())
}

fun main() {
    start()
}