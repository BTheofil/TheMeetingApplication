package hu.tb.profile.domain

import androidx.compose.runtime.Immutable

@Immutable
data class SupportInfo(
    val id: String,
    val displayName: String,
    val description: String?,
    val price: String,
    val isPurchasing: Boolean = false
)