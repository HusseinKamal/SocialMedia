package com.hussein.socialmedia.data.profile.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * User DTO from DummyJSON API
 */
data class FriendDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("maidenName")
    val maidenName: String?,

    @SerializedName("age")
    val age: Int,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("bloodGroup")
    val bloodGroup: String?,

    @SerializedName("height")
    val height: Double?,

    @SerializedName("weight")
    val weight: Double?,

    @SerializedName("eyeColor")
    val eyeColor: String?,

    @SerializedName("birthDate")
    val birthDate: String?,

    @SerializedName("university")
    val university: String?,

    @SerializedName("company")
    val company: CompanyDto?
)
