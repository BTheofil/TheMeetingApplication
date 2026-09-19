package hu.tb.profile.data

import android.content.Context
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

fun initRevenueCat(
    context: Context,
    apiKey: String,
    isDebug: Boolean,
) {
    if (Purchases.isConfigured) return

    Purchases.logLevel = if (isDebug) LogLevel.DEBUG else LogLevel.INFO
    Purchases.configure(
        PurchasesConfiguration.Builder(context, apiKey).build()
    )
}
