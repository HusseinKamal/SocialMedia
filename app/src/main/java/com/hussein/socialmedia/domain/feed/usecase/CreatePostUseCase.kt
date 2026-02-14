package com.hussein.socialmedia.domain.feed.usecase

import com.hussein.socialmedia.domain.feed.model.Post
import com.hussein.socialmedia.domain.feed.repository.PostRepository
import com.hussein.socialmedia.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for creating a post.
 */
class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(content: String, imageUrl: String?): Resource<Post> {
        if (content.isBlank()) {
            return Resource.Error("Post content cannot be empty")
        }
        return postRepository.createPost(content, imageUrl)
    }
}