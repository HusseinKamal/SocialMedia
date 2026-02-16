package com.hussein.socialmedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hussein.socialmedia.core.navigation.NavigationRoot
import com.hussein.socialmedia.presentation.feed.screen.FeedScreen
import com.hussein.socialmedia.presentation.setting.screen.SettingsScreen
import com.hussein.socialmedia.ui.theme.SocialMediaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SocialMediaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationRoot()
                   /* FeedScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToProfile = {},
                        onNavigateToComments = {}
                    )*/
                    /*SettingsScreen(
                        onNavigateBack = {},
                        onLogoutComplete = {}
                    )*/
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SocialMediaTheme {
        FeedScreen(
            modifier = Modifier.padding(10.dp),
            onNavigateToProfile = {},
            onNavigateToComments = {}
        )
    }
}