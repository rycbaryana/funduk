package by.funduk.ui

import by.funduk.api.TasksApi
import by.funduk.ui.general.Counts
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

import kotlinx.coroutines.*

val scope = MainScope()

private val Archive = FC<Props> { _ ->
    div {
        css {
            padding = 0.px
            margin = 0.px
        }

        var tasksViews by useState<List<TaskView>>(listOf())
        useEffectOnce {
            scope.launch {
                tasksViews = TasksApi.getTasksViews(Counts.TaskViewBatchSize, 0)
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
                tasks = tasksViews
            }
        }
        bottom
        nav
    }
}

fun start() {
    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(Archive.create())
}

fun main() {
    start()
}