package hu.tb.data.schedule

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SessionsSend(
    val date: LocalDate
)
