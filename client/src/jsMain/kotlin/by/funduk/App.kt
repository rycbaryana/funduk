import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import web.dom.document
import kotlinx.coroutines.*
import by.funduk.model.Task
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.input
import react.dom.server.rawRenderToString
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import io.ktor.serialization.kotlinx.json.*
import js.objects.jso
import js.promise.PromiseResult
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.ul
import react.router.RouterProvider
import react.router.dom.Link
import react.router.dom.createBrowserRouter
import react.router.useLoaderData
import react.router.useLocation
import remix.run.router.LoaderFunction

val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

val mainScope = MainScope()

val Tasks = FC<Props> { props ->
    var tasks: List<Task> by useState(listOf())
    var loading: Boolean by useState(false)
    useEffectOnce {
        mainScope.launch {
            loading = true
            tasks = client.get("/api/tasks").body()
            loading = false
        }
    }
    div {
        h1 { +"Tasks" }
        if (loading) {
            div {+"Fetching tasks..."}
        }
        ul {
            tasks.forEach {
                li {
                    Link {
                        to = "/tasks/${it.id}"
                        state = it
                        +"${it.id}. ${it.name}"
                    }
                }
            }
        }
    }
}

private val TaskLoader = LoaderFunction<Int> {
        args, _ -> PromiseResult(args.params["id"])
}

private val TaskPage = FC<Props> {
    val location = useLocation()
    val task: Task = location.state.unsafeCast<Task>()
    div {
        h1 {+"${task.id}. ${task.name}"}
        p {+task.statement}
    }
}

private val App = FC<Props> {
    val browserRouter = createBrowserRouter(
        routes = arrayOf(
            jso {
                path = "/"
                element = Tasks.create()
            },
            jso {
                path = "/tasks/:id"
                loader = TaskLoader
                element = TaskPage.create()
            }
        )
    )
    RouterProvider {
        router = browserRouter
    }
}

fun start() {
    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(App.create())
}

fun main() {
    start()
}