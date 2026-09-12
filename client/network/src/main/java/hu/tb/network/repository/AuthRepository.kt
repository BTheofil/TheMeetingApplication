package hu.tb.network.repository

import hu.tb.data.auth.AuthResponse
import hu.tb.data.auth.AuthSend
import hu.tb.data.notification.DeviceFidSend
import hu.tb.domain.AuthForm
import hu.tb.domain.AuthMode
import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.FidProvider
import hu.tb.network.apiCall
import hu.tb.network.map
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRepository(
    private val httpClient: HttpClient,
    private val fidProvider: FidProvider,
) {
    suspend fun authenticate(
        mode: AuthMode,
        form: AuthForm,
    ): ApiResult<String> {
        val path = when (mode) {
            AuthMode.LOGIN -> "/login"
            AuthMode.REGISTER -> "/register"
        }

        return apiCall<AuthResponse> {
            httpClient.post(path) {
                setBody(
                    AuthSend(
                        username = form.username,
                        password = form.password,
                        type = form.type
                    )
                )
            }
        }.map { it.token }
    }

    suspend fun registerDeviceFid(): ApiResult<Unit> {
        val fid = fidProvider.getFirebaseFid() ?: return ApiResult.Fail(DataError.UNKNOWN)

        return apiCall<Unit> {
            httpClient.post("/registerDeviceFid") {
                setBody(DeviceFidSend(fid = fid))
            }
        }
    }
}
