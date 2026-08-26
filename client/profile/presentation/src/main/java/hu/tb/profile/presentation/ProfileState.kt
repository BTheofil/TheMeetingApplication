package hu.tb.profile.presentation

import hu.tb.domain.ProfileType

data class ProfileState(
    val name: String = "",
    val profileType: ProfileType? = null,
    val isDeleting: Boolean = false
)
