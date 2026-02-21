package com.hussein.socialmedia.presentation.profile.event

import com.hussein.socialmedia.presentation.profile.model.FriendUiModel

/**
 * Side effects for Friends List screen
 */
sealed class FriendsListEffect {
    data class NavigateToChat(val friend: FriendUiModel) : FriendsListEffect()
}