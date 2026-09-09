package hu.tb.network.repository

import hu.tb.network.ApiResult
import hu.tb.network.apiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.delete

class ProfileRepository(private val httpClient: HttpClient) {

    suspend fun deleteProfile(): ApiResult<Unit> =
        apiCall<Unit> {
            httpClient.delete("/profile")
        }
}
