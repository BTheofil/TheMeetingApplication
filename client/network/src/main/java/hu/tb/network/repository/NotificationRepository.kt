package hu.tb.network.repository

import hu.tb.data.notification.PendingRequestResponse
import hu.tb.data.notification.ResolveRequestSend
import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.apiCall
import hu.tb.network.map
import hu.tb.notification.domain.RequestDecision
import hu.tb.notification.domain.RequestNotification
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class NotificationRepository(
    private val httpClient: HttpClient
) {
    suspend fun getPendingRequests(): ApiResult<List<RequestNotification>> =
        apiCall<List<PendingRequestResponse>> {
            httpClient.post("/showPendingRequests")
        }.map { requests -> requests.map { it.toDomain() } }

    suspend fun resolveRequest(
        decision: RequestDecision,
        requestId: String
    ): ApiResult<Unit> {
        val normalId = requestId.toIntOrNull()
            ?: return ApiResult.Fail(DataError.BAD_REQUEST)

        return apiCall<Unit> {
            httpClient.post(decision.endpoint()) {
                setBody(ResolveRequestSend(normalId))
            }
        }
    }

    private fun RequestDecision.endpoint(): String =
        when (this) {
            RequestDecision.ACCEPT -> "/acceptRequest"
            RequestDecision.REJECT -> "/rejectRequest"
        }

    private fun PendingRequestResponse.toDomain(): RequestNotification =
        RequestNotification(
            id = normalId,
            senderName = normalName
        )
}
