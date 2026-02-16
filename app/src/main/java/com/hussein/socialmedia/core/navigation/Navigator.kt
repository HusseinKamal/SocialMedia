package com.hussein.socialmedia.core.navigation

import androidx.navigation3.runtime.NavKey

class Navigator(
    val navigationState: NavigationState
) {
    fun navigate(key: NavKey) {
        if(key in navigationState.backStacks.keys){
            navigationState.topLevelRoute = key
        }
        else
        {
            navigationState.backStacks[navigationState.topLevelRoute]?.add(key)
        }
    }
    fun goBack(){
        val currentStack = navigationState.backStacks[navigationState.topLevelRoute] ?: error("Back Stack for ${navigationState.topLevelRoute} not founded")
        val currentRoute = currentStack.last()
        if(currentRoute == navigationState.topLevelRoute){
            navigationState.topLevelRoute = navigationState.startRoute
        }
        else
        {
            currentStack.removeLastOrNull()
        }
    }
}