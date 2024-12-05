package by.funduk.ui

import web.dom.document
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.textarea
import react.dom.html.ReactHTML.select

import emotion.react.*

import kotlinx.browser.window
import web.cssom.px
import web.cssom.*

import by.funduk.api.TasksApi
import by.funduk.model.Lang
import by.funduk.model.Task
import by.funduk.ui.general.*
import by.funduk.ui.system.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import web.html.HTMLTextAreaElement
import web.html.HTMLDivElement

var task_id = 0

var main_scope = MainScope()

external interface TextFieldWithNameProps : Props {
    var name: String
    var text: String
}

val textFieldWithName = FC<TextFieldWithNameProps> { props ->
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            borderRadius = Sizes.BoxBorderRadius
            backgroundColor = Pallete.Web.Light
            alignItems = AlignItems.start
            width = Sizes.TaskStatement.Width
        }

        // box name
        textFrame {
            text = props.name
            bold = true
        }

        div {
            css {
                margin = Sizes.RegularMargin
                wordWrap = WordWrap.breakWord
                whiteSpace = WhiteSpace.preWrap
            }

            +props.text
        }
    }
}

external interface SampleFieldProps : Props {
    var name: String
    var text: String
}

val sampleField = FC<SampleFieldProps> { props ->
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            borderRadius = Sizes.BoxBorderRadius
            backgroundColor = Pallete.Web.SecondLight
            alignItems = AlignItems.start
            width = 50.pct
        }

        // box name
        div {
            css {
                position = Position.relative
                display = Display.inlineBlock
                width = 100.pct
            }

            textFrame {
                text = props.name
                bold = true
            }

            div {
                css {
                    position = Position.absolute
                    display = Display.inlineBlock
                    margin = Sizes.RegularMargin
                    right = 0.px
                    cursor = Cursor.pointer
                }
                +"copy"

                onClick = {
                    window.navigator.clipboard.writeText(props.text);
                }
            }
        }

        div {
            css {
                margin = Sizes.RegularMargin
                wordWrap = WordWrap.breakWord
                whiteSpace = WhiteSpace.preWrap
            }
            +props.text
        }
    }
}

val langItemList = GetItemList<Lang>();

private val TaskPage = FC<Props> { props ->
    div {
        css {
            padding = 0.px
            margin = 0.px
        }

        val refLang = useRef<HTMLDivElement>(null)

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
                    val loaded_task = TasksApi.getTask(task_id)

                    println(loaded_task)
                    if (loaded_task == null) {
                        text_on_null = "We do not recognize this task"
                    }
                    document.title = loaded_task?.name ?: "unknown task"
                    task = loaded_task
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
                                        width = Sizes.RegularLimitsWidth
                                    }

                                    textFrame {
                                        color = text_color
                                        text = "time:"
                                        size = Sizes.Font.Small
                                        margins = listOf(0.px, 0.px, 0.px, 0.px)
                                    }

                                    textFrame {
                                        color = text_color
                                        text = "memory:"
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
                                        width = Sizes.RegularLimitsWidth
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

                        //content
                        div {
                            css {
                                display = Display.flex
                                flexDirection = FlexDirection.column
                                gap = Sizes.RegularGap
                                background = Pallete.Web.LightShadow
                                borderRadius = Sizes.BoxBorderRadius
                                boxShadow = Common.Shadow
                            }

                            textFieldWithName {
                                name = "statement"
                                text = task?.statement ?: "No statement."
                            }

                            textFieldWithName {
                                name = "input"
                                text = "You are given an only integer x."
                            }

                            textFieldWithName {
                                name = "output"
                                text = "Print x ^ 3 in one string."
                            }

                            //samples

                            div {
                                css {
                                    display = Display.flex
                                    flexDirection = FlexDirection.column
                                    borderRadius = Sizes.BoxBorderRadius
                                    backgroundColor = Pallete.Web.Light
                                    alignItems = AlignItems.start
                                    width = Sizes.TaskStatement.Width
                                }

                                // box name
                                textFrame {
                                    text = "samples"
                                    bold = true
                                }

                                div {
                                    css {
                                        display = Display.flex
                                        flexDirection = FlexDirection.column
                                        margin = Sizes.RegularMargin
                                        gap = Sizes.RegularGap
                                        width = 100.pct - Sizes.RegularGap
                                    }

                                    //sample row
                                    div {
                                        css {
                                            display = Display.flex
                                            flexDirection = FlexDirection.row
                                            width = 100.pct
                                            gap = Sizes.RegularGap
                                        }

                                        // input
                                        sampleField {
                                            name = "input"
                                            text = "3"
                                        }

                                        // output
                                        sampleField {
                                            name = "output"
                                            text = "27"
                                        }
                                    }

                                }
                            }

                            textFieldWithName {
                                name = "note"
                                text = "Optional field with notes and clues."
                            }

                            div {
                                css {
                                    display = Display.flex
                                    flexDirection = FlexDirection.column
                                    borderRadius = Sizes.BoxBorderRadius
                                    backgroundColor = Pallete.Web.Light
                                    alignItems = AlignItems.start
                                    width = Sizes.TaskStatement.Width
                                }

                                // box name
                                textFrame {
                                    text = "submit"
                                    bold = true
                                }

                                //submit content
                                div {
                                    css {
                                        display = Display.flex
                                        flexDirection = FlexDirection.column
                                        gap = Sizes.SmallMargin
                                        margin = Sizes.RegularMargin
                                        marginTop = 0.px
                                        width = 100.pct
                                        height = 100.pct
                                    }

                                    // head
                                    div {
                                        css {
                                            display = Display.flex
                                            gap = Sizes.SmallGap
                                            width = 100.pct
                                        }

                                        //select lang
                                        div {
                                            css {
                                                display = Display.flex
                                                justifyContent = JustifyContent.center
                                                alignItems = AlignItems.center

                                                borderRadius = Sizes.Button.Height / 2
                                                height = Sizes.Button.Height

                                                background = Pallete.Web.Light
                                                border = Sizes.Button.Border
                                                borderStyle = LineStyle.solid
                                                borderColor = Pallete.Web.Button.Border
                                                cursor = Cursor.pointer
                                            }

                                            onClick = { e ->
                                                refLang.current?.style?.visibility = "visible"
                                                refLang.current?.style?.left = "${e.pageX - window.scrollX.toInt()}px"
                                                refLang.current?.style?.top = "${e.pageY- window.scrollY.toInt()}px"
                                            }

                                            textFrame {
                                                text = Lang.entries[0].lang
                                                margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin);
                                            }

                                        }

                                        // load file
                                        div {
                                            css {
                                                display = Display.flex
                                                justifyContent = JustifyContent.center
                                                alignItems = AlignItems.center

                                                borderRadius = Sizes.Button.Height / 2
                                                height = Sizes.Button.Height

                                                background = Pallete.Web.Light
                                                border = Sizes.Button.Border
                                                borderStyle = LineStyle.solid
                                                borderColor = Pallete.Web.Button.Border
                                                cursor = Cursor.pointer
                                            }

                                            textFrame {
                                                text = "load file"
                                                margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin);
                                            }
                                        }
                                    }

                                    //text field
                                    div {

                                    }

                                    div {
                                        css {
                                            display = Display.flex
                                            flexDirection = FlexDirection.column
                                            borderRadius = Sizes.BoxBorderRadius
                                            backgroundColor = Pallete.Web.SecondLight
                                            alignItems = AlignItems.start
                                            width = Sizes.TaskStatement.Width - 4 * Sizes.RegularMargin
                                            padding = Sizes.RegularMargin
                                        }

                                        textarea {
                                            className = ClassName("editor")
                                            autoCapitalize = "off"
                                            autoCorrect = "off"
                                            wrap = "off"
                                            spellCheck = false

                                            onChange = { event ->
                                                run {
                                                    val target = event.target as HTMLTextAreaElement
                                                    target.style.height = "auto"
                                                    target.style.height = "${
                                                        max(
                                                            min(target.scrollHeight.px, Sizes.Editor.MaxHeight),
                                                            Sizes.Editor.StandardHeight
                                                        )
                                                    }"
                                                }
                                            }
                                        }

                                    }
                                }
                            }

                        }
                    }
                    // info board
                    div {

                        css {
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            justifyContent = JustifyContent.start
                        }

                        div {
                            css {
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
                }
            }


        }

        bottom {

        }

        nav {
        }

        // lang list context menu
        div {
            css {
                position = Position.fixed
                visibility = Visibility.hidden
            }

            ref = refLang
            langItemList {
                items = enumValues<Lang>().toList().map { Pair(it.lang, it) }
            }
        }
    }
}

fun start() {
    task_id = window.location.href.split("/").lastOrNull()?.toIntOrNull() ?: 2

    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(TaskPage.create())
}

fun main() {
    start()
}