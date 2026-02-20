package com.hussein.socialmedia.data.auth.model.request

import com.google.gson.annotations.SerializedName

/**
 * Register request DTO
 */
data class RegisterRequest(
    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("age")
    val age: Int = 18,

    @SerializedName("gender")
    val gender: String = "male"
)