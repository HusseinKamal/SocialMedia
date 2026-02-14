package com.hussein.socialmedia.presentation.feed.event

/**
 * One-time effects/side effects for Feed screen.
 */
sealed class FeedEffect {
    data class ShowError(val message: String) : FeedEffect()
    data class ShowSuccess(val message: String) : FeedEffect()
    data class NavigateToProfile(val userId: String) : FeedEffect()
    data class NavigateToComments(val postId: String) : FeedEffect()
}