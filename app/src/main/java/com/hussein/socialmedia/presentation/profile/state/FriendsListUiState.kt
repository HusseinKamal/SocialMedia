package com.hussein.socialmedia.presentation.profile.state

import com.hussein.socialmedia.presentation.profile.model.FriendUiModel

/**
 * UI State for Friends List screen
 */
data class FriendsListUiState(
    val friends: List<FriendUiModel> = emptyList(),
    val filteredFriends: List<FriendUiModel> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSearching: Boolean = false,
    val isEmpty: Boolean = false,
    val searchResultsEmpty: Boolean = false,
    val error: String? = null
)