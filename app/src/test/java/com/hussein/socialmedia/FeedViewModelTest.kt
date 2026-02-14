package com.hussein.socialmedia

import androidx.paging.PagingData
import app.cash.turbine.test
import com.hussein.socialmedia.domain.feed.model.Post
import com.hussein.socialmedia.domain.feed.usecase.CreatePostUseCase
import com.hussein.socialmedia.domain.feed.usecase.GetFeedPostsUseCase
import com.hussein.socialmedia.domain.feed.usecase.LikePostUseCase
import com.hussein.socialmedia.domain.feed.usecase.SavePostUseCase
import com.hussein.socialmedia.domain.feed.usecase.UnlikePostUseCase
import com.hussein.socialmedia.domain.feed.usecase.UnsavePostUseCase
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.presentation.feed.event.FeedEffect
import com.hussein.socialmedia.presentation.feed.event.FeedUiEvent
import com.hussein.socialmedia.presentation.feed.viewmodel.FeedViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for FeedViewModel.
 * Tests ViewModel logic in isolation from Android framework.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private lateinit var viewModel: FeedViewModel
    private lateinit var getFeedPostsUseCase: GetFeedPostsUseCase
    private lateinit var likePostUseCase: LikePostUseCase
    private lateinit var unlikePostUseCase: UnlikePostUseCase
    private lateinit var savePostUseCase: SavePostUseCase
    private lateinit var unsavePostUseCase: UnsavePostUseCase
    private lateinit var createPostUseCase: CreatePostUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create mocks
        getFeedPostsUseCase = mockk()
        likePostUseCase = mockk()
        unlikePostUseCase = mockk()
        savePostUseCase = mockk()
        unsavePostUseCase = mockk()
        createPostUseCase = mockk()

        // Setup default behavior
        every { getFeedPostsUseCase() } returns flowOf(PagingData.empty())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state should load feed`() = runTest {
        // Given
        viewModel = FeedViewModel(
            getFeedPostsUseCase,
            likePostUseCase,
            unlikePostUseCase,
            savePostUseCase,
            unsavePostUseCase,
            createPostUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        verify { getFeedPostsUseCase() }
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `likePost should call likePostUseCase`() = runTest {
        // Given
        val postId = "post123"
        val post = createMockPost(postId, isLiked = true)
        coEvery { likePostUseCase(postId) } returns Resource.Success(post)

        viewModel = FeedViewModel(
            getFeedPostsUseCase,
            likePostUseCase,
            unlikePostUseCase,
            savePostUseCase,
            unsavePostUseCase,
            createPostUseCase
        )

        // When
        viewModel.onEvent(FeedUiEvent.LikePost(postId))
        advanceUntilIdle()

        // Then
        coVerify { likePostUseCase(postId) }
    }

    @Test
    fun `likePost error should emit ShowError effect`() = runTest {
        // Given
        val postId = "post123"
        val errorMessage = "Failed to like post"
        coEvery { likePostUseCase(postId) } returns Resource.Error(errorMessage)

        viewModel = FeedViewModel(
            getFeedPostsUseCase,
            likePostUseCase,
            unlikePostUseCase,
            savePostUseCase,
            unsavePostUseCase,
            createPostUseCase
        )

        // When & Then
        viewModel.effect.test {
            viewModel.onEvent(FeedUiEvent.LikePost(postId))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is FeedEffect.ShowError)
            assertEquals(errorMessage, (effect as FeedEffect.ShowError).message)
        }
    }

    @Test
    fun `createPost should update state and emit success`() = runTest {
        // Given
        val content = "Test post content"
        val post = createMockPost("new123")
        coEvery { createPostUseCase(content, null) } returns Resource.Success(post)

        viewModel = FeedViewModel(
            getFeedPostsUseCase,
            likePostUseCase,
            unlikePostUseCase,
            savePostUseCase,
            unsavePostUseCase,
            createPostUseCase
        )

        // When & Then
        viewModel.effect.test {
            viewModel.onEvent(FeedUiEvent.CreatePost(content, null))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is FeedEffect.ShowSuccess)
            assertFalse(viewModel.uiState.value.isCreatingPost)
            assertTrue(viewModel.uiState.value.createPostSuccess)
        }
    }

    @Test
    fun `createPost with empty content should return error`() = runTest {
        // Given
        val emptyContent = ""
        coEvery { createPostUseCase(emptyContent, null) } returns Resource.Error("Post content cannot be empty")

        viewModel = FeedViewModel(
            getFeedPostsUseCase,
            likePostUseCase,
            unlikePostUseCase,
            savePostUseCase,
            unsavePostUseCase,
            createPostUseCase
        )

        // When & Then
        viewModel.effect.test {
            viewModel.onEvent(FeedUiEvent.CreatePost(emptyContent, null))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is FeedEffect.ShowError)
        }
    }

    @Test
    fun `savePost should call savePostUseCase`() = runTest {
        // Given
        val postId = "post123"
        val post = createMockPost(postId, isSaved = true)
        coEvery { savePostUseCase(postId) } returns Resource.Success(post)

        viewModel = FeedViewModel(
            getFeedPostsUseCase,
            likePostUseCase,
            unlikePostUseCase,
            savePostUseCase,
            unsavePostUseCase,
            createPostUseCase
        )

        // When
        viewModel.onEvent(FeedUiEvent.SavePost(postId))
        advanceUntilIdle()

        // Then
        coVerify { savePostUseCase(postId) }
    }

    @Test
    fun `refresh should reload feed`() = runTest {
        // Given
        viewModel = FeedViewModel(
            getFeedPostsUseCase,
            likePostUseCase,
            unlikePostUseCase,
            savePostUseCase,
            unsavePostUseCase,
            createPostUseCase
        )
        advanceUntilIdle()
        clearMocks(getFeedPostsUseCase)
        every { getFeedPostsUseCase() } returns flowOf(PagingData.empty())

        // When
        viewModel.onEvent(FeedUiEvent.Refresh)
        advanceUntilIdle()

        // Then
        verify(atLeast = 1) { getFeedPostsUseCase() }
    }

    // Helper function to create mock post
    private fun createMockPost(
        id: String,
        isLiked: Boolean = false,
        isSaved: Boolean = false
    ): Post {
        return Post(
            id = id,
            userId = "user123",
            username = "testuser",
            userAvatarUrl = null,
            content = "Test content",
            imageUrl = null,
            likesCount = 10,
            commentsCount = 5,
            sharesCount = 2,
            isLiked = isLiked,
            isSaved = isSaved,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )
    }
}