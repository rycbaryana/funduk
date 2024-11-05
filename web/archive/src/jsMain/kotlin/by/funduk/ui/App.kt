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

private val Archive = FC<Props> { props ->
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
                marginTop = Sizes.NavHeight + Sizes.MuchBiggerMargin
                marginBottom = Sizes.MuchBiggerMargin
                gap = Sizes.RegularGap
                width = 100.pct
                minHeight = 100.vh - Sizes.NavHeight - 2 * Sizes.MuchBiggerMargin
            }

            // taskboard

            taskBoard {

            }

        }

        bottom {

        }

        var scrolledDownState: Int by useState(0)

        nav {
            scrolledDown = scrolledDownState
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