package hu.tb.network

sealed interface ApiResult<out T> {
    data class Ok<T>(val data: T) : ApiResult<T>
    data class Fail(
        val dataError: DataError,
        val serverMessage: String? = null
    ) : ApiResult<Nothing> {
        val formatErrorMessage: String get() = serverMessage ?: dataError.asText()
    }
}

inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Ok -> ApiResult.Ok(transform(data))
    is ApiResult.Fail -> this
}

inline fun <T, R> ApiResult<T>.fold(
    success: (T) -> R,
    fail: (ApiResult.Fail) -> R
): R = when (this) {
    is ApiResult.Ok -> success(data)
    is ApiResult.Fail -> fail(this)
}
