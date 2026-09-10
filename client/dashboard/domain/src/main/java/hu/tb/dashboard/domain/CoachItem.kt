package hu.tb.dashboard.domain

import androidx.compose.runtime.Immutable

@Immutable
data class CoachItem(
    val id: String,
    val name: String,
) {
    val initials: String
        get() = name.split(' ')
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
}