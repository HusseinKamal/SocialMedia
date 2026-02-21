package com.hussein.socialmedia.data.profile.remote.model.response

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    @SerializedName("users")
    val users: List<FriendDto>  = emptyList(),

    @SerializedName("total")
    val total: Int,

    @SerializedName("skip")
    val skip: Int,

    @SerializedName("limit")
    val limit: Int
)