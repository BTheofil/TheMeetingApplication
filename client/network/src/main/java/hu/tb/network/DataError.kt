package hu.tb.network

enum class DataError {
    NO_INTERNET,
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    CONFLICT,
    SERVER_ERROR,
    UNKNOWN
}

fun DataError.asText(): String = when (this) {
    DataError.NO_INTERNET ->
        "No internet connection. Check your network and try again."

    DataError.BAD_REQUEST ->
        "The request was rejected. Check what you entered and try again."

    DataError.UNAUTHORIZED ->
        "Invalid credentials, or your session has expired. Please sign in again."

    DataError.FORBIDDEN ->
        "This action is not available for your account type."

    DataError.NOT_FOUND ->
        "We couldn't find what you were looking for."

    DataError.CONFLICT ->
        "That username is already taken."

    DataError.SERVER_ERROR ->
        "Something went wrong on our side. Please try again later."

    DataError.UNKNOWN ->
        "Something went wrong. Please try again."
}
