package hu.tb.presentation.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.domain.AuthMode
import hu.tb.domain.LoginForm
import hu.tb.domain.RegisterForm
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class AuthFormViewModel(
    private val mode: AuthMode
) : ViewModel() {

    private val _state = MutableStateFlow(AuthFormState())
    val state = _state.asStateFlow()

    private val _event = Channel<AuthFormEvent>()
    val event = _event.receiveAsFlow()

    init {
        _state.update {
            it.copy(
                mode = mode
            )
        }
    }

    fun register(
        form: RegisterForm
    ) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(1.seconds)
            _state.update { it.copy(isLoading = false) }
            _event.send(AuthFormEvent.Success)
        }
    }

    fun login(
        form: LoginForm
    ) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(1.seconds)
            _state.update { it.copy(isLoading = false) }
            _event.send(AuthFormEvent.Failed("server error"))
        }
    }
}
