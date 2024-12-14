package by.funduk.ui.system

import by.funduk.api.TasksApi
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

import kotlinx.coroutines.*

// taskboard + filter

external interface TaskBoardProps : Props {
    var scope: CoroutineScope
}

val taskBoard = FC<TaskBoardProps> { props ->
    div {
        var tasks by useState<List<TaskView>>(listOf())

        useEffectOnce {
            props.scope.launch {
                tasks = TasksApi.getTaskViews(Counts.TaskViewBatchSize, 0)
            }
        }
        css {
            display = Display.flex
            flexDirection = FlexDirection.column

            width = Sizes.TaskViewWidth + Sizes.RegularGap + Sizes.Filter.Width
            alignItems = AlignItems.center
            gap = Sizes.RegularGap
        }

        val content = useRef<HTMLDivElement>(null)

        div {
            ref = content
            css {
                width = 100.pct
                display = Display.flex
                flexDirection = FlexDirection.row
                gap = Sizes.BigMargin
            }

            // taskboard
            if (tasks.isEmpty()) {
                div {
                    css {
                        display = Display.flex
                        height = Sizes.TaskViewHeight
                        width = Sizes.TaskViewWidth
                        justifyContent = JustifyContent.center
                    }

                    textFrame {
                        size = Sizes.Font.Big
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

                        for (el in tasks) {
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

        val load_more = useRef<web.html.HTMLDivElement>(null)

        // load more
        div {
            css {
                display = Display.flex
                height = Sizes.LoadMoreButtonHeight
                width = Sizes.LoadMoreButtonWidth
                borderRadius = Sizes.BoxBorderRadius
                boxShadow = Common.Shadow
                justifyContent = JustifyContent.center
                cursor = Cursor.pointer
            }

            ref = load_more

            textFrame {
                text = "Load more"
            }

            onClick = {
                load_more.current?.attributeStyleMap?.set("visibility", "hidden")

                props.scope.launch {
                    val batch = TasksApi.getTaskViews(Counts.TaskViewBatchSize, tasks.size)
                    tasks = tasks + batch
                    if (batch.size == Counts.TaskViewBatchSize) {
                        load_more.current?.attributeStyleMap?.set("visibility", "visible")
                    }
                }
            }
        }

    }
}