import by.funduk.ui.general.Pallete
import by.funduk.ui.general.Sizes
import by.funduk.ui.general.textFrame
import web.dom.document
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div

import emotion.react.*

import by.funduk.ui.system.nav
import by.funduk.ui.system.bottom
import by.funduk.ui.system.taskBoard
import kotlinx.browser.window
import web.cssom.px
import web.cssom.*

var task_id = 0

private val Task = FC<Props> { props ->
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

            textFrame {
                text = task_id.toString()
                size = by.funduk.ui.general.Font.Size.Bigger
                color = Pallete.Web.SecondPlan
            }


        }

        bottom {

        }

        nav {
        }
    }
}

fun start() {
    task_id = window.location.href.split("/").lastOrNull()?.toInt() ?: -1
    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(Task.create())
}

fun main() {
    start()
}