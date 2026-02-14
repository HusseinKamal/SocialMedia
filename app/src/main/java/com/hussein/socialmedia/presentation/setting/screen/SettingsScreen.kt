package com.hussein.socialmedia.presentation.setting.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hussein.socialmedia.domain.setting.model.FeedLayout
import com.hussein.socialmedia.domain.setting.model.ThemeMode
import com.hussein.socialmedia.presentation.setting.event.SettingsUiEvent
import com.hussein.socialmedia.presentation.setting.state.SettingsUiState
import com.hussein.socialmedia.presentation.setting.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(),
    onNavigateBack: () -> Unit = {},
    onLogoutComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle logout success
    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            onLogoutComplete()
        }
    }

    // Handle errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(SettingsUiEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            SettingsContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        // Account Section
        uiState.userSession?.let { session ->
            SettingsSection(title = "Account") {
                SettingsItem(
                    title = "Email",
                    subtitle = session.email,
                    icon = Icons.Default.Email
                )
                SettingsItem(
                    title = "User ID",
                    subtitle = session.userId,
                    icon = Icons.Default.Person
                )
            }

            HorizontalDivider()
        }

        // Appearance Section
        SettingsSection(title = "Appearance") {
            ThemeSettingItem(
                currentTheme = uiState.settings.themeMode,
                onThemeChange = { onEvent(SettingsUiEvent.UpdateThemeMode(it)) }
            )

            FeedLayoutSettingItem(
                currentLayout = uiState.settings.feedLayout,
                onLayoutChange = { onEvent(SettingsUiEvent.UpdateFeedLayout(it)) }
            )
        }

        HorizontalDivider()

        // Notifications Section
        SettingsSection(title = "Notifications") {
            SwitchSettingsItem(
                title = "Enable Notifications",
                subtitle = "Receive push notifications",
                icon = Icons.Default.Notifications,
                checked = uiState.settings.notificationsEnabled,
                onCheckedChange = { onEvent(SettingsUiEvent.UpdateNotifications(it)) }
            )
        }

        HorizontalDivider()

        // Privacy Section
        SettingsSection(title = "Privacy") {
            SwitchSettingsItem(
                title = "Show Online Status",
                subtitle = "Let others see when you're online",
                icon = Icons.Default.Visibility,
                checked = uiState.settings.showOnlineStatus,
                onCheckedChange = { onEvent(SettingsUiEvent.UpdateShowOnlineStatus(it)) }
            )

            SwitchSettingsItem(
                title = "Read Receipts",
                subtitle = "Let others see when you've read messages",
                icon = Icons.Default.DoneAll,
                checked = uiState.settings.readReceiptsEnabled,
                onCheckedChange = { onEvent(SettingsUiEvent.UpdateReadReceipts(it)) }
            )
        }

        HorizontalDivider()

        // Media Section
        SettingsSection(title = "Media & Storage") {
            SwitchSettingsItem(
                title = "Auto-play Videos",
                subtitle = "Videos play automatically in feed",
                icon = Icons.Default.PlayArrow,
                checked = uiState.settings.autoPlayVideos,
                onCheckedChange = { onEvent(SettingsUiEvent.UpdateAutoPlayVideos(it)) }
            )
        }

        HorizontalDivider()

        // Language Section
        SettingsSection(title = "Language") {
            SettingsItem(
                title = "Language",
                subtitle = uiState.settings.language.uppercase(),
                icon = Icons.Default.Language,
                onClick = { /* Show language picker */ }
            )
        }

        HorizontalDivider()

        // Logout Section
        SettingsSection(title = "Account Actions") {
            SettingsItem(
                title = "Logout",
                subtitle = "Sign out of your account",
                icon = Icons.Default.Logout,
                onClick = { onEvent(SettingsUiEvent.Logout) },
                tint = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)? = null,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = { onClick?.invoke() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = tint
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SwitchSettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun ThemeSettingItem(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { expanded = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = when (currentTheme) {
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                        ThemeMode.SYSTEM -> "System Default"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Light") },
            onClick = {
                onThemeChange(ThemeMode.LIGHT)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text("Dark") },
            onClick = {
                onThemeChange(ThemeMode.DARK)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text("System Default") },
            onClick = {
                onThemeChange(ThemeMode.SYSTEM)
                expanded = false
            }
        )
    }
}

@Composable
fun FeedLayoutSettingItem(
    currentLayout: FeedLayout,
    onLayoutChange: (FeedLayout) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { expanded = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ViewModule,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Feed Layout",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = when (currentLayout) {
                        FeedLayout.GRID -> "Grid"
                        FeedLayout.LIST -> "List"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Grid") },
            onClick = {
                onLayoutChange(FeedLayout.GRID)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text("List") },
            onClick = {
                onLayoutChange(FeedLayout.LIST)
                expanded = false
            }
        )
    }
}