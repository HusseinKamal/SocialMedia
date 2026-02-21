package com.hussein.socialmedia.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.domain.profile.repository.UserRepository
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.presentation.profile.mapper.toAuthUser
import com.hussein.socialmedia.presentation.profile.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        userRepository.getCurrentUser().onEach { result ->
            _state.value = when (result) {
                is Resource.Success -> {
                    state.value.copy(
                        user = result.data.toAuthUser(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    state.value.copy(
                        user = AuthUser(), // Use default AuthUser on error
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    state.value.copy(
                        isLoading = true
                    )
                }
                is Resource.Idle -> state.value
            }
        }.launchIn(viewModelScope)
    }
}
