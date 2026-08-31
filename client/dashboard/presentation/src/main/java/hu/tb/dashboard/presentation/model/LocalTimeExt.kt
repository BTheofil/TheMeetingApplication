package hu.tb.dashboard.presentation.model

import kotlinx.datetime.LocalTime

private const val SECONDS_PER_MINUTE = 60
private const val SECONDS_PER_DAY = 24 * 60 * 60

internal fun LocalTime.plusMinutes(minutes: Int): LocalTime =
    LocalTime.fromSecondOfDay((toSecondOfDay() + minutes * SECONDS_PER_MINUTE).mod(SECONDS_PER_DAY))
