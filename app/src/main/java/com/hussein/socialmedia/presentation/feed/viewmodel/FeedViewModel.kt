package com.hussein.socialmedia.presentation.feed.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.hussein.socialmedia.domain.feed.usecase.CreatePostUseCase
import com.hussein.socialmedia.domain.feed.usecase.GetFeedPostsUseCase
import com.hussein.socialmedia.domain.feed.usecase.LikePostUseCase
import com.hussein.socialmedia.domain.feed.usecase.SavePostUseCase
import com.hussein.socialmedia.domain.feed.usecase.UnlikePostUseCase
import com.hussein.socialmedia.domain.feed.usecase.UnsavePostUseCase
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.presentation.feed.event.FeedEffect
import com.hussein.socialmedia.presentation.feed.event.FeedUiEvent
import com.hussein.socialmedia.presentation.feed.mapper.toUiModel
import com.hussein.socialmedia.presentation.feed.state.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Feed screen following MVI pattern.
 * Handles business logic and state management.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedPostsUseCase: GetFeedPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val savePostUseCase: SavePostUseCase,
    private val unsavePostUseCase: UnsavePostUseCase,
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _effect = Channel<FeedEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadFeed()
    }

    /**
     * Handle UI events from the view.
     */
    fun onEvent(event: FeedUiEvent) {
        when (event) {
            is FeedUiEvent.Refresh -> loadFeed()
            is FeedUiEvent.LikePost -> likePost(event.postId)
            is FeedUiEvent.UnlikePost -> unlikePost(event.postId)
            is FeedUiEvent.SavePost -> savePost(event.postId)
            is FeedUiEvent.UnsavePost -> unsavePost(event.postId)
            is FeedUiEvent.CreatePost -> createPost(event.content, event.imageUrl)
            is FeedUiEvent.NavigateToProfile -> sendEffect(FeedEffect.NavigateToProfile(event.userId))
            is FeedUiEvent.NavigateToComments -> sendEffect(FeedEffect.NavigateToComments(event.postId))
            is FeedUiEvent.ClearError -> clearError()
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            val posts = getFeedPostsUseCase()
                .map { pagingData ->
                    pagingData.map { post -> post.toUiModel() }
                }
                .cachedIn(viewModelScope)

            _uiState.update {
                it.copy(
                    posts = posts,
                    isRefreshing = false,
                    error = null
                )
            }
        }
    }

    private fun likePost(postId: String) {
        viewModelScope.launch {
            when (val result = likePostUseCase(postId)) {
                is Resource.Success -> {
                    // Post updated in database, paging will refresh automatically
                }
                is Resource.Error -> {
                    sendEffect(FeedEffect.ShowError(result.message))
                }
                else -> { /* Handle other states if needed */ }
            }
        }
    }

    private fun unlikePost(postId: String) {
        viewModelScope.launch {
            when (val result = unlikePostUseCase(postId)) {
                is Resource.Success -> {
                    // Post updated in database
                }
                is Resource.Error -> {
                    sendEffect(FeedEffect.ShowError(result.message))
                }
                else -> {}
            }
        }
    }

    private fun savePost(postId: String) {
        viewModelScope.launch {
            when (val result = savePostUseCase(postId)) {
                is Resource.Success -> {
                    sendEffect(FeedEffect.ShowSuccess("Post saved"))
                }
                is Resource.Error -> {
                    sendEffect(FeedEffect.ShowError(result.message))
                }
                else -> {}
            }
        }
    }

    private fun unsavePost(postId: String) {
        viewModelScope.launch {
            when (val result = unsavePostUseCase(postId)) {
                is Resource.Success -> {
                    sendEffect(FeedEffect.ShowSuccess("Post removed from saved"))
                }
                is Resource.Error -> {
                    sendEffect(FeedEffect.ShowError(result.message))
                }
                else -> {}
            }
        }
    }

    private fun createPost(content: String, imageUrl: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingPost = true) }

            when (val result = createPostUseCase(content, imageUrl)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isCreatingPost = false,
                            createPostSuccess = true
                        )
                    }
                    sendEffect(FeedEffect.ShowSuccess("Post created successfully"))
                    loadFeed() // Refresh feed
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isCreatingPost = false) }
                    sendEffect(FeedEffect.ShowError(result.message))
                }
                else -> {
                    _uiState.update { it.copy(isCreatingPost = false) }
                }
            }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun sendEffect(effect: FeedEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}