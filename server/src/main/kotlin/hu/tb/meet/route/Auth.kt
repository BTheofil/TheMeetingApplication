package hu.tb.meet.route

import hu.tb.meet.domain.get.LoginReceive
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.auth() {
    post("/login") {
        val loginInfo = call.receive<LoginReceive>()


    }
}