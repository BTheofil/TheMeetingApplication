package hu.tb.network

import hu.tb.data.ErrorResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException
import java.io.IOException

suspend inline fun <reified T : Any> apiCall(execute: () -> HttpResponse): ApiResult<T> {
    val response = try {
        execute()
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        e.printStackTrace()
        return ApiResult.Fail(DataError.NO_INTERNET)
    } catch (e: Exception) {
        e.printStackTrace()
        return ApiResult.Fail(DataError.UNKNOWN)
    }

    if (response.status.value !in 200..299) return response.toFail()

    if (T::class == Unit::class) return ApiResult.Ok(Unit as T)

    return try {
        ApiResult.Ok(response.body<T>())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        e.printStackTrace()
        ApiResult.Fail(DataError.UNKNOWN)
    }
}

@PublishedApi
internal suspend fun HttpResponse.toFail(): ApiResult.Fail =
    ApiResult.Fail(
        dataError = when (status.value) {
            401 -> DataError.UNAUTHORIZED
            409 -> DataError.CONFLICT
            else -> DataError.UNKNOWN
        },
        serverMessage = serverMessage()
    )

private suspend fun HttpResponse.serverMessage(): String? =
    try {
        body<ErrorResponse>().message.takeIf { it.isNotBlank() }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
