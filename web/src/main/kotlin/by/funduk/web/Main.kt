package by.funduk.web

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.response.*

fun main() {
    embeddedServer(Netty, port = 8081, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(HttpsRedirect) {
        sslPort = 8443
        permanentRedirect = true
    }
    routing {
        get("/") {
            call.respondRedirect("/archive")
        }
        staticResources("/archive", "static/archive")
        staticResources("/task/{index}", "static/task")
        staticResources("/authenticate", "static/login")
        staticResources("/user/{name}", "static/user")
    }
}