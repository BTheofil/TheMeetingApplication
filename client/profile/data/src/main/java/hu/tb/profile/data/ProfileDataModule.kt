package hu.tb.profile.data

import com.revenuecat.purchases.Purchases
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val profileDataModule = module {
    single<Purchases> { Purchases.sharedInstance }
    singleOf(::PurchasesRepository)
}