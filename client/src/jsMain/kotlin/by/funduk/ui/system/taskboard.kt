package by.funduk.ui.system

import by.funduk.general.*
import by.funduk.ui.general.*
import by.funduk.ui.general.Font

import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.a
import emotion.react.*
import web.cssom.*

// taskboard + filter

external interface TaskBoardProps : Props {
}

val taskBoard = FC<TaskBoardProps> { props ->
    div {

        css {
            display = Display.flex
            flexDirection = FlexDirection.column

            alignItems = AlignItems.center
            gap = Sizes.RegularGap
        }
        // content
        div {

            css {
                width = 100.pct
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.center
                gap = Sizes.BigMargin
            }

            // taskboard
            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    justifyContent = JustifyContent.start
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
                    tags = listOf(Tag.Dp, Tag.Fft, Tag.Greed)
                }

                taskView {
                    name = "Example task"
                    ind = 100323
                    rank = Rank.MediumRare
                    numberOfSolvers = 1
                    status = Status.WA
                    tags = listOf(Tag.Dp, Tag.Fft, Tag.Greed)
                }

                taskView {
                    name = "Example task"
                    ind = 123402
                    rank = Rank.Calf
                    numberOfSolvers = 1
                    status = Status.TL
                    tags = listOf(
                        Tag.Dp,
                        Tag.Fft,
                        Tag.Greed
                    )
                }
            }

            // filter
            filterField {

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