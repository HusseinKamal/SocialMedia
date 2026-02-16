package com.hussein.socialmedia.core.navigation

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey

@Composable
fun HomeNavigationBar(
    modifier: Modifier = Modifier,
    selectKey : NavKey,
    onSelected : (NavKey) -> Unit
) {
    BottomAppBar(
        modifier = modifier
    ){
        TOP_LEVEL_DESTINATION.forEach { (key, value) ->
            NavigationBarItem(
                selected = key == selectKey,
                onClick = { onSelected(key) },
                icon = {
                    Icon(
                        imageVector = value.icon,
                        contentDescription = value.title
                    )
                },
                label = {
                    Text(text = value.title)
                }
            )
        }
    }
}