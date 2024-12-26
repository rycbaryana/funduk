package by.funduk.ui

import by.funduk.ui.api.InitPage
import by.funduk.ui.general.Sizes
import by.funduk.ui.system.NavPage
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

val mainScope = MainScope()

private val Archive = FC<Props> { _ ->
    div {
        css {
            padding = 0.px
            margin = 0.px
        }

        // body
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                marginTop = Sizes.Nav.Height + Sizes.MuchBiggerMargin
                marginBottom = Sizes.MuchBiggerMargin
                gap = Sizes.RegularGap
                width = 100.pct
                minHeight = 100.vh - Sizes.Nav.Height - 2 * Sizes.MuchBiggerMargin
            }

            // taskboard
            taskBoard {
                scope = mainScope
            }
        }
        bottom {

        }
        nav {
            page = NavPage.Archive
        }
    }
}

fun start() {
    mainScope.launch {
        InitPage()
    }
    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(Archive.create())
}

fun main() {
    start()
}