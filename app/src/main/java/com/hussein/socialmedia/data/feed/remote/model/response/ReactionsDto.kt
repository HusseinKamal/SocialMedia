package com.hussein.socialmedia.data.feed.remote.model.response

import com.google.gson.annotations.SerializedName

data class ReactionsDto(
    @SerializedName("likes")
    val likes: Int?,

    @SerializedName("dislikes")
    val dislikes: Int?
)
