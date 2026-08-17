package hu.tb.meet.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.status() {
    get("/ping") {
        call.respondText(text = "Pong! from meet server", status = HttpStatusCode.OK)
    }
}