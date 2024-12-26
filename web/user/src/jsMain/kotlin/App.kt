package by.funduk.ui

import by.funduk.api.AuthenticationApi
import web.dom.document
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.canvas

import emotion.react.*

import kotlinx.browser.window
import web.cssom.px
import web.cssom.*

import by.funduk.api.TasksApi
import by.funduk.api.SubmissionApi
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
import kotlin.time.Duration.Companion.seconds

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

    useEffectOnce {
        mainScope.launch {
            InitPage()
            userId = withAuth { access ->
                AuthenticationApi.me(access)
            }.let {
                if (it.status == HttpStatusCode.OK) {
                    it.body<Int>()
                } else {
                    null
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
                    if (userId == null) {
                        visibility = Visibility.hidden
                        height = 0.px
                    } else {
                        display = Display.flex
                        justifyContent = JustifyContent.center
                    }
                }
                textFrame {
                    text = if (userId == null) "We do not recognize this user" else "Loading user info..."
                    size = Sizes.Font.Big
                    color = Pallete.Web.SecondPlan
                }
            }

            // user exists
            div {
                css {
                    if (userId == null) {
                        visibility = Visibility.hidden
                        height = 0.px
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

                    canvas {
                        css {
                            minWidth = Sizes.Avatar.Width
                            minHeight = Sizes.Avatar.Height
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            justifyContent = JustifyContent.end
                            alignContent = AlignContent.center
                            borderRadius = Sizes.BoxBorderRadius
                            boxShadow = Common.Shadow
                        }

                        //nickname
                        textFrame {
                            text = "N3vajnoKto"
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
                    }

                    div {
                        css {
                            display = Display.flex
                            cursor = Cursor.pointer
                        }
                        textFrame {
                            text = "submissions"
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
                    }
                }

                // content
                div {
                    when (contentType) {
                        ContentType.General -> {

                        }

                        ContentType.Submissions -> {

                        }

                        ContentType.Tasks -> {

                        }
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
            (currentUserId == userId) -> UserButtonType.UserPage
            else -> UserButtonType.LogOut
        }
        id = userId
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