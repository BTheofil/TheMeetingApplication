package hu.tb.presentation.util

import hu.tb.network.DataError

fun DataError.asUiText(): String = when (this) {
    DataError.NO_INTERNET ->
        "No internet connection. Check your network and try again."

    DataError.UNAUTHORIZED ->
        "Invalid username, password, or account type."

    DataError.CONFLICT ->
        "That username is already taken."

    DataError.UNKNOWN ->
        "Something went wrong. Please try again."
}
