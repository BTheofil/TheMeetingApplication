package hu.tb.meet.domain.receive

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MonthReceive(val date: LocalDate)