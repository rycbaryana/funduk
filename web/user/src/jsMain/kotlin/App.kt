package by.funduk.ui

import by.funduk.api.AuthenticationApi
import web.dom.document
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div

import emotion.react.*

import kotlinx.browser.window
import kotlinx.browser.localStorage
import web.cssom.px
import web.cssom.*

import by.funduk.api.UserApi
import by.funduk.api.kServerAddress
import by.funduk.model.*
import by.funduk.ui.api.InitPage
import by.funduk.ui.api.logOut
import by.funduk.ui.api.withAuth
import by.funduk.ui.general.*
import by.funduk.ui.system.*
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.*
import org.w3c.dom.events.Event
import web.html.HTMLDivElement
import web.html.HTMLInputElement

enum class ContentType {
    General,
    Submissions,
    Tasks
}

var mainScope = MainScope()

var currentUserId: Int? = null

private val UserPage = FC<Props> { props ->
    var userId by useState<Int?>(null)
    var contentType by useState<ContentType>(ContentType.General)
    var userInfo by useState<UserInfo?>(null)
    var currentUserInfo by useState<UserInfo?>(null)
    var submissionViews by useState<List<SubmissionView>>(listOf())

    var refInputTextMenu = useRef<HTMLDivElement>(null)
    var refInputText = useRef<HTMLInputElement>(null)

    useEffectOnce {
        mainScope.launch {
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

            userId = id

            if (id != null) {
                userInfo = UserApi.getUserInfo(id).let {
                    if (it.status == HttpStatusCode.OK) {
                        it.body<UserInfo>()
                    } else {
                        null
                    }
                }
            }

            val cur = currentUserId
            if (cur != null) {
                currentUserInfo = UserApi.getUserInfo(cur).let {
                    if (it.status == HttpStatusCode.OK) {
                        it.body<UserInfo>()
                    } else {
                        null
                    }
                }

                submissionViews = UserApi.getSubmissions(cur).let {
                    if (it.status == HttpStatusCode.OK) {
                        it.body<List<SubmissionView>>()
                    } else {
                        listOf()
                    }
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
                justifyContent = JustifyContent.start
                marginTop = Sizes.Nav.Height + Sizes.MuchBiggerMargin
                marginBottom = Sizes.MuchBiggerMargin
                gap = Sizes.RegularGap
                width = 100.pct
                minHeight = 100.vh - Sizes.Nav.Height - 2 * Sizes.MuchBiggerMargin
            }

            div {
                css {
                    if (currentUserInfo != null) {
                        visibility = Visibility.hidden
                        height = 0.px
                    } else {
                        display = Display.flex
                        justifyContent = JustifyContent.center
                    }
                }
                textFrame {
                    text =
                        if (currentUserId == null || currentUserInfo == null) "We do not recognize this user" else "Loading user info..."
                    size = Sizes.Font.Big
                    color = Pallete.Web.SecondPlan
                }
            }

            // user exists
            div {
                css {
                    if (currentUserInfo == null) {
                        visibility = Visibility.hidden
                        height = 0.px
                    } else {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        gap = Sizes.RegularGap
                    }
                }
                //avatar section
                div {
                    css {
                        display = Display.flex
                        justifyContent = JustifyContent.center
                        alignContent = AlignContent.center
                    }
                    // picture

                    div {
                        css {
                            minWidth = Sizes.Avatar.Width
                            minHeight = Sizes.Avatar.Height
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            justifyContent = JustifyContent.end
                            alignContent = AlignContent.center
                            borderRadius = Sizes.BoxBorderRadius
                            boxShadow = Common.Shadow
                            background = colorOfRank[currentUserInfo?.rank] ?: Pallete.Web.SecondPlan
                        }

                        //nickname
                        textFrame {
                            text = currentUserInfo?.realName ?: currentUserInfo?.login ?: "Unknown"
                            color = Pallete.Web.LightText
                            size = Sizes.Font.Big
                        }
                    }
                }

                // buttons
                div {
                    css {
                        display = Display.flex
                        justifyContent = JustifyContent.center
                        gap = Sizes.RegularGap
                    }

                    div {
                        css {
                            display = Display.flex
                            cursor = Cursor.pointer
                        }
                        textFrame {
                            text = "generals"
                        }

                        onClick = {
                            contentType = ContentType.General
                        }
                    }

                    div {
                        css {
                            display = Display.flex
                            cursor = Cursor.pointer
                        }
                        textFrame {
                            text = "submissions"
                        }

                        onClick = {
                            contentType = ContentType.Submissions
                        }
                    }

                    div {
                        css {
                            display = Display.flex
                            cursor = Cursor.pointer
                        }
                        textFrame {
                            text = "tasks"
                        }

                        val cnt = currentUserInfo?.solvedCount
                        if (cnt != null) {
                            div {
                                css {
                                    display = Display.flex
                                    justifyContent = JustifyContent.center
                                    alignContent = AlignContent.center
                                    alignItems = AlignItems.center
                                }
                                tagFrame {
                                    name = cnt.toString()
                                    back = colorOfRank[currentUserInfo?.rank]
                                }
                            }
                        }
                        onClick = {
                            contentType = ContentType.Tasks
                        }
                    }
                }

                // content
                div {
                    when (contentType) {
                        ContentType.General -> {
                            div {
                                css {
                                    display = Display.flex
                                    flexDirection = FlexDirection.row
                                    alignItems = AlignItems.start
                                    justifyContent = JustifyContent.center
                                    gap = Sizes.RegularGap
                                }

                                // first column
                                div {
                                    css {
                                        display = Display.flex
                                        flexDirection = FlexDirection.row
                                        alignItems = AlignItems.start
                                        justifyContent = JustifyContent.center
                                        gap = Sizes.RegularGap
                                    }
                                    // left
                                    div {
                                        css {
                                            display = Display.flex
                                            flexDirection = FlexDirection.column
                                            alignItems = AlignItems.start
                                            justifyContent = JustifyContent.start
                                        }
                                        textFrame {
                                            text = "Name"
                                            bold = true
                                        }

                                        textFrame {
                                            text = "Login"
                                            bold = true
                                        }

                                        textFrame {
                                            text = "Birth date"
                                            bold = true
                                        }
                                    }

                                    // right
                                    div {
                                        css {
                                            display = Display.flex
                                            flexDirection = FlexDirection.column
                                            alignItems = AlignItems.start
                                            justifyContent = JustifyContent.start
                                        }
                                        textFrame {
                                            text = currentUserInfo?.realName ?: "-"
                                        }

                                        textFrame {
                                            text = currentUserInfo?.login ?: "-"
                                        }

                                        textFrame {
                                            text = currentUserInfo?.birthDate?.toString() ?: "-"
                                        }

                                    }
                                }

                                // second column
                                div {
                                    css {
                                        display = Display.flex
                                        flexDirection = FlexDirection.row
                                        alignItems = AlignItems.start
                                        justifyContent = JustifyContent.center
                                        gap = Sizes.RegularGap
                                    }
                                    // left
                                    div {
                                        css {
                                            display = Display.flex
                                            flexDirection = FlexDirection.column
                                            alignItems = AlignItems.start
                                            justifyContent = JustifyContent.start
                                        }

                                        textFrame {
                                            text = "Rank"
                                            bold = true
                                        }

                                        textFrame {
                                            text = "Tasks solved"
                                            bold = true
                                        }
                                    }

                                    // right
                                    div {
                                        css {
                                            display = Display.flex
                                            flexDirection = FlexDirection.column
                                            alignItems = AlignItems.start
                                            justifyContent = JustifyContent.start
                                        }

                                        textFrame {
                                            text = currentUserInfo?.rank?.name ?: "-"
                                            color = colorOfRank[currentUserInfo?.rank] ?: Pallete.Web.DarkText
                                        }

                                        textFrame {
                                            text = currentUserInfo?.solvedCount?.toString() ?: "-"
                                        }


                                    }
                                }
                            }
                        }

                        ContentType.Submissions -> {
                            //submission section
                            if (submissionViews.isNotEmpty()) {
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
                                            submissions = submissionViews
                                        }
                                    }
                                }
                            } else {
                                div {
                                    css {
                                        display = Display.flex
                                        justifyContent = JustifyContent.center
                                    }
                                    textFrame {
                                        text = "There are no submissions"
                                        size = Sizes.Font.Big
                                        color = Pallete.Web.SecondPlan
                                    }
                                }
                            }
                        }

                        ContentType.Tasks -> {
                            div {
                                css {
                                    display = Display.flex
                                    justifyContent = JustifyContent.center
                                }
                                textFrame {
                                    text = "There are no tasks"
                                    size = Sizes.Font.Big
                                    color = Pallete.Web.SecondPlan
                                }
                            }
                        }
                    }
                }

                div {
                    ref = refInputTextMenu
                    css {
                        position = Position.fixed
                        height = Sizes.SearchHeight
                        visibility = Visibility.hidden
                        width = Sizes.OneLineTextFieldWidth
                    }

                    inputField {
                        ref = refInputText
                    }
                }
            }
        }

    }

    bottom {

    }

    nav {
        user = when {
            userId == null -> UserButtonType.LogIn
            (currentUserId == userId) -> UserButtonType.LogOut
            else -> UserButtonType.UserPage
        }
        id = userId
        userName = userInfo?.realName ?: userInfo?.login ?: "unknown"
        onLogOut = {
            mainScope.launch {
                AuthenticationApi.logOut()
                window.location.reload()
            }
            localStorage.removeItem("$kServerAddress/access")
        }
    }

    val hideContext: (Event?) -> Unit = {
        refInputTextMenu.current?.style?.visibility = "hidden"
    }

    useEffectWithCleanup {
        window.addEventListener("click", hideContext) // Добавляем обработчик клика
        onCleanup {
            window.removeEventListener("click", hideContext)
        }
    }
}

fun start() {
    currentUserId = window.location.href.split("/").lastOrNull()?.toIntOrNull()
    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(UserPage.create())
}

fun main() {
    start()
}