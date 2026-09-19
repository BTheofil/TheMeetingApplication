package hu.tb.profile.presentation.di

import com.revenuecat.purchases.Purchases
import hu.tb.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    single<Purchases> { Purchases.sharedInstance }
}
