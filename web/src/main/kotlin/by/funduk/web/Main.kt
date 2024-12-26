package by.funduk.web

import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.*
import java.io.*
import java.security.KeyStore

fun main() {
    embeddedServer(Netty, applicationEngineEnvironment{
        module {
            module()
        }
        val keyStoreFile = File("build/keystore.jks")
        val keyStore = buildKeyStore {
            certificate("key") {
                password = "1234Debil$"
                domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
            }
        }

        keyStore.saveToFile(keyStoreFile, "123456")

        sslConnector(
            keyStore = keyStore,
            keyAlias = "key",
            keyStorePassword = { "123456".toCharArray() },
            privateKeyPassword = { "1234Debil$".toCharArray() }) {
            port = 8443
            keyStorePath = keyStoreFile
        }
    }).start(wait = true)
}


fun Application.module() {
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