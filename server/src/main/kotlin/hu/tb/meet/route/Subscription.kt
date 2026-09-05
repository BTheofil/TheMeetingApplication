package hu.tb.meet.route

import hu.tb.meet.data.repository.SubscriptionRepository
import hu.tb.meet.domain.receive.ResolveReceive
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.RequestReceive
import hu.tb.meet.domain.send.ErrorResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.subscription() {

    val subscriptionRepository by inject<SubscriptionRepository>()

    authenticate("auth-jwt") {
        post("/requestToCoach") {
            val username = authenticatedUsername(AccountType.NORMAL) ?: return@post

            val requestReceive = call.receive<RequestReceive>()

            val status = subscriptionRepository.request(username, requestReceive.coachId)

            if (status != null) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respondText(text = "Failed to send request", status = HttpStatusCode.NotFound)
            }
        }

        post("/showPendingRequests") {
            val username = authenticatedUsername(AccountType.COACH) ?: return@post

            val results = subscriptionRepository.pendingRequests(username)

            call.respond(HttpStatusCode.OK, results)
        }

        post("/acceptRequest") {
            val username = authenticatedUsername(AccountType.COACH) ?: return@post

            val receive = call.receive<ResolveReceive>()

            val isSuccess = subscriptionRepository.accept(coachUsername = username, normalId = receive.normalId)

            call.respond(if (isSuccess) HttpStatusCode.OK else HttpStatusCode.BadRequest)
        }

        post("/rejectRequest") {
            val username = authenticatedUsername(AccountType.COACH) ?: return@post

            val receive = call.receive<ResolveReceive>()

            val isSuccess = subscriptionRepository.reject(coachUsername = username, normalId = receive.normalId)

            call.respond(if (isSuccess) HttpStatusCode.OK else HttpStatusCode.BadRequest)
        }

        get("/myCoaches") {
            val username = authenticatedUsername(AccountType.NORMAL) ?: return@get

            val coaches = subscriptionRepository.myCoaches(username)

            call.respond(HttpStatusCode.OK, coaches)
        }
    }
}

private suspend fun RoutingContext.authenticatedUsername(required: AccountType): String? {
    val payload = call.principal<JWTPrincipal>()?.payload
    val username = payload?.getClaim("username")?.asString()
    val accountType = payload?.getClaim("type")?.asString()
        ?.let { name -> AccountType.entries.find { it.name == name } }

    if (username == null || accountType != required) {
        call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token is not valid or has expired"))
        return null
    }

    return username
}
