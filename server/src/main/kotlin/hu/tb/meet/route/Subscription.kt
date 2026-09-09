package hu.tb.meet.route

import hu.tb.meet.data.repository.SubscriptionRepository
import hu.tb.meet.domain.error.ApiError
import hu.tb.meet.domain.error.fail
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.RequestReceive
import hu.tb.meet.domain.receive.ResolveReceive
import hu.tb.meet.route.helper.requireAccount
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.subscription() {

    val subscriptionRepository by inject<SubscriptionRepository>()

    authenticate("auth-jwt") {
        post("/requestToCoach") {
            val (username, _) = requireAccount(AccountType.NORMAL)

            val requestReceive = call.receive<RequestReceive>()

            subscriptionRepository.request(username, requestReceive.coachId)
                ?: fail(ApiError.SUBSCRIPTION_REQUEST_FAILED)

            call.respond(HttpStatusCode.OK)
        }

        post("/showPendingRequests") {
            val (username, _) = requireAccount(AccountType.COACH)

            val results = subscriptionRepository.pendingRequests(username)

            call.respond(HttpStatusCode.OK, results)
        }

        post("/acceptRequest") {
            val (username, _) = requireAccount(AccountType.COACH)

            val receive = call.receive<ResolveReceive>()

            if (!subscriptionRepository.accept(coachUsername = username, normalId = receive.normalId)) {
                fail(ApiError.REQUEST_NOT_FOUND)
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/rejectRequest") {
            val (username, _) = requireAccount(AccountType.COACH)

            val receive = call.receive<ResolveReceive>()

            if (!subscriptionRepository.reject(coachUsername = username, normalId = receive.normalId)) {
                fail(ApiError.REQUEST_NOT_FOUND)
            }

            call.respond(HttpStatusCode.OK)
        }

        get("/myCoaches") {
            val (username, _) = requireAccount(AccountType.NORMAL)

            val coaches = subscriptionRepository.myCoaches(username)

            call.respond(HttpStatusCode.OK, coaches)
        }
    }
}
