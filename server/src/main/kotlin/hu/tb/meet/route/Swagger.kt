package hu.tb.meet.route

import io.ktor.http.ContentType
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.Routing
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.routingRoot

fun Routing.swagger() {
    swaggerUI("/swagger") {
        info = OpenApiInfo(title = "Endpoints", version = "1.1.2")
        source = OpenApiDocSource.Routing(ContentType.Application.Json) {
            routingRoot.descendants()
        }
    }
}