package hu.tb.profile.data

import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitOfferings
import hu.tb.profile.domain.SupportInfo

class PurchasesRepository(
    private val purchases: Purchases
) {
    private var packages: Map<String, Package> = emptyMap()

    suspend fun loadSupportOptions(): List<SupportInfo> {
        val available = runCatching { purchases.awaitOfferings().current?.availablePackages }
            .getOrNull()
            .orEmpty()

        packages = available.associateBy { it.identifier }

        return available.map { pack ->
            SupportInfo(
                id = pack.identifier,
                displayName = pack.product.title,
                description = pack.product.description.ifBlank { null },
                price = pack.product.price.formatted
            )
        }
    }
}