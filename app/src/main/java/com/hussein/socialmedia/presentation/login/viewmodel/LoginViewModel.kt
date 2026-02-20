package com.hussein.socialmedia.presentation.login.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hussein.socialmedia.domain.auth.usecase.LoginUseCase
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.presentation.login.event.LoginEffect
import com.hussein.socialmedia.presentation.login.event.LoginUiEvent
import com.hussein.socialmedia.presentation.login.mapper.toLoginUiModel
import com.hussein.socialmedia.presentation.login.mapper.validatePassword
import com.hussein.socialmedia.presentation.login.mapper.validateUsername
import com.hussein.socialmedia.presentation.login.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Login screen following MVI pattern
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.UsernameChanged -> {
                updateUsername(event.username)
            }
            is LoginUiEvent.PasswordChanged -> {
                updatePassword(event.password)
            }
            is LoginUiEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is LoginUiEvent.Login -> login()
            is LoginUiEvent.NavigateToRegister -> {
                sendEffect(LoginEffect.NavigateToRegister)
            }
            is LoginUiEvent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
            is LoginUiEvent.ValidateFields -> {
                validateAllFields()
            }
        }
    }

    private fun updateUsername(username: String) {
        val validation = username.validateUsername()
        _uiState.update {
            it.copy(
                username = username,
                usernameError = if (username.isNotBlank() && !validation.isValid) {
                    validation.errorMessage
                } else null,
                canSubmit = canSubmit(username, it.password)
            )
        }
    }

    private fun updatePassword(password: String) {
        val validation = password.validatePassword()
        _uiState.update {
            it.copy(
                password = password,
                passwordError = if (password.isNotBlank() && !validation.isValid) {
                    validation.error
                } else null,
                canSubmit = canSubmit(it.username, password)
            )
        }
    }

    private fun validateAllFields() {
        val state = _uiState.value

        val usernameValidation = state.username.validateUsername()
        val passwordValidation = state.password.validatePassword()

        _uiState.update {
            it.copy(
                usernameError = if (!usernameValidation.isValid) usernameValidation.errorMessage else null,
                passwordError = if (!passwordValidation.isValid) passwordValidation.error else null
            )
        }
    }

    private fun login() {
        // Validate before submitting
        validateAllFields()

        val state = _uiState.value
        if (state.usernameError != null || state.passwordError != null) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = loginUseCase(state.username, state.password)) {
                is Resource.Success -> {
                    val uiModel = result.data.toLoginUiModel()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true
                        )
                    }
                    sendEffect(LoginEffect.NavigateToHome(uiModel.user))
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message,
                            loginSuccess = false
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun canSubmit(username: String, password: String): Boolean {
        return username.isNotBlank() &&
                password.isNotBlank() &&
                username.validateUsername().isValid &&
                password.validatePassword().isValid
    }

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}