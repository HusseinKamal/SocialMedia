package com.hussein.socialmedia

import com.hussein.socialmedia.domain.feed.model.Post
import com.hussein.socialmedia.domain.feed.repository.PostRepository
import com.hussein.socialmedia.domain.feed.usecase.CreatePostUseCase
import com.hussein.socialmedia.domain.feed.usecase.LikePostUseCase
import com.hussein.socialmedia.domain.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Feed Use Cases.
 * Tests business logic in isolation.
 */
class FeedUseCasesTest {

    private lateinit var postRepository: PostRepository
    private lateinit var likePostUseCase: LikePostUseCase
    private lateinit var createPostUseCase: CreatePostUseCase

    @Before
    fun setup() {
        postRepository = mockk()
        likePostUseCase = LikePostUseCase(postRepository)
        createPostUseCase = CreatePostUseCase(postRepository)
    }

    @Test
    fun `likePostUseCase should call repository likePost`() = runTest {
        // Given
        val postId = "post123"
        val post = createMockPost(postId, isLiked = true)
        coEvery { postRepository.likePost(postId) } returns Resource.Success(post)

        // When
        val result = likePostUseCase(postId)

        // Then
        coVerify { postRepository.likePost(postId) }
        assertTrue(result is Resource.Success)
        assertEquals(post, (result as Resource.Success).data)
    }

    @Test
    fun `likePostUseCase should return error when repository fails`() = runTest {
        // Given
        val postId = "post123"
        val errorMessage = "Network error"
        coEvery { postRepository.likePost(postId) } returns Resource.Error(errorMessage)

        // When
        val result = likePostUseCase(postId)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)
    }

    @Test
    fun `createPostUseCase should return error when content is blank`() = runTest {
        // Given
        val blankContent = "   "

        // When
        val result = createPostUseCase(blankContent, null)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Post content cannot be empty", (result as Resource.Error).message)
    }

    @Test
    fun `createPostUseCase should call repository when content is valid`() = runTest {
        // Given
        val content = "Valid post content"
        val post = createMockPost("new123")
        coEvery { postRepository.createPost(content, null) } returns Resource.Success(post)

        // When
        val result = createPostUseCase(content, null)

        // Then
        coVerify { postRepository.createPost(content, null) }
        assertTrue(result is Resource.Success)
    }

    @Test
    fun `createPostUseCase should handle repository error`() = runTest {
        // Given
        val content = "Valid content"
        val errorMessage = "Server error"
        coEvery { postRepository.createPost(content, null) } returns Resource.Error(errorMessage)

        // When
        val result = createPostUseCase(content, null)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)
    }

    private fun createMockPost(
        id: String,
        isLiked: Boolean = false
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
            isSaved = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = null
        )
    }
}