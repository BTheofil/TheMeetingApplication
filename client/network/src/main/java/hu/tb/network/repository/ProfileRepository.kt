package hu.tb.network.repository

import hu.tb.data.notification.DeviceFidSend
import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.FidProvider
import hu.tb.network.apiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ProfileRepository(
    private val httpClient: HttpClient,
    private val fidProvider: FidProvider,
) {

    suspend fun deleteProfile(): ApiResult<Unit> =
        apiCall<Unit> {
            httpClient.delete("/profile")
        }

    suspend fun unregisterDeviceFid(): ApiResult<Unit> {
        val fid = fidProvider.getFirebaseFid() ?: return ApiResult.Fail(DataError.UNKNOWN)

        return apiCall<Unit> {
            httpClient.post("/unregisterDeviceFid") {
                setBody(DeviceFidSend(fid = fid))
            }
        }
    }
}
