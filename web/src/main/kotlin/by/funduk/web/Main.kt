package by.funduk.web

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import kotlinx.html.*

fun main() {
    embeddedServer(Netty, port = 8081, module = Application::module).start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title("Funduk")
                }
                body {
                    a("/archive") {
                        +"Archive"
                    }
                }
            }
        }

        staticResources("/archive", "static/archive")
        staticResources("/task/{index}", "static/task")
        staticResources("/authenticate", "static/login")
        staticResources("/user/{name}", "static/user")
    }
}