package by.funduk.ui

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

import by.funduk.api.TasksApi
import by.funduk.model.Task
import by.funduk.ui.general.*
import by.funduk.ui.system.staticTagBoard
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

var task_id = 0

var main_scope = MainScope()

private val TaskPage = FC<Props> { props ->
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

            var text_on_null by useState("Loading task...")
            var task by useState<Task?>(null)

            useEffectOnce {
                main_scope.launch {
                    task = TasksApi.getTask(task_id)
                    if (task == null) {
                        text_on_null = "We do not recognize this task"
                    } else {
                        document.title = task?.name ?: "unknown task"
                    }
                }
            }

            if (task == null) {
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
                        text = text_on_null
                    }

                }
            } else {

                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.row
                        gap = Sizes.RegularGap
                    }
                    // task
                    div {
                        css {
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            gap = Sizes.RegularGap
                            alignItems = AlignItems.center
                        }

                        // title
                        div {
                           css {
                               display = Display.flex
                               flexDirection = FlexDirection.column
                               borderRadius = Sizes.BoxBorderRadius
                               backgroundColor = colorOfRank[task?.rank] ?: NamedColor.transparent
                               alignItems = AlignItems.center
                               padding = Sizes.RegularMargin
                               boxShadow = Common.Shadow
                           }

                            val text_color = if (task?.rank == null) Pallete.Web.DarkText else Pallete.Web.LightText
                            textFrame {
                                color = text_color
                                text = task?.name ?: "Unnamed"
                                size = Sizes.Font.Big
                                margins = listOf(0.px, 0.px, 0.px, 0.px)
                            }
                            textFrame {
                                color = text_color
                                text = (task?.id.toString() ?: "not indexed")
                                size = Sizes.Font.Small
                                margins = listOf(0.px, Sizes.SmallMargin, 0.px, 0.px)
                            }

                            div {
                                css {
                                    display = Display.flex
                                    flexDirection = FlexDirection.row
                                    alignContent = AlignContent.center
                                    gap = Sizes.SmallGap
                                }

                                // left
                                div {
                                    css {
                                        display = Display.flex
                                        flexDirection = FlexDirection.column
                                        alignItems = AlignItems.end
                                        width = 50.pct
                                    }

                                    textFrame {
                                        color = text_color
                                        text = "time limit:"
                                        size = Sizes.Font.Small
                                        margins = listOf(0.px, 0.px, 0.px, 0.px)
                                    }

                                    textFrame {
                                        color = text_color
                                        text = "memory limit:"
                                        size = Sizes.Font.Small
                                        margins = listOf(0.px, 0.px, 0.px, 0.px)
                                    }
                                }

                                // right
                                div {
                                    css {
                                        display = Display.flex
                                        flexDirection = FlexDirection.column
                                        alignItems = AlignItems.start
                                        width = 50.pct
                                    }

                                    textFrame {
                                        color = text_color
                                        text = "${2}s"
                                        size = Sizes.Font.Small
                                        margins = listOf(0.px, 0.px, 0.px, 0.px)
                                    }

                                    textFrame {
                                        color = text_color
                                        text = "${256}mb"
                                        size = Sizes.Font.Small
                                        margins = listOf(0.px, 0.px, 0.px, 0.px)
                                    }
                                }
                            }
                        }

                        //statement
                        div {
                            css {
                                display = Display.flex
                                flexDirection = FlexDirection.column
                                borderRadius = Sizes.BoxBorderRadius
                                backgroundColor = Pallete.Web.Light
                                boxShadow = Common.Shadow
                                alignItems = AlignItems.start
                                width = Sizes.TaskStatement.Width
                            }

                            // box name
                            textFrame {
                                text = "statement"
                                bold = true
                            }

                            div {
                                css {
                                    margin = Sizes.RegularMargin
                                }
                                +(task?.statement ?: "")
                            }
                        }
                    }
                    // info board
                    div {
                        css {
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            width = Sizes.TaskStatement.InfoWidth
                            alignItems = AlignItems.start
                            borderRadius = Sizes.BoxBorderRadius
                            backgroundColor = Pallete.Web.Light
                            boxShadow = Common.Shadow
                        }

                        textFrame {
                            text = "tags"
                            bold = true
                        }

                        staticTagBoard {
                            tags = (task?.tags ?: listOf())
                            direction = FlexDirection.row
                            align = AlignItems.start
                        }

                    }
                }

                textFrame {
                    text = task_id.toString()
                    size = Sizes.Font.Bigger
                    color = Pallete.Web.SecondPlan
                }
            }


        }

        bottom {

        }

        nav {
        }
    }
}

fun start() {
    task_id = window.location.href.split("/").lastOrNull()?.toIntOrNull() ?: -1

    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(TaskPage.create())
}

fun main() {
    start()
}