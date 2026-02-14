package com.hussein.socialmedia.presentation.feed.state

import androidx.paging.PagingData
import com.hussein.socialmedia.presentation.feed.model.PostUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * UI State for Feed screen following MVI pattern.
 * Represents the complete state of the feed screen.
 */
data class FeedUiState(
    val posts: Flow<PagingData<PostUiModel>> = emptyFlow(),
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val isCreatingPost: Boolean = false,
    val createPostSuccess: Boolean = false
)