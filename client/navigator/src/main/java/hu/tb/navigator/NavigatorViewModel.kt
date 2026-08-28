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

class NavigatorViewModel(
    private val userDatastoreRepository: UserDatastoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _session = MutableStateFlow<SessionState>(SessionState.Init)
    val session = _session.asStateFlow()

    private var isTokenRefreshed = false

    init {
        viewModelScope.launch {
            val userData = userDatastoreRepository.userdataFlow().first()
            _session.value =
                if (userData.isLoggedIn) SessionState.LoggedIn else SessionState.NoUserSavedData
        }
    }

    fun refreshToken() {
        if (isTokenRefreshed) return
        isTokenRefreshed = true

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
                else -> _session.value = SessionState.Expired
            }
        }
    }

    fun clearUserData() {
        viewModelScope.launch {
            userDatastoreRepository.clearUserData()
            isTokenRefreshed = false
            _session.value = SessionState.NoUserSavedData
        }
    }

    private fun UserData.toAuthForm(): AuthForm = AuthForm(
        username = name,
        password = password,
        type = ProfileType.fromValue(profileType)
    )
}
