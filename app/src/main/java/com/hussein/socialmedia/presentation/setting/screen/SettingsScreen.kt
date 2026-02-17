package com.hussein.socialmedia.presentation.setting.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hussein.socialmedia.domain.setting.model.FeedLayout
import com.hussein.socialmedia.domain.setting.model.ThemeMode
import com.hussein.socialmedia.presentation.setting.event.SettingsUiEvent
import com.hussein.socialmedia.presentation.setting.state.SettingsUiState
import com.hussein.socialmedia.presentation.setting.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onLogoutComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // 1. Header (Tighter padding)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            SettingsContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
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
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // --- Account Section ---
        uiState.userSession?.let { session ->
            item(key = "section_account") {
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
            }
            item(key = "divider_account") {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))
            }
        }

        // --- Appearance Section ---
        item(key = "section_appearance") {
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
        }

        item(key = "divider_appearance") {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))
        }

        // --- Notifications Section ---
        item(key = "section_notifications") {
            SettingsSection(title = "Notifications") {
                SwitchSettingsItem(
                    title = "Enable Notifications",
                    subtitle = "Receive push notifications",
                    icon = Icons.Default.Notifications,
                    checked = uiState.settings.notificationsEnabled,
                    onCheckedChange = { onEvent(SettingsUiEvent.UpdateNotifications(it)) }
                )
            }
        }

        item(key = "divider_notifications") {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))
        }

        // --- Privacy Section ---
        item(key = "section_privacy") {
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
        }

        item(key = "divider_privacy") {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))
        }

        // --- Actions Section (Logout) ---
        item(key = "section_actions") {
            SettingsSection(title = "Account Actions") {
                SettingsItem(
                    title = "Logout",
                    subtitle = "Sign out of your account",
                    icon = Icons.Default.Logout,
                    onClick = { onEvent(SettingsUiEvent.Logout) },
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        // No extra Spacers here! LazyColumn handles the end of the list automatically.
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
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface
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
    icon: ImageVector,
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