package com.hussein.socialmedia.presentation.profile.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hussein.socialmedia.presentation.profile.event.FriendsListEffect
import com.hussein.socialmedia.presentation.profile.event.FriendsListUiEvent
import com.hussein.socialmedia.presentation.profile.mapper.getAvatarColor
import com.hussein.socialmedia.presentation.profile.model.FriendUiModel
import com.hussein.socialmedia.presentation.profile.viewmodel.FriendsListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsListScreen(
    viewModel: FriendsListViewModel = hiltViewModel<FriendsListViewModel>(),
    onNavigateBack: () -> Unit = {},
    onNavigateToChat: (FriendUiModel) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FriendsListEffect.NavigateToChat -> {
                    onNavigateToChat(effect.friend)
                }
            }
        }
    }

    // Show error snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(FriendsListUiEvent.ClearError)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text("Friends List" , modifier = Modifier.padding(16.dp))

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))

        // Search Bar
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onEvent(FriendsListUiEvent.SearchQueryChanged(it)) },
            isSearching = uiState.isSearching,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Content
        PullToRefreshBox(
            state = rememberPullToRefreshState(),
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.onEvent(FriendsListUiEvent.Refresh) }
        ) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                uiState.isEmpty -> {
                    EmptyContent(message = "No friends found")
                }
                uiState.searchQuery.isNotEmpty() && uiState.searchResultsEmpty -> {
                    EmptyContent(message = "No results for \"${uiState.searchQuery}\"")
                }
                else -> {
                    FriendsList(
                        friends = uiState.filteredFriends,
                        onFriendClick = { friend ->
                            viewModel.onEvent(FriendsListUiEvent.FriendClicked(friend))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search friends...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large
    )
}

@Composable
fun FriendsList(
    friends: List<FriendUiModel>,
    onFriendClick: (FriendUiModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(friends, key = { it.id }) { friend ->
            FriendItem(
                friend = friend,
                onClick = { onFriendClick(friend) }
            )
        }
    }
}

@Composable
fun FriendItem(
    friend: FriendUiModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                if (friend.avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = friend.avatarUrl,
                        contentDescription = "${friend.fullName}'s avatar",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder with initials
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(friend.getAvatarColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = friend.initials,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Online status indicator
                if (friend.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(friend.statusColor)
                            .padding(2.dp)
                            .background(Color.White, CircleShape)
                            .background(friend.statusColor)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Friend info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = friend.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = friend.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!friend.isOnline && friend.statusText != "Offline") {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = friend.statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = friend.statusColor
                    )
                }
            }

            // Online status text
            if (friend.isOnline) {
                Text(
                    text = "Online",
                    style = MaterialTheme.typography.labelSmall,
                    color = friend.statusColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading friends...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}