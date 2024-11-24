package by.funduk.ui.system

import by.funduk.model.Rank
import by.funduk.model.Status
import by.funduk.model.Tag
import by.funduk.ui.general.*
import by.funduk.ui.*

import react.*
import react.dom.html.ReactHTML.div
import emotion.react.*
import web.cssom.*
import web.html.HTMLDivElement

// taskboard + filter

external interface TaskBoardProps : Props {
    var tasks: List<TaskView>
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
            if (props.tasks.isEmpty()) {
                div {
                    css {
                        display = Display.flex
                        height = Sizes.TaskViewHeight
                        width = Sizes.TaskViewWidth
                        justifyContent = JustifyContent.center
                    }

                    textFrame {
                        size = by.funduk.ui.general.Font.Size.Big
                        color = Pallete.Web.SecondPlan
                        text = "No tasks were found"
                    }

                }
            } else {
                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        justifyContent = JustifyContent.start
                    }
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

                        for (el in props.tasks) {
                            taskView {
                                task = el
                            }

                        }

                    }
                }
            }

            // filter column
            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    justifyContent = JustifyContent.start
                }

                filterField {
                }
            }

        }

        // load more
        if (props.tasks.isNotEmpty()) {
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
}