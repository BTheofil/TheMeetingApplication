package hu.tb.network

internal sealed interface ApiResult<out T> {
    data class Ok<T>(val body: T) : ApiResult<T>
    data class Fail(val dataError: DataError) : ApiResult<Nothing>
}
