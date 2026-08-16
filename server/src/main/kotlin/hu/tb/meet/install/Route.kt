package hu.tb.meet.install

import io.ktor.server.application.Application
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val status: String)

fun Application.setupRoute() {
    routing {
        get("/health") {
            call.respond(HealthResponse("UP"))
        }

        swaggerUI(path = "swagger")
    }
}
