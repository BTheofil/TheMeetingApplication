package hu.tb.network

import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException
import java.io.IOException

internal inline fun safeCall(
    execute: () -> HttpResponse
): ApiResult<HttpResponse> {
    val response = try {
        execute()
    } catch (e: CancellationException) {
        throw e
    } catch (_: IOException) {
        return ApiResult.Fail(DataError.NO_INTERNET)
    } catch (e: Throwable) {
        e.printStackTrace()
        return ApiResult.Fail(DataError.UNKNOWN)
    }

    return ApiResult.Ok(response)
}
