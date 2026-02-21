package com.hussein.socialmedia.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hussein.socialmedia.domain.profile.repository.UserRepository
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.presentation.profile.state.EditProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        userRepository.getCurrentUser().onEach { result ->
            if (result is Resource.Success) {
                _state.value = _state.value.copy(
                    username = result.data.username,
                    fullName = result.data.displayName
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onUsernameChanged(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun onFullNameChanged(fullName: String) {
        _state.value = _state.value.copy(fullName = fullName)
    }

    fun saveProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            userRepository.updateProfile(
                displayName = state.value.fullName,
                bio = null, // Not implemented yet
                avatarUrl = null // Not implemented yet
            )
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
