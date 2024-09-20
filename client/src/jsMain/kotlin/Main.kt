import kotlinx.browser.document
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

fun greeting(): String {
    return "Hello!"
}

suspend fun main() {
    delay(3.seconds)
    document.getElementById("root")?.innerHTML = "${greeting()} from precompiled!"
}