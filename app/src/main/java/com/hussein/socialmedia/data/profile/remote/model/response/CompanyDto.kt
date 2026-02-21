package com.hussein.socialmedia.data.profile.remote.model.response

import com.google.gson.annotations.SerializedName

data class CompanyDto(
    @SerializedName("name")
    val name: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("department")
    val department: String?
)