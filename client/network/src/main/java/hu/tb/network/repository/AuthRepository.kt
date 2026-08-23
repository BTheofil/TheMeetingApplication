package hu.tb.network.repository

import hu.tb.data.auth.dto.AuthResponse
import hu.tb.data.auth.dto.AuthSend
import hu.tb.data.auth.dto.ErrorResponse
import hu.tb.domain.AuthForm
import hu.tb.domain.AuthMode
import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException

class AuthRepository(
    private val httpClient: HttpClient
) {
    suspend fun authenticate(
        mode: AuthMode,
        form: AuthForm,
    ): DataError? {
        val path = when (mode) {
            AuthMode.LOGIN -> "/login"
            AuthMode.REGISTER -> "/register"
        }

        val result = safeCall {
            httpClient.post(path) {
                setBody(
                    AuthSend(
                        username = form.username,
                        password = form.password,
                        type = form.type.value
                    )
                )
            }
        }

        return when (result) {
            is ApiResult.Ok -> result.body.toDataError()
            is ApiResult.Fail -> result.dataError
        }
    }

    private suspend fun HttpResponse.toDataError(): DataError? =
        when (status.value) {
            in 200..299 -> try {
                body<AuthResponse>()
                null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                e.printStackTrace()
                DataError.UNKNOWN
            }

            401, 409 -> body<ErrorResponse>().message
            else -> DataError.UNKNOWN
        }
}
