package com.hussein.socialmedia.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {

    @Serializable
    data object Feed : Route
    @Serializable
    data object Chat : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object Settings : Route
}