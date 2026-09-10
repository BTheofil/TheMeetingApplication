package hu.tb.network.repository

import hu.tb.data.auth.AuthResponse
import hu.tb.data.auth.AuthSend
import hu.tb.domain.AuthForm
import hu.tb.domain.AuthMode
import hu.tb.network.ApiResult
import hu.tb.network.apiCall
import hu.tb.network.map
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRepository(
    private val httpClient: HttpClient
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
}
