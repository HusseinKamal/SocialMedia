package com.hussein.socialmedia.presentation.feed.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.hussein.socialmedia.presentation.feed.event.FeedEffect
import com.hussein.socialmedia.presentation.feed.event.FeedUiEvent
import com.hussein.socialmedia.presentation.feed.model.PostUiModel
import com.hussein.socialmedia.presentation.feed.viewmodel.FeedViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel<FeedViewModel>(),
    onNavigateToProfile: (String) -> Unit,
    onNavigateToComments: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val posts = uiState.posts.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FeedEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is FeedEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                is FeedEffect.NavigateToProfile -> onNavigateToProfile(effect.userId)
                is FeedEffect.NavigateToComments -> onNavigateToComments(effect.postId)
            }
        }
    }

    // Use Box to overlay Snackbar on top of the Column
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // 1. Custom Header (Replaces TopAppBar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Feed",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.onEvent(FeedUiEvent.Refresh) }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

            // 2. Feed Content
            // We use weight(1f) to ensure the list takes all space but
            // stops exactly at the Bottom Navigation Bar
            FeedContent(
                posts = posts,
                onEvent = viewModel::onEvent,
                modifier = Modifier.weight(1f)
            )
        }

        // 3. Manual Snackbar Host (Since Scaffold is gone)
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun FeedContent(
    posts: LazyPagingItems<PostUiModel>,
    modifier: Modifier = Modifier,
    onEvent: (FeedUiEvent) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            count = posts.itemCount,
            key = { index -> posts[index]?.id ?: index }
        ) { index ->
            posts[index]?.let { post ->
                PostItem(
                    post = post,
                    onLikeClick = {
                        if (post.isLiked) {
                            onEvent(FeedUiEvent.UnlikePost(post.id))
                        } else {
                            onEvent(FeedUiEvent.LikePost(post.id))
                        }
                    },
                    onCommentClick = {
                        onEvent(FeedUiEvent.NavigateToComments(post.id))
                    },
                    onSaveClick = {
                        if (post.isSaved) {
                            onEvent(FeedUiEvent.UnsavePost(post.id))
                        } else {
                            onEvent(FeedUiEvent.SavePost(post.id))
                        }
                    },
                    onProfileClick = {
                        onEvent(FeedUiEvent.NavigateToProfile(post.userId))
                    }
                )
            }
        }

        // Loading indicator
        posts.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                loadState.refresh is LoadState.Error -> {
                    val error = (loadState.refresh as LoadState.Error).error
                    item {
                        ErrorItem(
                            message = error.message ?: "Unknown error",
                            onRetry = { posts.retry() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(
    post: PostUiModel,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onSaveClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.userAvatarUrl,
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.username,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = post.formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { /* More options */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium
            )

            // Image if available
            post.imageUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    IconButton(onClick = onLikeClick) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = post.likesCount.toString(),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = onCommentClick) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comment")
                    }
                    Text(
                        text = post.commentsCount.toString(),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }

                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save"
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorItem(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}