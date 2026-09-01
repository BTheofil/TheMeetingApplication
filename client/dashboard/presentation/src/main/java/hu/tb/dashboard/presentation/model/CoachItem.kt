package hu.tb.dashboard.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class CoachItem(
    val id: String,
    val name: String,
    val openHourCount: Int
) {
    val initials: String
        get() = name.split(' ')
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
}
