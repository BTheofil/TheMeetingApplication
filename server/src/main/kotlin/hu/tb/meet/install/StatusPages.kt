package hu.tb.meet.install

import hu.tb.meet.domain.error.ApiError
import hu.tb.meet.domain.error.ApiException
import hu.tb.meet.domain.send.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respond
import kotlinx.serialization.SerializationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            call.respondError(cause.error)
        }
        exception<BadRequestException> { call, cause ->
            call.respondError(ApiError.MALFORMED_BODY, detail = cause.message)
        }
        exception<SerializationException> { call, cause ->
            call.respondError(ApiError.MALFORMED_BODY, detail = cause.message)
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled error on ${call.request.httpMethod.value} ${call.request.path()}", cause)
            call.respondError(ApiError.INTERNAL)
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondError(ApiError.ROUTE_NOT_FOUND)
        }
    }
}

suspend fun ApplicationCall.respondError(
    error: ApiError,
    detail: String? = null,
    cause: Throwable? = null
) {
    if (detail != null || cause != null) {
        application.log.warn("${error.name} on ${request.httpMethod.value} ${request.path()}: ${detail.orEmpty()}", cause)
    }
    respond(error.status, ErrorResponse(error.message))
}
