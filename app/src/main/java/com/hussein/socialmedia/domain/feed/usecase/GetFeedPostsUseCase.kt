package com.hussein.socialmedia.domain.feed.usecase

import com.hussein.socialmedia.domain.feed.model.Post
import com.hussein.socialmedia.domain.feed.repository.PostRepository
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting paginated feed posts.
 * Encapsulates business logic for feed retrieval.
 */
class GetFeedPostsUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(): Flow<PagingData<Post>> {
        return postRepository.getFeedPosts()
    }
}