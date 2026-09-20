package hu.tb.profile.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.ProfileType
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.network.fold
import hu.tb.network.repository.ProfileRepository
import hu.tb.profile.data.PurchasesRepository
import hu.tb.profile.domain.PurchaseOutcome
import hu.tb.profile.domain.SupportInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userDatastoreRepository: UserDatastoreRepository,
    private val profileRepository: ProfileRepository,
    private val purchasesRepository: PurchasesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _event = Channel<ProfileEvent>()
    val event = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userDataIndo = userDatastoreRepository.userdataFlow().first()
            _state.update {
                it.copy(
                    name = userDataIndo.name,
                    profileType = ProfileType.fromValue(userDataIndo.profileType)
                )
            }
        }

        viewModelScope.launch {
            val options = purchasesRepository.loadSupportOptions()
            _state.update { it.copy(supportOptions = options) }
        }
    }

    fun supportOption(activity: Activity, option: SupportInfo) {
        if (state.value.supportOptions.any { it.isPurchasing }) return

        setPurchasing(option.id, isPurchasing = true)
        viewModelScope.launch {
            when (val outcome = purchasesRepository.purchase(activity, option.id)) {
                PurchaseOutcome.Success ->
                    _event.send(ProfileEvent.ShowThankYouDialog)

                PurchaseOutcome.Cancelled -> Unit

                is PurchaseOutcome.Failed ->
                    _event.send(ProfileEvent.Failed(outcome.message))
            }

            setPurchasing(option.id, isPurchasing = false)
        }
    }

    private fun setPurchasing(optionId: String, isPurchasing: Boolean) {
        _state.update { state ->
            state.copy(
                supportOptions = state.supportOptions.map { option ->
                    if (option.id == optionId) option.copy(isPurchasing = isPurchasing)
                    else option
                }
            )
        }
    }

    fun deleteProfile() {
        viewModelScope.launch {
            _event.send(ProfileEvent.ShowDeleteLoadingDialog)
            profileRepository.unregisterDeviceFid()
            val event = profileRepository.deleteProfile().fold(
                success = {
                    userDatastoreRepository.clearUserData()
                    ProfileEvent.ProfileCleared
                },
                fail = { ProfileEvent.Failed(it.formatErrorMessage) }
            )

            _event.send(event)
        }
    }

    fun logout() {
        viewModelScope.launch {
            profileRepository.unregisterDeviceFid()
            userDatastoreRepository.clearUserData()
            _event.send(ProfileEvent.ProfileCleared)
        }
    }
}
