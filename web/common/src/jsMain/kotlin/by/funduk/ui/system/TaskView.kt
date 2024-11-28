package by.funduk.ui.system

import by.funduk.ui.*
import by.funduk.ui.general.*
import by.funduk.ui.general.Font

import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.a
import emotion.react.*
import web.cssom.*

external interface TaskViewProps : Props {
    var task: TaskView
}

val taskView = FC<TaskViewProps> { props ->
    a {
        href = "task/${props.task.id}"
        css {
            textDecoration = TextDecoration.solid
            display = Display.flex
            flexDirection = FlexDirection.row
            backgroundColor = Pallete.Web.Light
            borderRadius = Sizes.BoxBorderRadius
            overflow = Overflow.clip
            padding = 0.px;
            width = Sizes.TaskViewWidth
            height = Sizes.TaskViewHeight
            cursor = Cursor.pointer
        }

        //left
        div {
            css {
                width = 50.pct
                height = 100.pct
                display = Display.flex
                flexDirection = FlexDirection.row

                val back = props.task.rank?.let { colorOfRank[it] } ?: NamedColor.transparent
                background = back
            }

            val tc = props.task.rank?.let { Pallete.Web.LightText } ?: Pallete.Web.DarkText

            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    justifyContent = JustifyContent.center
                    alignItems = AlignItems.start
                    height = 100.pct
                }

                textFrame {
                    text = props.task.name
                    color = tc
                    margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin)
                }

                textFrame {
                    text = props.task.id.toString()
                    color = tc
                    size = Sizes.Font.Small
                    margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin)
                }

            }

            div {
                css {
                    width = 100.pct
                }
            }

            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.center
                }

                textFrame {
                    text = props.task.rank?.toString() ?: ""
                    color = tc
                    size = Sizes.Font.Small
                }

            }


        }

        //right
        div {
            css {
                width = 50.pct
                height = 100.pct
                display = Display.flex
                padding = 0.px
                flexDirection = FlexDirection.rowReverse
            }

            div {
                css {
                    width = Sizes.TaskViewStatusWidth
                    height = 100.pct
                    display = Display.flex
                    margin = 0.px

                    justifyContent = JustifyContent.center
                    alignItems = AlignItems.center

                    val back = props.task.userStatus?.let { colorOfStatus[it] } ?: NamedColor.transparent
                    background = back
                }

                val tc = props.task.userStatus?.let { Pallete.Web.LightText } ?: Pallete.Web.DarkText

                textFrame {
                    text = props.task.solvedCount.toString()
                    color = tc
                }
            }

            println(props.task.tags)
            //tag list
            staticTagBoard {
                direction = FlexDirection.column
                padding = 0.5 * (Sizes.TaskViewHeight - Counts.UI.NumberOfTagLinesInTaskView * Sizes.RegularTagHeight)
                tags = props.task.tags
            }
        }
    }
}