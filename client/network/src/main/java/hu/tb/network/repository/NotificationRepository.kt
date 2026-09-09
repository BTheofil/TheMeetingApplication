package hu.tb.network.repository

import hu.tb.data.notification.RequestAnswer
import hu.tb.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.post

class NotificationRepository(
    private val httpClient: HttpClient
) {
    suspend fun getNotifications() {

    }

    suspend fun sendRequest(requestAnswer: RequestAnswer, normalProfilId: Int) {
        val result = safeCall {
            httpClient.post("") {

            }
        }
    }
}