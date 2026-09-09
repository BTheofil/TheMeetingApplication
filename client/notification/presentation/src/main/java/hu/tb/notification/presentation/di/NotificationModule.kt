package hu.tb.notification.presentation.di

import hu.tb.notification.presentation.NotificationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val notificationModule = module {
    viewModelOf(::NotificationViewModel)
}
