package com.hussein.socialmedia.presentation.profile.event

import com.hussein.socialmedia.presentation.profile.model.FriendUiModel


/**
 * UI Events for Friends List screen
 */
sealed class FriendsListUiEvent {
    data object LoadFriends : FriendsListUiEvent()
    data object Refresh : FriendsListUiEvent()
    data class SearchQueryChanged(val query: String) : FriendsListUiEvent()
    data class FriendClicked(val friend: FriendUiModel) : FriendsListUiEvent()
    data object ClearError : FriendsListUiEvent()
}