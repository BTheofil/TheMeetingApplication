package hu.tb.network.repository

import hu.tb.data.profile.ProfileDeleteResponse
import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.asText
import hu.tb.network.safeCall
import hu.tb.profile.data.DeleteProfileResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class ProfileRepository(private val httpClient: HttpClient) {

    suspend fun deleteProfile(token: String): DeleteProfileResult {
        val result = safeCall {
            httpClient.delete("/profile") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        return when (result) {
            is ApiResult.Fail -> DeleteProfileResult(errorMessage = DataError.UNKNOWN.asText())
            is ApiResult.Ok -> {
                DeleteProfileResult(
                    isSuccess = result.httpResponse.status == HttpStatusCode.NoContent,
                    errorMessage = if (result.httpResponse.status == HttpStatusCode.Unauthorized) result.httpResponse.body<ProfileDeleteResponse>().message else null
                )
            }
        }
    }
}
