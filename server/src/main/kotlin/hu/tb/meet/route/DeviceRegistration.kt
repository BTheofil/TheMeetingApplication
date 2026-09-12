package hu.tb.meet.route

import hu.tb.meet.data.repository.DeviceFidRepository
import hu.tb.meet.domain.error.ApiError
import hu.tb.meet.domain.error.fail
import hu.tb.meet.domain.receive.DeviceFidReceive
import hu.tb.meet.route.helper.requireAccount
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.deviceRegistration() {

    val deviceFidRepository by inject<DeviceFidRepository>()

    authenticate("auth-jwt") {
        post("/registerDeviceFid") {
            val (username, type) = requireAccount()
            val fid = receiveDeviceFid()

            if (!deviceFidRepository.register(type, username, fid)) {
                fail(ApiError.PROFILE_NOT_FOUND)
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/unregisterDeviceFid") {
            val (username, type) = requireAccount()
            val fid = receiveDeviceFid()

            if (!deviceFidRepository.unregister(type, username, fid)) {
                fail(ApiError.PROFILE_NOT_FOUND)
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}

private suspend fun RoutingContext.receiveDeviceFid(): String {
    val fid = call.receive<DeviceFidReceive>().fid.trim()

    if (fid.isEmpty() || fid.length > 512) {
        fail(ApiError.MALFORMED_BODY)
    }

    return fid
}
