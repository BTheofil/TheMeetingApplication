package hu.tb.search.domain

import androidx.compose.runtime.Stable

@Stable
data class Coach(
    val id: String,
    val name: String,
    val status: Status
) {
    val initials: String
        get() = name.split(' ')
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
}

enum class Status {
    INIT, PENDING, ADDED
}