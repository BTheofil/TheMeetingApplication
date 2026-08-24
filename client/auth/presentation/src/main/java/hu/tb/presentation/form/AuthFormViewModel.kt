package hu.tb.presentation.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.domain.AuthForm
import hu.tb.domain.AuthMode
import hu.tb.network.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthFormViewModel(
    private val mode: AuthMode,
    private val authRepository: AuthRepository,
    private val userDatastoreRepository: UserDatastoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthFormState(mode = mode))
    val state = _state.asStateFlow()

    private val _event = Channel<AuthFormEvent>()
    val event = _event.receiveAsFlow()

    fun submit(mode: AuthMode, form: AuthForm) {
        if (_state.value.isLoading) return

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = authRepository.authenticate(
                mode = mode,
                form = form
            )

            val token = result.token
            if (token != null) {
                userDatastoreRepository.updateUserData(
                    name = form.username,
                    password = form.password,
                    profileType = form.type.value,
                    token = token,
                    tokenRefreshDate = System.currentTimeMillis()
                )
            }

            _state.update { it.copy(isLoading = false) }

            _event.send(
                if (token != null) AuthFormEvent.Success
                else AuthFormEvent.Failed(
                    result.errorMessage ?: "Something went wrong. Please try again."
                )
            )
        }
    }
}
