package com.hussein.socialmedia.data.auth.model.response

import com.google.gson.annotations.SerializedName

/**
 * User response DTO
 */
data class UserResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("image")
    val image: String?,

    @SerializedName("age")
    val age: Int?
)