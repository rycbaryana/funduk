package by.funduk.ui

import by.funduk.api.AuthenticationApi
import by.funduk.ui.api.InitPage
import by.funduk.ui.api.withAuth
import by.funduk.ui.general.Sizes
import by.funduk.ui.system.*
import web.dom.document
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import io.ktor.http.*
import io.ktor.client.call.*
import emotion.react.*

import web.cssom.px
import web.cssom.*

import kotlinx.coroutines.*

val mainScope = MainScope()

private val Archive = FC<Props> { _ ->
    var userId by useState<Int?>(null)
    div {
        css {
            padding = 0.px
            margin = 0.px
        }

        useEffectOnce {
            InitPage()

            val id = withAuth { access ->
                AuthenticationApi.me(access)
            }.let {
                if (it.status == HttpStatusCode.OK) {
                    it.body<Int>()
                } else {
                    null
                }
            }

            userId = id
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
            user = when(userId) {
                null -> UserButtonType.LogIn
                else -> UserButtonType.UserPage
            }
            id = userId
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