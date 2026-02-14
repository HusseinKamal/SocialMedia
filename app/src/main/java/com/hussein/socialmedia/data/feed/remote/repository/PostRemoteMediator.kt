package com.hussein.socialmedia.data.feed.remote.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hussein.socialmedia.data.database.SocialMediaDatabase
import com.hussein.socialmedia.data.feed.local.PostEntity
import com.hussein.socialmedia.data.feed.remote.mapper.toEntity
import com.hussein.socialmedia.data.feed.remote.service.PostApi
import retrofit2.HttpException
import java.io.IOException

/**
 * RemoteMediator for Posts using Paging 3.
 * Handles loading data from network and caching in Room database.
 */
@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postApi: PostApi,
    private val database: SocialMediaDatabase
) : RemoteMediator<Int, PostEntity>() {

    private val postDao = database.postDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        return try {
            // Determine the page to load based on LoadType
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // For simplicity, we'll use page calculation based on data size
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        // Calculate next page (this is a simplified approach)
                        (state.pages.size) + 1
                    }
                }
            }

            // Fetch data from API
            val response = postApi.getFeedPosts(
                page = page,
                pageSize = state.config.pageSize
            )

            // Store data in database
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDao.deleteAllPosts()
                }

                val postEntities = response.posts.map { it.toEntity() }
                postDao.insertPosts(postEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = !response.hasMore
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}