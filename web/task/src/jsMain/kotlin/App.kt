package by.funduk.ui

import by.funduk.api.AuthenticationApi
import web.dom.document
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.textarea
import react.dom.html.ReactHTML.input

import emotion.react.*

import kotlinx.browser.window
import web.cssom.px
import web.cssom.*

import by.funduk.api.TasksApi
import by.funduk.api.SubmissionApi
import by.funduk.api.SubmitRequest
import by.funduk.model.*
import by.funduk.ui.api.InitPage
import by.funduk.ui.api.withAuth
import by.funduk.ui.general.*
import by.funduk.ui.system.*
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.*
import org.w3c.dom.events.Event
import web.html.HTMLTextAreaElement
import web.html.HTMLDivElement
import web.html.HTMLInputElement
import web.html.InputType
import kotlin.math.min

var taskId = 0

var mainScope = MainScope()

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
                    window.navigator.clipboard.writeText(props.text)
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
    val refLanguageListMenu = useRef<HTMLDivElement>(null)
    val refLanguageList = useRef<HTMLDivElement>(null)
    val refFileInput = useRef<HTMLInputElement>(null)
    val refEditor = useRef<HTMLTextAreaElement>(null)

    val (submissionViews, setSubmissionViews) = useState<List<SubmissionView>>(mutableListOf())
    var userId by useState<Int?>(null)

    fun handleSubmit(view: SubmissionView) {
        setSubmissionViews {
            listOf(view).plus(it)
        }
    }

    fun handleStatusMessage(message: StatusMessage) {
        setSubmissionViews { list ->
            list.map {
                if (it.id == message.id) {
                    it.copy(testInfo = message.testInfo)
                } else {
                    it
                }
            }
        }
    }

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

            var textOnNull by useState("Loading task...")
            var task by useState<Task?>(null)

            // preparations
            useEffectOnce {
                mainScope.launch(Dispatchers.Main) {
                    val loaded_task = TasksApi.getTask(taskId).let {
                        if (it.status == HttpStatusCode.OK) {
                            it.body<Task>()
                        } else {
                            null
                        }
                    }
                    InitPage()

                    val id = withAuth { access ->
                        AuthenticationApi.me(access)
                    }.let {
                        if (it.status == HttpStatusCode.OK) {
                            it.body<Int>()
                        } else {
                            null
                        }
                    }

                    println(id)

                    userId = id

                    if (loaded_task == null) {
                        textOnNull = "We do not recognize this task"
                    } else {
                        if (id != null) {
                            setSubmissionViews(SubmissionApi.getSubmissionViews(taskId, id).let {
                                if (it.status == HttpStatusCode.OK) {
                                    it.body<List<SubmissionView>>()
                                } else {
                                    listOf()
                                }
                            })
                        }

                    }
                    document.title = loaded_task?.name ?: "unknown task"
                    task = loaded_task

                    mainScope.launch(Dispatchers.Main) {
                        SubmissionApi.connectToTaskWebSocket(taskId, id ?: 0) {
                            if (it is StatusMessage) {
                                handleStatusMessage(it)
                            }
                        }
                    }
                }
            }
            div {
                css {
                    display = Display.flex
                    justifyContent = JustifyContent.center
                }

                if (task == null) {
                    textFrame {
                        size = Sizes.Font.Big
                        color = Pallete.Web.SecondPlan
                        text = textOnNull
                    }
                }
            }

            div {
                css {
                    if (task != null) {
                        display = Display.flex
                    } else {
                        visibility = Visibility.hidden
                        maxHeight = 0.px
                        height = 0.px
                    }
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
                        key = "title"
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
                            text = task?.id.toString()
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
                        if (task?.samples != null) {
                            //samples
                            div {
                                key = "samples"
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

                                    for (sample in task!!.samples!!) {
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
                                                text = sample.input
                                            }

                                            // output
                                            sampleField {
                                                name = "output"
                                                text = sample.output
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (task?.notes != null) {
                            textFieldWithName {
                                name = "note"
                                text = task!!.notes!!
                            }
                        }


                        //submit + submissions
                        div {
                            css {
                                display = Display.flex
                                flexDirection = FlexDirection.columnReverse
                                gap = Sizes.BigMargin
                            }

                            //submission section
                            div {
                                var isShortened by useState(true)

                                css {
                                    if (submissionViews.isNotEmpty()) {
                                        display = Display.flex
                                    } else {
                                        visibility = Visibility.hidden
                                        maxHeight = 0.px
                                        height = 0.px
                                    }
                                    position = Position.relative
                                    flexDirection = FlexDirection.column
                                    borderRadius = Sizes.BoxBorderRadius
                                    backgroundColor = Pallete.Web.Light
                                    alignItems = AlignItems.start
                                    width = Sizes.TaskStatement.Width
                                }

                                // box name
                                textFrame {
                                    text = "submissions"
                                    bold = true
                                }

                                //submit content
                                div {
                                    css {
                                        display = Display.flex
                                        flexDirection = FlexDirection.column
                                        padding = Sizes.RegularMargin
                                        paddingTop = 0.px
                                        width = 100.pct
                                    }

                                    submissionTable {
                                        width = 100.pct - 2 * Sizes.RegularMargin
                                        submissions = if (isShortened) submissionViews.let {
                                            it.subList(
                                                0, min(it.size, Counts.UI.SubmissionTable.DefaultNumberOfSubmissions)
                                            )
                                        } else submissionViews
                                    }

                                    // all submissions button
                                    if (submissionViews.size > Counts.UI.SubmissionTable.DefaultNumberOfSubmissions) {
                                        div {
                                            css {
                                                display = Display.flex
                                                justifyContent = JustifyContent.center
                                            }
                                            div {
                                                css {
                                                    display = Display.inlineBlock
                                                    cursor = Cursor.pointer
                                                }
                                                textFrame {
                                                    text = if (isShortened) "all submissions" else "shorten"
                                                    size = Sizes.Font.Small
                                                    color = Pallete.Web.SecondPlan
                                                }
                                            }

                                            onClick = {
                                                isShortened = !isShortened
                                            }
                                        }
                                    }
                                }
                            }

                            // submit
                            div {
                                id = "submit"
                                css {
                                    if (userId == null) {
                                        visibility = Visibility.hidden
                                        height = 0.px
                                    } else {
                                        display = Display.flex
                                        position = Position.relative
                                        flexDirection = FlexDirection.column
                                        borderRadius = Sizes.BoxBorderRadius
                                        backgroundColor = Pallete.Web.Light
                                        alignItems = AlignItems.start
                                        width = Sizes.TaskStatement.Width
                                    }
                                }

                                // box name
                                textFrame {
                                    text = "submit"
                                    bold = true
                                }

                                //submit content
                                div {
                                    var canSubmit by useState(true)
                                    var fileButtonName by useState<String?>(null)
                                    var language by useState(Language.entries[0])
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
                                        commonButton {
                                            text = language.text
                                            onClick = { e ->
                                                refLanguageListMenu.current?.style?.visibility = "visible"
                                                refLanguageListMenu.current?.focus()
                                                e.stopPropagation()
                                            }
                                        }

                                        // load file
                                        input {
                                            css {
                                                position = Position.fixed
                                                visibility = Visibility.hidden
                                            }

                                            ref = refFileInput
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

                                        commonButton {
                                            text = fileButtonName ?: "load file"
                                            onClick = {
                                                refFileInput.current?.click()
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
                                            ref = refEditor
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

                                            // submission
                                            onClick = { e ->
                                                if (canSubmit) {
                                                    canSubmit = false
                                                    val submission = SubmitRequest(
                                                        taskId, refEditor.current?.value ?: "", language
                                                    )
                                                    mainScope.launch {
                                                        val id = withAuth { access ->
                                                            SubmissionApi.submit(access, submission)
                                                        }.let {
                                                            if (it.status != HttpStatusCode.OK) {
                                                                null
                                                            } else {
                                                                it.body<Int>()
                                                            }
                                                        }

                                                        if (id == null) {
                                                            // invalid submission
                                                        } else {
                                                            SubmissionApi.getSubmissionView(id).let {
                                                                if (it.status == HttpStatusCode.OK) {
                                                                    handleSubmit(it.body())
                                                                }
                                                            }
                                                        }
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

                                        ref = refLanguageListMenu

                                        langItemList {
                                            ref = refLanguageList
                                            items = enumValues<Language>().toList().map { Pair(it.text, it) }
                                            onClickCallback = { item ->
                                                language = item
                                                refLanguageListMenu.current?.style?.visibility = "hidden"
                                            }
                                        }

                                        onFocus = { e ->
                                            refLanguageList.current?.focus()
                                            e.stopPropagation()
                                        }

                                        onClick = { e ->
                                            e.stopPropagation()
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
        user = when (userId) {
            null -> UserButtonType.LogIn
            else -> UserButtonType.UserPage
        }
        id = userId
    }

    val hideContext: (Event?) -> Unit = {
        refLanguageListMenu.current?.style?.visibility = "hidden"
    }

    useEffectWithCleanup {
        window.addEventListener("click", hideContext) // Добавляем обработчик клика
        onCleanup {
            window.removeEventListener("click", hideContext)
        }
    }
}

fun start() {
    taskId = window.location.href.split("/").lastOrNull()?.toIntOrNull() ?: -1
    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(TaskPage.create())
}

fun main() {
    start()
}