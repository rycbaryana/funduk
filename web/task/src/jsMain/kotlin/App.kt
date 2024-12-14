package by.funduk.ui

import web.dom.document
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.textarea
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.select
import kotlinx.datetime.*

import emotion.react.*

import kotlinx.browser.window
import web.cssom.px
import web.cssom.*

import by.funduk.api.TasksApi
import by.funduk.api.SubmissionApi
import by.funduk.model.*
import by.funduk.ui.general.*
import by.funduk.ui.system.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.events.Event
import org.w3c.files.Blob
import react.dom.events.EventHandler
import web.events.ProgressEvent
import org.w3c.files.FileReader
import web.html.HTMLTextAreaElement
import web.html.HTMLDivElement
import web.html.HTMLInputElement
import web.html.InputType

var task_id = 0

var main_scope = MainScope()

var previousSubmission = ""

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

val langItemList = GetItemList<Language>()

private val TaskPage = FC<Props> { props ->
    val refLang = useRef<HTMLDivElement>(null)
    val list = useRef<HTMLDivElement>(null)
    val fileInput = useRef<HTMLInputElement>(null)
    val editor = useRef<HTMLTextAreaElement>(null)

    var canSubmit by useState(true)
    var language by useState(Language.entries[0])
    var fileButtonName by useState<String?>(null)

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
                    val loaded_task = TasksApi.getTask(task_id)

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

                            //submission section
                            div {
                                css {
                                    display = Display.flex
                                    position = Position.relative
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
                                        alignContent = AlignContent.start
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
                                                refLang.current?.focus()
                                                e.stopPropagation()
                                            }

                                            textFrame {
                                                text = language.text
                                                margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin)
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

                                            input {
                                                css {
                                                    position = Position.fixed
                                                    visibility = Visibility.hidden
                                                }

                                                ref = fileInput
                                                accept = language.extensions.map { ".$it" }.joinToString(" ")
                                                type = InputType.file
                                                multiple = false

                                                onChange = { e ->
                                                    val file = e.target.files?.get(0)

                                                    if (file != null) {
//                                                        val reader = FileReader()
//                                                        reader.onload = { e ->
//                                                            val data = (reader.result as String?)
//                                                        }
//                                                        reader.readAsArrayBuffer(file)


                                                    }

                                                    fileButtonName = file?.name ?: "load file"
                                                }
                                            }

                                            onClick = {
                                                fileInput.current?.click()
                                            }

                                            textFrame {
                                                text = fileButtonName ?: "load file"
                                                margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin)
                                            }
                                        }
                                    }

                                    //text field
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
                                            ref = editor
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

                                    // submit button
                                    div {
                                        css {
                                            display = Display.flex
                                            flexDirection = FlexDirection.row
                                        }
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
                                                // submition

                                                if (canSubmit) {
                                                    canSubmit = false
                                                    val code = editor.current?.value ?: ""
                                                    val submission = RawSubmission(
                                                        task_id,
                                                        1,
                                                        editor.current?.value ?: "",
                                                        language
                                                    )
                                                    main_scope.launch {
                                                        val id = SubmissionApi.submit(submission)
                                                        println(id)
                                                        canSubmit = true
                                                    }
                                                }
                                            }

                                            textFrame {
                                                text = if (canSubmit) "submit" else "sending..."
                                                margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin)
                                            }
                                        }
                                    }
                                }

                                // lang list context menu
                                div {
                                    css {
                                        position = Position.absolute
                                        top = 4 * Sizes.RegularMargin + Sizes.Button.Height + Sizes.Font.Regular
                                        left = Sizes.RegularMargin
                                        visibility = Visibility.hidden
                                        outline = 0.px
                                    }

                                    tabIndex = 0

                                    ref = refLang

                                    langItemList {
                                        ref = list
                                        items = enumValues<Language>().toList().map { Pair(it.text, it) }
                                        onClickCallback = { item ->
                                            language = item
                                            refLang.current?.style?.visibility = "hidden"
                                        }
                                    }

                                    onFocus = { e ->
                                        list.current?.focus()
                                        e.stopPropagation()
                                    }

                                    onClick = { e ->
                                        e.stopPropagation()
                                    }
                                }
                            }


                            //submission section
                            div {
                                css {
                                    display = Display.flex
                                    position = Position.relative
                                    flexDirection = FlexDirection.column
                                    borderRadius = Sizes.BoxBorderRadius
                                    backgroundColor = Pallete.Web.Light
                                    alignItems = AlignItems.start
                                    width = Sizes.TaskStatement.Width
                                }

                                // box name
                                textFrame {
                                    text = "submitions"
                                    bold = true
                                }

                                //submit content
                                div {
                                    css {
                                        display = Display.flex
                                        padding = Sizes.RegularMargin
                                        paddingTop = 0.px
                                        width = 100.pct
                                    }

                                    submissionTable {
                                        width = 100.pct - 2 * Sizes.RegularMargin
                                        submissions = listOf(
                                            SubmissionView(
                                                113424,
                                                1,
                                                1,
                                                "task name",
                                                "user name",
                                                LocalDateTime(2023, 12, 12, 15, 30),
                                                Language.Python3,
                                                TestInfo(
                                                    Status.OK, 99, 123, 1243232454
                                                )
                                            ),

                                            SubmissionView(
                                                10133424,
                                                1,
                                                1,
                                                "task name",
                                                "user name",
                                                LocalDateTime(2024, 12, 12, 15, 30),
                                                Language.CPP23_GCC14,
                                                TestInfo(
                                                    Status.WA, 12, 1423, 134345645
                                                )
                                            ),

                                            SubmissionView(
                                                1133424,
                                                1,
                                                1,
                                                "task name",
                                                "user name",
                                                LocalDateTime(2024, 12, 12, 5, 30),
                                                Language.CPP23_GCC14,
                                                TestInfo(
                                                    Status.Running, 243, 1423, 134
                                                )
                                            ),

                                            SubmissionView(
                                                1133424,
                                                1,
                                                1,
                                                "task name",
                                                "user name",
                                                LocalDateTime(2024, 12, 12, 5, 30),
                                                Language.CPP23_GCC14,
                                                TestInfo(
                                                    Status.Fail, 99, 1423, 134
                                                )
                                            )
                                        )
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

        val hideContext: (Event?) -> Unit = {
            refLang.current?.style?.visibility = "hidden"
        }

        useEffectWithCleanup {
            window.addEventListener("click", hideContext) // Добавляем обработчик клика
            onCleanup {
                window.removeEventListener("click", hideContext)
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