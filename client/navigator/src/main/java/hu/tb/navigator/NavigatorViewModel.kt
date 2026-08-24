package hu.tb.navigator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserData
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.domain.AuthForm
import hu.tb.domain.AuthMode
import hu.tb.domain.ProfileType
import hu.tb.network.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SessionState {
    data object Init : SessionState
    data object NoUserSavedData : SessionState
    data object LoggedIn : SessionState
    data object Expired : SessionState
}

class NavigatorViewModel(
    private val userDatastoreRepository: UserDatastoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SessionState>(SessionState.Init)
    val state = _state.asStateFlow()

    private var hasRefreshed = false

    init {
        viewModelScope.launch {
            val userData = userDatastoreRepository.userdataFlow().first()
            _state.value =
                if (userData.isLoggedIn) SessionState.LoggedIn else SessionState.NoUserSavedData
        }
    }

    fun refreshToken() {
        if (hasRefreshed) return
        hasRefreshed = true

        viewModelScope.launch {
            val userData = userDatastoreRepository.userdataFlow().first()
            if (!userData.isLoggedIn) return@launch

            val result = authRepository.authenticate(
                mode = AuthMode.LOGIN,
                form = userData.toAuthForm()
            )

            when {
                result.token != null -> userDatastoreRepository.updateUserData(
                    token = result.token,
                    tokenRefreshDate = System.currentTimeMillis()
                )
                else -> _state.value = SessionState.Expired
            }
        }
    }

    fun onExpiredConfirmed() {
        viewModelScope.launch {
            userDatastoreRepository.clearUserData()
            _state.value = SessionState.NoUserSavedData
        }
    }

    private fun UserData.toAuthForm(): AuthForm = AuthForm(
        username = name,
        password = password,
        type = ProfileType.entries.firstOrNull { it.value == profileType } ?: ProfileType.NORMAL
    )
}
