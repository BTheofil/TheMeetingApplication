package hu.tb.meet.route

import hu.tb.meet.data.repository.RequestRepository
import hu.tb.meet.domain.error.ApiError
import hu.tb.meet.domain.error.fail
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.RequestReceive
import hu.tb.meet.domain.receive.ResolveReceive
import hu.tb.meet.domain.send.SubscriptionStatus
import hu.tb.meet.notification.PushNotifier
import hu.tb.meet.route.helper.requireAccount
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun Route.request() {

    val requestRepository by inject<RequestRepository>()
    val pushNotifier by inject<PushNotifier>()

    authenticate("auth-jwt") {
        post("/requestToCoach") {
            val (username, _) = requireAccount(AccountType.NORMAL)

            val requestReceive = call.receive<RequestReceive>()

            val outcome = requestRepository.request(username, requestReceive.coachId)
                ?: fail(ApiError.SUBSCRIPTION_REQUEST_FAILED)

            val isNewRequest =
                outcome.previousStatus == SubscriptionStatus.INIT &&
                        outcome.currentStatus == SubscriptionStatus.PENDING

            if (isNewRequest) {
                call.application.launch(Dispatchers.IO) {
                    pushNotifier.coachRequested(
                        coachId = requestReceive.coachId,
                        normalId = outcome.normalId,
                        normalUsername = username
                    )
                }
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/showPendingRequests") {
            val (username, _) = requireAccount(AccountType.COACH)

            val results = requestRepository.pendingRequests(username)

            call.respond(HttpStatusCode.OK, results)
        }

        post("/acceptRequest") {
            val (username, _) = requireAccount(AccountType.COACH)

            val receive = call.receive<ResolveReceive>()

            if (!requestRepository.accept(coachUsername = username, normalId = receive.normalId)) {
                fail(ApiError.REQUEST_NOT_FOUND)
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/rejectRequest") {
            val (username, _) = requireAccount(AccountType.COACH)

            val receive = call.receive<ResolveReceive>()

            if (!requestRepository.reject(coachUsername = username, normalId = receive.normalId)) {
                fail(ApiError.REQUEST_NOT_FOUND)
            }

            call.respond(HttpStatusCode.OK)
        }

        get("/myCoaches") {
            val (username, _) = requireAccount(AccountType.NORMAL)

            val coaches = requestRepository.myCoaches(username)

            call.respond(HttpStatusCode.OK, coaches)
        }
    }
}
