package hu.tb.dashboard.domain

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class FreeSession(
    val coachId: String,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
)