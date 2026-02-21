package com.hussein.socialmedia.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hussein.socialmedia.domain.profile.usecase.GetFriendsUseCase
import com.hussein.socialmedia.domain.profile.usecase.SearchFriendsUseCase
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.presentation.profile.event.FriendsListEffect
import com.hussein.socialmedia.presentation.profile.event.FriendsListUiEvent
import com.hussein.socialmedia.presentation.profile.mapper.toUiModelList
import com.hussein.socialmedia.presentation.profile.model.FriendUiModel
import com.hussein.socialmedia.presentation.profile.state.FriendsListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Friends List screen
 */
@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val searchFriendsUseCase: SearchFriendsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsListUiState())
    val uiState: StateFlow<FriendsListUiState> = _uiState.asStateFlow()

    private val _effect = Channel<FriendsListEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        loadFriends()
    }

    fun onEvent(event: FriendsListUiEvent) {
        when (event) {
            is FriendsListUiEvent.LoadFriends -> loadFriends()
            is FriendsListUiEvent.Refresh -> refresh()
            is FriendsListUiEvent.SearchQueryChanged -> updateSearchQuery(event.query)
            is FriendsListUiEvent.FriendClicked -> {
                sendEffect(FriendsListEffect.NavigateToChat(event.friend))
            }
            is FriendsListUiEvent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun loadFriends() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getFriendsUseCase()) {
                is Resource.Success -> {
                    val friendsUi = result.data.toUiModelList()
                    _uiState.update {
                        it.copy(
                            friends = friendsUi,
                            filteredFriends = friendsUi,
                            isLoading = false,
                            isEmpty = friendsUi.isEmpty()
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message,
                            isEmpty = it.friends.isEmpty()
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }

            when (val result = getFriendsUseCase()) {
                is Resource.Success -> {
                    val friendsUi = result.data.toUiModelList()
                    _uiState.update {
                        it.copy(
                            friends = friendsUi,
                            filteredFriends = if (it.searchQuery.isEmpty()) {
                                friendsUi
                            } else {
                                filterFriends(friendsUi, it.searchQuery)
                            },
                            isRefreshing = false,
                            isEmpty = friendsUi.isEmpty()
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // Cancel previous search job
        searchJob?.cancel()

        if (query.isEmpty()) {
            // Show all friends
            _uiState.update {
                it.copy(
                    filteredFriends = it.friends,
                    isSearching = false
                )
            }
            return
        }

        // Debounce search
        searchJob = viewModelScope.launch {
            delay(300) // Wait for user to stop typing
            searchFriends(query)
        }
    }

    private suspend fun searchFriends(query: String) {
        _uiState.update { it.copy(isSearching = true) }

        when (val result = searchFriendsUseCase(query)) {
            is Resource.Success -> {
                val friendsUi = result.data.toUiModelList()
                _uiState.update {
                    it.copy(
                        filteredFriends = friendsUi,
                        isSearching = false,
                        searchResultsEmpty = friendsUi.isEmpty()
                    )
                }
            }
            is Resource.Error -> {
                // Fallback to locally filtering
                val filtered = filterFriends(_uiState.value.friends, query)
                _uiState.update {
                    it.copy(
                        filteredFriends = filtered,
                        isSearching = false,
                        searchResultsEmpty = filtered.isEmpty()
                    )
                }
            }
            else -> {
                _uiState.update { it.copy(isSearching = false) }
            }
        }
    }

    private fun filterFriends(friends: List<FriendUiModel>, query: String): List<FriendUiModel> {
        val lowerQuery = query.lowercase()
        return friends.filter {
            it.fullName.lowercase().contains(lowerQuery) ||
                    it.username.lowercase().contains(lowerQuery)
        }
    }

    private fun sendEffect(effect: FriendsListEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}