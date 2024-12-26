package by.funduk.ui

import by.funduk.api.*
import web.dom.document
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.textarea
import react.dom.html.ReactHTML.input

import emotion.react.*

import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import web.cssom.px
import web.cssom.*

import by.funduk.model.*
import by.funduk.ui.api.InitPage
import by.funduk.ui.api.logIn
import by.funduk.ui.general.*
import by.funduk.ui.system.*
import io.ktor.client.call.*
import io.ktor.http.*
import js.promise.Promise
import kotlinx.coroutines.*
import org.w3c.dom.events.Event
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import web.html.HTMLTextAreaElement
import web.html.HTMLDivElement
import web.html.HTMLInputElement
import web.html.InputType
import web.http.RequestMethod
import kotlin.js.json
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

var mainScope = MainScope()

fun IsValidNickname(nickname: String): Boolean {
    val nicknameRegex = "^[a-zA-Z0-9](?!.*__)[a-zA-Z0-9_]{1,18}[a-zA-Z0-9]$".toRegex()
    return nickname.matches(nicknameRegex)
}

fun isValidPassword(password: String): Boolean {
    // Проверяем длину пароля
    if (password.length < 8) return false

    // Регулярное выражение для проверки пароля:
    // Должен содержать:
    // - хотя бы одну букву в верхнем регистре
    // - хотя бы одну букву в нижнем регистре
    // - хотя бы одну цифру
    // - хотя бы один специальный символ
    // - не содержать пробелов
    val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$".toRegex()

    return password.matches(passwordRegex)
}

private val LogInPage = FC<Props> { props ->
    div {
        css {
            padding = 0.px
            margin = 0.px
        }

        // body
        div {
            var isLoginField by useState(true)
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                justifyContent = JustifyContent.center
                marginTop = Sizes.Nav.Height + Sizes.MuchBiggerMargin
                marginBottom = Sizes.MuchBiggerMargin
                gap = Sizes.RegularGap
                width = 100.pct
                minHeight = 100.vh - Sizes.Nav.Height - 2 * Sizes.MuchBiggerMargin
            }

            // log in
            div {
                var loginNotification by useState("")
                var canLogIn by useState(true)
                val nicknameInput = useRef<HTMLInputElement>(null)
                val passwordInput = useRef<HTMLInputElement>(null)
                css {
                    if (!isLoginField) {
                        visibility = Visibility.hidden
                        height = 0.px
                    } else {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        boxShadow = Common.Shadow
                        padding = Sizes.RegularMargin
                        width = Sizes.Login.Width
                        borderRadius = Sizes.BoxBorderRadius
                        gap = Sizes.RegularMargin
                    }
                }

                // head
                div {
                    css {
                        display = Display.flex
                        gap = Sizes.SmallGap
                        width = 100.pct
                    }

                    textFrame {
                        text = "Log in"
                        bold = true
                        size = Sizes.Font.Big
                    }

                    //space
                    div {
                        css {
                            width = 100.pct
                        }
                    }

                    textFrame {
                        text = loginNotification
                        color = Pallete.Status.WA
                    }
                }

                inputField {
                    ref = nicknameInput
                    placeholder = "login"
                }

                inputField {
                    ref = passwordInput
                    placeholder = "password"
                }

                //buttons
                div {
                    css {
                        display = Display.flex
                        justifyContent = JustifyContent.center
                        alignItems = AlignItems.center
                        gap = Sizes.SmallGap
                        width = 100.pct
                    }
                    commonButton {
                        text = if (canLogIn) "log in" else "processing..."
                        onClick = { e ->
                            val nick = nicknameInput.current?.value
                            val password = passwordInput.current?.value

                            if (nick != null && password != null) {
                                if (canLogIn) {
                                    canLogIn = false
                                    //log in
                                    println(document.cookie)

                                    mainScope.launch {
                                        val token = AuthenticationApi.logIn(nick, password)
                                        if (token == null) {
                                            loginNotification = "invalid login or password"
                                        } else {
                                            AuthenticationApi.me(token).let {
                                                if (it.status == HttpStatusCode.OK) {
                                                    window.location.href = "/user/${it.body<Int>()}"
                                                } else {
                                                    loginNotification = "internal error"
                                                }
                                            }
                                        }
                                        canLogIn = true
                                    }
                                }
                            }
                        }
                    }

                    div {
                        onClick = {
                            nicknameInput.current?.value = ""
                            passwordInput.current?.value = ""
                            loginNotification = ""
                            isLoginField = false
                        }
                        css {
                            display = Display.flex
                            justifyContent = JustifyContent.center
                            alignItems = AlignItems.center
                            cursor = Cursor.pointer
                        }
                        textFrame {
                            text = "register"
                            color = Pallete.Web.SecondPlan
                        }
                    }
                }
            }

            // register
            div {
                var registerNotification by useState("")
                var canRegister by useState(true)
                val nicknameInput = useRef<HTMLInputElement>(null)
                val passwordInput = useRef<HTMLInputElement>(null)
                val repeatPasswordInput = useRef<HTMLInputElement>(null)
                css {
                    if (isLoginField) {
                        visibility = Visibility.hidden
                        height = 0.px
                    } else {
                        position = Position.relative
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        boxShadow = Common.Shadow
                        padding = Sizes.RegularMargin
                        width = Sizes.Login.Width
                        borderRadius = Sizes.BoxBorderRadius
                        gap = Sizes.RegularMargin
                    }
                }

                // head
                div {
                    css {
                        display = Display.flex
                        gap = Sizes.SmallGap
                        width = 100.pct
                    }

                    textFrame {
                        text = "Register"
                        bold = true
                        size = Sizes.Font.Big
                    }

                    //space
                    div {
                        css {
                            width = 100.pct
                        }
                    }

                    textFrame {
                        text = registerNotification
                        color = Pallete.Status.WA
                    }
                }

                inputField {
                    ref = nicknameInput
                    placeholder = "login"
                }

                inputField {
                    ref = passwordInput
                    placeholder = "password"
                }

                inputField {
                    ref = repeatPasswordInput
                    placeholder = "repeat password"
                }

                //buttons
                div {
                    css {
                        display = Display.flex
                        justifyContent = JustifyContent.center
                        alignItems = AlignItems.center
                        gap = Sizes.SmallGap
                        width = 100.pct
                    }

                    div {
                        onClick = {
                            nicknameInput.current?.value = ""
                            passwordInput.current?.value = ""
                            repeatPasswordInput.current?.value = ""
                            registerNotification = ""
                            isLoginField = true
                        }
                        css {
                            display = Display.flex
                            justifyContent = JustifyContent.center
                            alignItems = AlignItems.center
                            cursor = Cursor.pointer
                        }
                        textFrame {
                            text = "log in"
                            color = Pallete.Web.SecondPlan
                        }
                    }

                    commonButton {
                        text = if (canRegister) "register" else "processing..."
                        onClick = { e ->
                            val nick = nicknameInput.current?.value
                            val pass = passwordInput.current?.value
                            val rep = repeatPasswordInput.current?.value

                            if (nick != null && pass != null && rep != null) {
                                if (canRegister) {
                                    canRegister = false
                                    if (!IsValidNickname(nick) || !isValidPassword(pass) || pass != rep) {
                                        registerNotification = "nickname/password is in wrong format"
                                        canRegister = true
                                    } else {
                                        //register
                                        mainScope.launch {
                                            val res = AuthenticationApi.register(nick, pass)

                                            when (res.status) {
                                                HttpStatusCode.Created -> {
                                                    isLoginField = true
                                                }

                                                HttpStatusCode.Conflict -> {
                                                    registerNotification = "this user already exists"
                                                }

                                                else -> {
                                                    registerNotification = "unknown error, try again later"
                                                }
                                            }
                                            canRegister = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //rules
                div {
                    css {
                        if (isLoginField) {
                            visibility = Visibility.hidden
                            height = 0.px
                        } else {
                            position = Position.absolute
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            padding = Sizes.RegularMargin
                            gap = Sizes.SmallGap
                            left = Sizes.Login.Width + Sizes.RegularMargin + Sizes.RegularGap
                            top = -Sizes.RegularMargin
                        }
                    }

                    // nick
                    div {
                        css {
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            alignItems = AlignItems.start
                        }
                        textFrame {
                            text = "Nickname"
                            margins = listOf(0.px, Sizes.MicroGap, 0.px, 0.px)
                            bold = true
                        }

                        textFrame {
                            text = "Длинна от 3 до 20"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                        textFrame {
                            text = "Никнейм не может начинаться или заканчиваться символом _"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                        textFrame {
                            text = "Никнейм не может содержать двойное подчеркивание __"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                        textFrame {
                            text = "Разрешены только буквы латиницы, цифры и символ _"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                    }

                    // pass
                    div {
                        css {
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            alignItems = AlignItems.start
                        }

                        textFrame {
                            text = "Password"
                            margins = listOf(0.px, Sizes.MicroGap, 0.px, 0.px)
                            bold = true
                        }

                        textFrame {
                            text = "Минимальная длина пароля — 8 символов"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                        textFrame {
                            text = "Хотя бы одну буква в верхнем регистре"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                        textFrame {
                            text = "Хотя бы одну буква в нижнем регистре"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                        textFrame {
                            text = "Хотя бы одна цифра"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                        textFrame {
                            text = "Хотя бы один специальный символ (!@#\$%^&*())"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                        textFrame {
                            text = "Не допускаются пробелы"
                            margins = listOf(0.px, 0.px, 0.px, 0.px)
                            size = Sizes.Font.Small
                        }
                    }


                }
            }

        }


    }

    bottom {

    }

    nav {
        user = UserButtonType.LogIn
    }
}

fun start() {
    mainScope.launch {
        InitPage()
    }
    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(LogInPage.create())
}

fun main() {
    start()
}