package hu.tb.dashboard.domain

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class FreeSession(
    val id: Int,
    val coachId: Int,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
)