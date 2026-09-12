package hu.tb.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.datastore.ProfileType
import hu.tb.network.fold
import hu.tb.network.repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userDatastoreRepository: UserDatastoreRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _event = Channel<ProfileEvent>()
    val event = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            userDatastoreRepository.userdataFlow().collect { userData ->
                _state.update {
                    it.copy(
                        name = userData.name,
                        profileType = ProfileType.fromValue(userData.profileType)
                    )
                }
            }
        }
    }

    fun deleteProfile() {
        if (state.value.isDeleting) return

        _state.update { it.copy(isDeleting = true) }
        viewModelScope.launch {
            profileRepository.unregisterDeviceFid()
            val event = profileRepository.deleteProfile().fold(
                success = {
                    userDatastoreRepository.clearUserData()
                    ProfileEvent.Cleared
                },
                fail = { ProfileEvent.Failed(it.formatErrorMessage) }
            )

            _state.update { it.copy(isDeleting = false) }
            _event.send(event)
        }
    }

    fun logout() {
        viewModelScope.launch {
            profileRepository.unregisterDeviceFid()
            userDatastoreRepository.clearUserData()
            _event.send(ProfileEvent.Cleared)
        }
    }
}
