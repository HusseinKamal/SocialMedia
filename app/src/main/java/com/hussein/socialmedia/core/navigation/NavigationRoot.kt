package com.hussein.socialmedia.core.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.hussein.socialmedia.presentation.setting.screen.SettingsScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.TodoList,
        topLevelRoutes = TOP_LEVEL_DESTINATION.keys
    )
    val navigator = remember {
        Navigator(navigationState)
    }
    val resultStore = rememberResultStore()

    Scaffold(
        bottomBar = {
            HomeNavigationBar(
                selectKey = navigationState.topLevelRoute,
                onSelected = {
                    navigator.navigate(it)

                }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = navigator::goBack,
            sceneStrategy = rememberListDetailSceneStrategy(),//support Large screens
            transitionSpec = {
                slideInHorizontally {it} + fadeIn() togetherWith
                        slideOutHorizontally{-it} + fadeOut()
            },//Transition during navigation
            popTransitionSpec = {
                slideInHorizontally {-it} + fadeIn() togetherWith
                        slideOutHorizontally{it} + fadeOut()
            },
            predictivePopTransitionSpec = {
                slideInHorizontally {-it} + fadeIn() togetherWith
                        slideOutHorizontally{it} + fadeOut()
            },
            entries = navigationState.toEntries(
                entryProvider {
                    entry<Route.TodoList>(
                        metadata = ListDetailScene.listPane()
                    ) {
                        TodoListScreen(
                            onTodoClick = {
                                navigator.navigate(Route.TodoDetail(it))
                            }
                        )
                    }
                    entry<Route.TodoFavorites>(
                        metadata = ListDetailScene.listPane()
                    ) {
                        TodoListScreen(
                            onTodoClick = {
                                navigator.navigate(Route.TodoDetail(it))
                            }
                        )
                    }
                    entry<Route.TodoDetail>(
                        metadata = ListDetailScene.detailPane()
                    ){
                        TodoDetailScreen(
                            todo = it.todo
                        )
                    }
                    entry<Route.Settings> {
                        SettingsScreen(
                            //resultStore = resultStore,
                            onNavigateBack = {
                                navigator.navigate(Route.ChangeSettings)
                            },
                            onLogoutComplete = {

                            }
                        )
                    }
                    entry<Route.ChangeSettings> {
                        ChangeSettingScreen(
                            onSave = {
                                navigator.goBack()
                            },
                            resultStore = resultStore
                        )
                    }
                }
            )
        )
    }
}