package hu.tb.network.repository

import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.asText
import hu.tb.network.safeCall
import hu.tb.profile.data.DeleteProfileResult
import io.ktor.client.HttpClient
import io.ktor.client.request.post

class ProfileRepository(private val httpClient: HttpClient) {

    suspend fun deleteProfile(token: String): DeleteProfileResult {
        val result = safeCall {
            httpClient.post("") {

            }
        }
        return when (result) {
            is ApiResult.Fail -> DeleteProfileResult(errorMessage = DataError.UNKNOWN.asText())
            is ApiResult.Ok<*> -> TODO()
        }
    }
}
