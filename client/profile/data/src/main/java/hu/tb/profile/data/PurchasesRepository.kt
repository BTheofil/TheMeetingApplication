package hu.tb.profile.data

import android.app.Activity
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesErrorCode
import com.revenuecat.purchases.PurchasesTransactionException
import com.revenuecat.purchases.awaitLogIn
import com.revenuecat.purchases.awaitLogOut
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchaseResult
import hu.tb.profile.domain.PurchaseOutcome
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

    suspend fun purchase(activity: Activity, optionId: String): PurchaseOutcome {
        val pack = packages[optionId]
            ?: return PurchaseOutcome.Failed("This option is no longer available.")

        val params = PurchaseParams.Builder(activity, pack).build()

        return purchases.awaitPurchaseResult(params).fold(
            onSuccess = { PurchaseOutcome.Success },
            onFailure = { throwable ->
                val exception = throwable as? PurchasesTransactionException
                if (exception?.userCancelled == true) {
                    PurchaseOutcome.Cancelled
                } else {
                    PurchaseOutcome.Failed(exception.toUserMessage())
                }
            }
        )
    }

    suspend fun syncIdentity(userId: String?) {
        runCatching {
            when {
                userId == null -> if (!purchases.isAnonymous) purchases.awaitLogOut()
                purchases.appUserID != userId -> purchases.awaitLogIn(userId)
            }
        }
    }
}

private fun PurchasesTransactionException?.toUserMessage(): String = when (this?.code) {
    PurchasesErrorCode.NetworkError,
    PurchasesErrorCode.UnknownBackendError,
    PurchasesErrorCode.UnexpectedBackendResponseError ->
        "No connection to the store. Check your internet and try again."

    PurchasesErrorCode.StoreProblemError ->
        "The store is having trouble right now. Please try again later."

    PurchasesErrorCode.PurchaseNotAllowedError,
    PurchasesErrorCode.InsufficientPermissionsError ->
        "This device is not allowed to make purchases."

    PurchasesErrorCode.ProductNotAvailableForPurchaseError ->
        "This option is not available in your region."

    PurchasesErrorCode.ProductAlreadyPurchasedError ->
        "You already own this. It may take a moment before you can tip again."

    PurchasesErrorCode.PaymentPendingError ->
        "Your payment is still processing. Thanks for the support!"

    PurchasesErrorCode.TestStoreSimulatedPurchaseError ->
        "Simulated purchase failure (Test Store)."

    else -> "Something went wrong with the purchase. Please try again."
}
