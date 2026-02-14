package com.hussein.socialmedia.presentation.feed.event

/**
 * UI Events for Feed screen.
 * Represents all possible user interactions.
 */
sealed class FeedUiEvent {
    data object Refresh : FeedUiEvent()
    data class LikePost(val postId: String) : FeedUiEvent()
    data class UnlikePost(val postId: String) : FeedUiEvent()
    data class SavePost(val postId: String) : FeedUiEvent()
    data class UnsavePost(val postId: String) : FeedUiEvent()
    data class CreatePost(val content: String, val imageUrl: String?) : FeedUiEvent()
    data class NavigateToProfile(val userId: String) : FeedUiEvent()
    data class NavigateToComments(val postId: String) : FeedUiEvent()
    data object ClearError : FeedUiEvent()
}