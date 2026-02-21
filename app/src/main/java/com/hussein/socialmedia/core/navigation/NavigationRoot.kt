package com.hussein.socialmedia.core.navigation

import ChatScreen
import com.hussein.socialmedia.presentation.profile.screen.ProfileScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.hussein.socialmedia.presentation.feed.screen.FeedScreen
import com.hussein.socialmedia.presentation.login.screen.LoginScreen
import com.hussein.socialmedia.presentation.profile.screen.EditProfileScreen
import com.hussein.socialmedia.presentation.profile.screen.FriendsList
import com.hussein.socialmedia.presentation.profile.screen.FriendsListScreen
import com.hussein.socialmedia.presentation.register.screen.RegisterScreen
import com.hussein.socialmedia.presentation.setting.screen.SettingsScreen

@Composable
fun NavigationRoot() {
    val navigationState = rememberNavigationState(
        startRoute = Route.Login, // App starts at Splash
        topLevelRoutes = TOP_LEVEL_DESTINATION.keys
    )
    val navigator = remember { Navigator(navigationState) }

    // Logic: Only show bottom bar if the current root is a Tab (Feed, Chat, etc.)
    val isTabSelected = navigationState.topLevelRoute in TOP_LEVEL_DESTINATION.keys

    Scaffold(
        bottomBar = {
            if (isTabSelected) {
                HomeNavigationBar(
                    selectKey = navigationState.topLevelRoute,
                    onSelected = { navigator.navigate(it) }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(if (isTabSelected) innerPadding else PaddingValues(0.dp)),
            onBack = navigator::goBack,
            entries = navigationState.toEntries(
                entryProvider {
                    entry<Route.Register>{
                        RegisterScreen(
                            onNavigateToLogin = {
                                navigator.navigate(Route.Login)
                            },
                            onNavigateToHome = {
                                navigator.navigate(Route.Feed)
                            }
                        )
                    }
                    entry<Route.Login>{
                        LoginScreen(
                            onNavigateToRegister = {
                                navigator.navigate(Route.Register)
                            },
                            onNavigateToHome = {
                                navigator.navigate(Route.Feed)
                            }
                        )
                    }
                    entry<Route.Feed>{
                        FeedScreen(
                            onNavigateToProfile = { navigator.navigate(Route.Profile) },
                            onNavigateToComments = {}
                        )
                    }
                    entry<Route.Chat>{
                        ChatScreen(
                        )
                    }
                    entry<Route.Profile>{
                        ProfileScreen(
                            onNavigateToEditProfile = { navigator.navigate(Route.EditProfile) }
                        )
                    }
                    entry<Route.EditProfile>{
                        EditProfileScreen()
                    }
                    entry<Route.FriendsList>{
                        FriendsListScreen(
                            onNavigateBack = { navigator.goBack() },
                            onNavigateToChat = {
                                navigator.navigate(Route.Chat)
                            }
                        )
                    }
                    entry<Route.Settings> {
                        SettingsScreen(
                            //resultStore = resultStore,
                            onLogoutComplete = {
                            }
                        )
                    }
                }
            )
        )
    }
}