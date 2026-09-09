package hu.tb.network.repository

import hu.tb.data.notification.PendingRequestResponse
import hu.tb.data.notification.ResolveRequestSend
import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.asText
import hu.tb.network.safeCall
import hu.tb.notification.domain.PendingRequestsResult
import hu.tb.notification.domain.RequestDecision
import hu.tb.notification.domain.RequestNotification
import hu.tb.notification.domain.ResolveRequestResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class NotificationRepository(
    private val httpClient: HttpClient
) {
    suspend fun getPendingRequests(): PendingRequestsResult {
        val result = safeCall {
            httpClient.post("/showPendingRequests")
        }

        return when (result) {
            is ApiResult.Fail -> PendingRequestsResult(errorMessage = result.dataError.asText())

            is ApiResult.Ok -> result.httpResponse.toPendingRequestsResult()
        }
    }

    suspend fun resolveRequest(
        decision: RequestDecision,
        requestId: String
    ): ResolveRequestResult {
        val normalId = requestId.toIntOrNull()
            ?: return ResolveRequestResult(errorMessage = DataError.UNKNOWN.asText())

        val result = safeCall {
            httpClient.post(decision.endpoint()) {
                setBody(ResolveRequestSend(normalId))
            }
        }

        return when (result) {
            is ApiResult.Fail -> ResolveRequestResult(errorMessage = result.dataError.asText())

            is ApiResult.Ok -> when (result.httpResponse.status) {
                HttpStatusCode.OK -> ResolveRequestResult(isResolved = true)

                HttpStatusCode.Unauthorized ->
                    ResolveRequestResult(errorMessage = DataError.UNAUTHORIZED.asText())

                else -> ResolveRequestResult(errorMessage = DataError.UNKNOWN.asText())
            }
        }
    }

    private fun RequestDecision.endpoint(): String =
        when (this) {
            RequestDecision.ACCEPT -> "/acceptRequest"
            RequestDecision.REJECT -> "/rejectRequest"
        }

    private suspend fun HttpResponse.toPendingRequestsResult(): PendingRequestsResult =
        when (status.value) {
            in 200..299 -> try {
                PendingRequestsResult(
                    requests = body<List<PendingRequestResponse>>().map { it.toDomain() }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                PendingRequestsResult(errorMessage = DataError.UNKNOWN.asText())
            }

            401 -> PendingRequestsResult(errorMessage = DataError.UNAUTHORIZED.asText())

            else -> PendingRequestsResult(errorMessage = DataError.UNKNOWN.asText())
        }

    private fun PendingRequestResponse.toDomain(): RequestNotification =
        RequestNotification(
            id = normalId,
            senderName = normalName
        )
}
