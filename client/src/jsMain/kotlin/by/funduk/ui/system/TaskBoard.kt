package by.funduk.ui.system

import by.funduk.model.Rank
import by.funduk.model.Status
import by.funduk.model.Tag
import by.funduk.ui.general.*

import react.*
import react.dom.html.ReactHTML.div
import emotion.react.*
import web.cssom.*
import web.events.Event
import web.events.EventType
import web.events.addEventListener
import web.html.HTMLDivElement
import web.window.window

// taskboard + filter

external interface TaskBoardProps : Props {
}

val taskBoard = FC<TaskBoardProps> { props ->
    div {

        css {
            display = Display.flex
            flexDirection = FlexDirection.column

            width = Sizes.TaskViewWidth + Sizes.RegularGap + Sizes.Filter.Width
            alignItems = AlignItems.center
            gap = Sizes.RegularGap
        }

        val content = useRef<HTMLDivElement>(null);
        // content

        div {
            ref = content
            css {
                width = 100.pct
                display = Display.flex
                flexDirection = FlexDirection.row
                gap = Sizes.BigMargin
            }

            // taskboard

            val filter = useRef<HTMLDivElement>(null)
            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    justifyContent = JustifyContent.start
                    justifyItems = JustifyItems.start;
                    alignItems = AlignItems.center
                    gap = Sizes.RegularMargin
                    boxShadow = Common.Shadow
                    background = Pallete.Web.LightShadow
                    borderRadius = Sizes.BoxBorderRadius
                }
                taskView {
                    name = "Example task"
                    ind = 102
                    rank = Rank.Cow
                    numberOfSolvers = 1
                    status = Status.OK
                    tags = listOf(Tag.DP, Tag.FFT, Tag.Greedy)
                }

                taskView {
                    name = "Example task"
                    ind = 100323
                    rank = Rank.MediumRare
                    numberOfSolvers = 1
                    status = Status.WA
                    tags = listOf(Tag.DP, Tag.FFT, Tag.Greedy)
                }

                taskView {
                    name = "Example task"
                    ind = 123402
                    rank = Rank.Calf
                    numberOfSolvers = 1
                    status = Status.TL
                    tags = listOf(
                        Tag.DP,
                        Tag.FFT,
                        Tag.Greedy
                    )
                }

                taskView {
                    name = "Example task"
                    ind = 123402
                    rank = Rank.MediumWell
                    numberOfSolvers = 1
                    status = Status.Fail
                    tags = listOf(
                        Tag.DP,
                        Tag.FFT,
                        Tag.Greedy
                    )
                }

                taskView {
                    name = "Example task"
                    ind = 123402
                    rank = Rank.WellDone
                    numberOfSolvers = 123
                    tags = listOf(
                        Tag.DP,
                        Tag.BinSearch,
                        Tag.Greedy
                    )
                }
            }

            onLoad = {
                val rect = content.current?.getBoundingClientRect()
                println("structing filter")
                // filter
                filterField {
                    ref = filter

                    top = (rect?.top ?: 0.0)
                    right = (rect?.right ?: 0.0)
                }
            }
        }

        // load more
        div {
            css {
                display = Display.flex
                height = Sizes.LoadMoreButtonHeight
                width = Sizes.LoadMoreButtonWidth
                borderRadius = Sizes.BoxBorderRadius
                boxShadow = Common.Shadow
                justifyContent = JustifyContent.center
            }

            textFrame {
                text = "Load more"
            }
        }

    }
}