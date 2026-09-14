package hu.tb.schedule.presentation.di

import hu.tb.schedule.presentation.ScheduleViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val scheduleModule = module {
    viewModelOf(::ScheduleViewModel)
}
