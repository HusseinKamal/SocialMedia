package com.hussein.socialmedia.data.feed.remote.model.request
/**
 * Request models for API calls
 */

data class CreatePostRequest(
    val content: String,
    val imageUrl: String?
)

data class UpdatePostRequest(
    val content: String,
    val imageUrl: String?
)