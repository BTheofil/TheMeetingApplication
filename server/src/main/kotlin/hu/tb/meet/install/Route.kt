package hu.tb.meet.install

import hu.tb.meet.route.auth
import hu.tb.meet.route.profile
import hu.tb.meet.route.search
import hu.tb.meet.route.status
import hu.tb.meet.route.subscription
import hu.tb.meet.route.swagger
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.setupRoute() {
    routing {
        status()
        swagger()
        auth()
        profile()
        search()
        subscription()
    }
}
