package hu.tb.meet.domain.error

import io.ktor.http.HttpStatusCode

enum class ApiError(val status: HttpStatusCode, val message: String) {
    INVALID_CREDENTIALS(HttpStatusCode.Unauthorized, "Invalid username or password"),
    USERNAME_TAKEN(HttpStatusCode.Conflict, "Username already taken"),
    INVALID_TOKEN(HttpStatusCode.Unauthorized, "Token is not valid or has expired"),
    WRONG_ACCOUNT_TYPE(HttpStatusCode.Forbidden, "This endpoint is not available for this account type"),
    PROFILE_NOT_FOUND(HttpStatusCode.NotFound, "Profile no longer exists"),
    SUBSCRIPTION_REQUEST_FAILED(HttpStatusCode.NotFound, "Failed to send request"),
    REQUEST_NOT_FOUND(HttpStatusCode.NotFound, "There is no pending request from this user"),
    MALFORMED_BODY(HttpStatusCode.BadRequest, "Malformed request body"),
    ROUTE_NOT_FOUND(HttpStatusCode.NotFound, "Page not found"),
    INTERNAL(HttpStatusCode.InternalServerError, "Something went wrong, please try again later"),
}

fun fail(error: ApiError): Nothing = throw ApiException(error)

data class ApiException(val error: ApiError, override val cause: Throwable? = null) :
    RuntimeException(error.message, cause)