package com.hussein.socialmedia.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute by topLevelRoute

    val stacksInUse: List<NavKey>
        get() = if(topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {

    // 1. Define all possible roots (Tabs + Auth + Splash)
    // This ensures every major flow has a backstack to hold its entries.
    val allPossibleRoots = remember(topLevelRoutes) {
        topLevelRoutes + setOf(Route.Login, Route.Register)
    }

    // 2. State to track the current active root (Start at Splash or Login)
    val topLevelRouteState = rememberSerializable(
        startRoute,
        allPossibleRoots,
        configuration = serializersConfig,
        serializer = MutableStateSerializer(PolymorphicSerializer(NavKey::class))
    ) {
        mutableStateOf(startRoute)
    }

    // 3. Create a backstack for every possible root
    val backStacks = allPossibleRoots.associateWith { key ->
        rememberNavBackStack(
            configuration = serializersConfig,
            key // The backstack is initialized with the root key itself
        )
    }

    return remember(startRoute, allPossibleRoots) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRouteState,
            backStacks = backStacks
        )
    }
}

val serializersConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Route.Feed::class, Route.Feed.serializer())
            subclass(Route.Chat::class, Route.Chat.serializer())
            subclass(Route.Profile::class, Route.Profile.serializer())
            subclass(Route.Login::class, Route.Login.serializer())
            subclass(Route.Register::class, Route.Register.serializer())
            subclass(Route.Settings::class, Route.Settings.serializer())
            subclass(Route.EditProfile::class, Route.EditProfile.serializer())
            subclass(Route.FriendsList::class, Route.FriendsList.serializer())
        }
    }
}

@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}