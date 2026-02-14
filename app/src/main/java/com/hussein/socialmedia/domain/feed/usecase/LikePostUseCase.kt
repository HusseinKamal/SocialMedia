package com.hussein.socialmedia.domain.feed.usecase

import com.hussein.socialmedia.domain.feed.model.Post
import com.hussein.socialmedia.domain.feed.repository.PostRepository
import com.hussein.socialmedia.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for liking a post.
 */
class LikePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): Resource<Post> {
        return postRepository.likePost(postId)
    }
}