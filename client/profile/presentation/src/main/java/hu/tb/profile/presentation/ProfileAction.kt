package hu.tb.profile.presentation

import hu.tb.profile.domain.SupportInfo

sealed interface ProfileAction {
    data object OnBackClick : ProfileAction
    data object OnLogoutClick : ProfileAction
    data object OnDeleteConfirmed : ProfileAction
    data class SupportOptionClick(val supportInfo: SupportInfo): ProfileAction
}
