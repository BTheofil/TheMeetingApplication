package hu.tb.network.repository

import hu.tb.data.auth.dto.AuthResponse
import hu.tb.data.auth.dto.AuthSend
import hu.tb.data.auth.dto.ErrorResponse
import hu.tb.domain.AuthForm
import hu.tb.domain.AuthMode
import hu.tb.domain.AuthResults
import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.asText
import hu.tb.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class AuthRepository(
    private val httpClient: HttpClient
) {
    suspend fun authenticate(
        mode: AuthMode,
        form: AuthForm,
    ): AuthResults {
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
            is ApiResult.Fail -> AuthResults(errorMessage = result.dataError.asText())
        }
    }

    private suspend fun HttpResponse.toDataError(): AuthResults =
        when (status.value) {
            in 200..299 -> try {
                val token = body<AuthResponse>().token
                AuthResults(token = token)
            } catch (e: Exception) {
                e.printStackTrace()
                AuthResults(errorMessage = DataError.UNKNOWN.asText())
            }

            401, 409 -> {
                val message = body<ErrorResponse>().message
                AuthResults(errorMessage = message)
            }

            else -> AuthResults(errorMessage = DataError.UNKNOWN.asText())
        }
}
