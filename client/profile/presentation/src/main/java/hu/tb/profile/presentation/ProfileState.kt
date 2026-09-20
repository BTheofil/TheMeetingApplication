package hu.tb.profile.presentation

import androidx.compose.runtime.Immutable
import hu.tb.datastore.ProfileType
import hu.tb.profile.domain.SupportInfo

@Immutable
data class ProfileState(
    val name: String = "",
    val profileType: ProfileType? = null,
    val supportOptions: List<SupportInfo> = emptyList(),
)
