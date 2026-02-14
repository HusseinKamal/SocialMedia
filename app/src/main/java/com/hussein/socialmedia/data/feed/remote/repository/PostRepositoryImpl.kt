package com.hussein.socialmedia.data.feed.remote.repository


import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hussein.socialmedia.data.database.SocialMediaDatabase
import com.hussein.socialmedia.data.feed.remote.mapper.toDomain
import com.hussein.socialmedia.data.feed.remote.mapper.toEntity
import com.hussein.socialmedia.data.feed.remote.model.request.CreatePostRequest
import com.hussein.socialmedia.data.feed.remote.model.request.UpdatePostRequest
import com.hussein.socialmedia.data.feed.remote.service.PostApi
import com.hussein.socialmedia.domain.feed.model.Post
import com.hussein.socialmedia.domain.feed.repository.PostRepository
import com.hussein.socialmedia.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of PostRepository.
 * Handles data operations for posts with offline support and pagination.
 */
class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi,
    private val database: SocialMediaDatabase
) : PostRepository {

    private val postDao = database.postDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getFeedPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            remoteMediator = PostRemoteMediator(
                postApi = postApi,
                database = database
            ),
            pagingSourceFactory = { postDao.getPostsPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun getPostsByUserId(userId: String): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading)
        try {
            // Try to fetch from network
            val response = postApi.getPostsByUserId(userId, page = 1, pageSize = 50)
            val posts = response.posts.map { it.toDomain() }

            // Cache in database
            val entities = response.posts.map { it.toEntity() }
            postDao.insertPosts(entities)

            emit(Resource.Success(posts))
        } catch (e: Exception) {
            // Fallback to cached data
            postDao.getPostsByUserId(userId).map { entities ->
                val posts = entities.map { it.toDomain() }
                emit(Resource.Success(posts))
            }
        }
    }

    override suspend fun getPostById(postId: String): Resource<Post> {
        return try {
            val postDto = postApi.getPostById(postId)
            val post = postDto.toDomain()

            // Update cache
            postDao.insertPost(postDto.toEntity())

            Resource.Success(post)
        } catch (e: Exception) {
            // Try to get from cache
            val cachedPost = postDao.getPostById(postId)
            if (cachedPost != null) {
                Resource.Success(cachedPost.toDomain())
            } else {
                Resource.Error("Failed to load post: ${e.message}", e)
            }
        }
    }

    override suspend fun createPost(content: String, imageUrl: String?): Resource<Post> {
        return try {
            val request = CreatePostRequest(content, imageUrl)
            val postDto = postApi.createPost(request)
            val post = postDto.toDomain()

            // Add to cache
            postDao.insertPost(postDto.toEntity())

            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error("Failed to create post: ${e.message}", e)
        }
    }

    override suspend fun updatePost(
        postId: String,
        content: String,
        imageUrl: String?
    ): Resource<Post> {
        return try {
            val request = UpdatePostRequest(content, imageUrl)
            val postDto = postApi.updatePost(postId, request)
            val post = postDto.toDomain()

            // Update cache
            postDao.insertPost(postDto.toEntity())

            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error("Failed to update post: ${e.message}", e)
        }
    }

    override suspend fun deletePost(postId: String): Resource<Unit> {
        return try {
            postApi.deletePost(postId)

            // Remove from cache
            postDao.deletePost(postId)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to delete post: ${e.message}", e)
        }
    }

    override suspend fun likePost(postId: String): Resource<Post> {
        return try {
            val postDto = postApi.likePost(postId)
            val post = postDto.toDomain()

            // Update cache
            postDao.updateLikeStatus(postId, true, postDto.likesCount)

            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error("Failed to like post: ${e.message}", e)
        }
    }

    override suspend fun unlikePost(postId: String): Resource<Post> {
        return try {
            val postDto = postApi.unlikePost(postId)
            val post = postDto.toDomain()

            // Update cache
            postDao.updateLikeStatus(postId, false, postDto.likesCount)

            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error("Failed to unlike post: ${e.message}", e)
        }
    }

    override suspend fun savePost(postId: String): Resource<Post> {
        return try {
            val postDto = postApi.savePost(postId)
            val post = postDto.toDomain()

            // Update cache
            postDao.updateSaveStatus(postId, true)

            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error("Failed to save post: ${e.message}", e)
        }
    }

    override suspend fun unsavePost(postId: String): Resource<Post> {
        return try {
            val postDto = postApi.unsavePost(postId)
            val post = postDto.toDomain()

            // Update cache
            postDao.updateSaveStatus(postId, false)

            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error("Failed to unsave post: ${e.message}", e)
        }
    }
}