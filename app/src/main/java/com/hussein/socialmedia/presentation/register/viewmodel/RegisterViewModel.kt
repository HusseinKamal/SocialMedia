package com.hussein.socialmedia.presentation.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hussein.socialmedia.domain.auth.usecase.RegisterUseCase
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.presentation.login.mapper.isRegisterFormComplete
import com.hussein.socialmedia.presentation.login.mapper.toRegisterUiModel
import com.hussein.socialmedia.presentation.login.mapper.validateConfirmPassword
import com.hussein.socialmedia.presentation.login.mapper.validateEmail
import com.hussein.socialmedia.presentation.login.mapper.validateName
import com.hussein.socialmedia.presentation.login.mapper.validatePassword
import com.hussein.socialmedia.presentation.login.mapper.validateUsername
import com.hussein.socialmedia.presentation.register.event.RegisterEffect
import com.hussein.socialmedia.presentation.register.event.RegisterUiEvent
import com.hussein.socialmedia.presentation.register.state.RegisterUiState
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
 * ViewModel for Register screen following MVI pattern
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _effect = Channel<RegisterEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.FirstNameChanged -> updateFirstName(event.firstName)
            is RegisterUiEvent.LastNameChanged -> updateLastName(event.lastName)
            is RegisterUiEvent.UsernameChanged -> updateUsername(event.username)
            is RegisterUiEvent.EmailChanged -> updateEmail(event.email)
            is RegisterUiEvent.PasswordChanged -> updatePassword(event.password)
            is RegisterUiEvent.ConfirmPasswordChanged -> updateConfirmPassword(event.confirmPassword)
            is RegisterUiEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is RegisterUiEvent.ToggleConfirmPasswordVisibility -> {
                _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }
            is RegisterUiEvent.Register -> register()
            is RegisterUiEvent.NavigateToLogin -> {
                sendEffect(RegisterEffect.NavigateToLogin)
            }
            is RegisterUiEvent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
            is RegisterUiEvent.ValidateFields -> {
                validateAllFields()
            }
        }
    }

    private fun updateFirstName(firstName: String) {
        val validation = firstName.validateName("First name")
        _uiState.update {
            it.copy(
                firstName = firstName,
                firstNameError = if (firstName.isNotBlank() && !validation.isValid) {
                    validation.errorMessage
                } else null,
                canSubmit = canSubmit(it)
            )
        }
    }

    private fun updateLastName(lastName: String) {
        val validation = lastName.validateName("Last name")
        _uiState.update {
            it.copy(
                lastName = lastName,
                lastNameError = if (lastName.isNotBlank() && !validation.isValid) {
                    validation.errorMessage
                } else null,
                canSubmit = canSubmit(it)
            )
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
                canSubmit = canSubmit(it)
            )
        }
    }

    private fun updateEmail(email: String) {
        val validation = email.validateEmail()
        _uiState.update {
            it.copy(
                email = email,
                emailError = if (email.isNotBlank() && !validation.isValid) {
                    validation.errorMessage
                } else null,
                canSubmit = canSubmit(it)
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
                passwordStrength = validation.strengthLevel,
                passwordStrengthMessage = validation.strengthMessage,
                confirmPasswordError = if (it.confirmPassword.isNotBlank() &&
                    it.confirmPassword != password) {
                    "Passwords do not match"
                } else null,
                canSubmit = canSubmit(it)
            )
        }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        val validation = confirmPassword.validateConfirmPassword(_uiState.value.password)
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (confirmPassword.isNotBlank() && !validation.isValid) {
                    validation.errorMessage
                } else null,
                canSubmit = canSubmit(it)
            )
        }
    }

    private fun validateAllFields() {
        val state = _uiState.value

        val firstNameValidation = state.firstName.validateName("First name")
        val lastNameValidation = state.lastName.validateName("Last name")
        val usernameValidation = state.username.validateUsername()
        val emailValidation = state.email.validateEmail()
        val passwordValidation = state.password.validatePassword()
        val confirmPasswordValidation = state.confirmPassword.validateConfirmPassword(state.password)

        _uiState.update {
            it.copy(
                firstNameError = if (!firstNameValidation.isValid) firstNameValidation.errorMessage else null,
                lastNameError = if (!lastNameValidation.isValid) lastNameValidation.errorMessage else null,
                usernameError = if (!usernameValidation.isValid) usernameValidation.errorMessage else null,
                emailError = if (!emailValidation.isValid) emailValidation.errorMessage else null,
                passwordError = if (!passwordValidation.isValid) passwordValidation.error else null,
                confirmPasswordError = if (!confirmPasswordValidation.isValid) confirmPasswordValidation.errorMessage else null
            )
        }
    }

    private fun register() {
        // Validate before submitting
        validateAllFields()

        val state = _uiState.value
        if (state.hasErrors()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = registerUseCase(
                firstName = state.firstName,
                lastName = state.lastName,
                username = state.username,
                email = state.email,
                password = state.password,
                confirmPassword = state.confirmPassword
            )) {
                is Resource.Success -> {
                    val uiModel = result.data.toRegisterUiModel()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registerSuccess = true
                        )
                    }
                    sendEffect(RegisterEffect.NavigateToHome(uiModel.user))
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message,
                            registerSuccess = false
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

    private fun canSubmit(state: RegisterUiState): Boolean {
        return isRegisterFormComplete(
            state.firstName,
            state.lastName,
            state.username,
            state.email,
            state.password,
            state.confirmPassword
        ) &&
                state.firstName.validateName("First name").isValid &&
                state.lastName.validateName("Last name").isValid &&
                state.username.validateUsername().isValid &&
                state.email.validateEmail().isValid &&
                state.password.validatePassword().isValid &&
                state.confirmPassword.validateConfirmPassword(state.password).isValid
    }

    private fun RegisterUiState.hasErrors(): Boolean {
        return firstNameError != null ||
                lastNameError != null ||
                usernameError != null ||
                emailError != null ||
                passwordError != null ||
                confirmPasswordError != null
    }

    private fun sendEffect(effect: RegisterEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}