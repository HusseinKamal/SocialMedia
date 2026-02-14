package com.hussein.socialmedia
import com.hussein.socialmedia.domain.setting.model.AppSettings
import com.hussein.socialmedia.domain.setting.model.FeedLayout
import com.hussein.socialmedia.domain.setting.model.ThemeMode
import com.hussein.socialmedia.domain.setting.model.UserSession
import com.hussein.socialmedia.domain.setting.usecase.GetAppSettingsUseCase
import com.hussein.socialmedia.domain.setting.usecase.GetUserSessionUseCase
import com.hussein.socialmedia.domain.setting.usecase.LogoutUseCase
import com.hussein.socialmedia.domain.setting.usecase.SaveUserSessionUseCase
import com.hussein.socialmedia.domain.setting.usecase.UpdateAppSettingsUseCase
import com.hussein.socialmedia.presentation.setting.event.SettingsUiEvent
import com.hussein.socialmedia.presentation.setting.viewmodel.SettingsViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SettingsViewModel.
 * Tests settings management with encrypted preferences and DataStore.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var getAppSettingsUseCase: GetAppSettingsUseCase
    private lateinit var updateAppSettingsUseCase: UpdateAppSettingsUseCase
    private lateinit var saveUserSessionUseCase: SaveUserSessionUseCase
    private lateinit var getUserSessionUseCase: GetUserSessionUseCase
    private lateinit var logoutUseCase: LogoutUseCase

    private val testDispatcher = StandardTestDispatcher()

    private val mockSettings = AppSettings(
        themeMode = ThemeMode.DARK,
        language = "en",
        notificationsEnabled = true,
        autoPlayVideos = false,
        showOnlineStatus = true,
        readReceiptsEnabled = true,
        feedLayout = FeedLayout.GRID,
        autoDownloadMedia = true
    )

    private val mockSession = UserSession(
        authToken = "test_token",
        refreshToken = "refresh_token",
        userId = "user123",
        email = "test@example.com",
        isLoggedIn = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create mocks
        getAppSettingsUseCase = mockk()
        updateAppSettingsUseCase = mockk()
        saveUserSessionUseCase = mockk()
        getUserSessionUseCase = mockk()
        logoutUseCase = mockk()

        // Setup default behavior
        every { getAppSettingsUseCase() } returns flowOf(mockSettings)
        coEvery { getUserSessionUseCase() } returns mockSession
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state should load settings and session`() = runTest {
        // Given
        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        verify { getAppSettingsUseCase() }
        coVerify { getUserSessionUseCase() }

        val state = viewModel.uiState.value
        assertEquals(mockSettings, state.settings)
        assertEquals(mockSession, state.userSession)
        assertFalse(state.isLoading)
    }

    @Test
    fun `updateThemeMode should update settings`() = runTest {
        // Given
        coEvery { updateAppSettingsUseCase(any()) } just Runs

        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.onEvent(SettingsUiEvent.UpdateThemeMode(ThemeMode.LIGHT))
        advanceUntilIdle()

        // Then
        coVerify {
            updateAppSettingsUseCase(
                withArg { settings ->
                    assertEquals(ThemeMode.LIGHT, settings.themeMode)
                }
            )
        }
    }

    @Test
    fun `updateNotifications should update settings`() = runTest {
        // Given
        coEvery { updateAppSettingsUseCase(any()) } just Runs

        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.onEvent(SettingsUiEvent.UpdateNotifications(false))
        advanceUntilIdle()

        // Then
        coVerify {
            updateAppSettingsUseCase(
                withArg { settings ->
                    assertFalse(settings.notificationsEnabled)
                }
            )
        }
    }

    @Test
    fun `updateFeedLayout should update settings`() = runTest {
        // Given
        coEvery { updateAppSettingsUseCase(any()) } just Runs

        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.onEvent(SettingsUiEvent.UpdateFeedLayout(FeedLayout.LIST))
        advanceUntilIdle()

        // Then
        coVerify {
            updateAppSettingsUseCase(
                withArg { settings ->
                    assertEquals(FeedLayout.LIST, settings.feedLayout)
                }
            )
        }
    }

    @Test
    fun `saveUserSession should update state with session`() = runTest {
        // Given
        val newSession = mockSession.copy(userId = "newUser123")
        coEvery { saveUserSessionUseCase(any()) } just Runs

        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.onEvent(SettingsUiEvent.SaveUserSession(newSession))
        advanceUntilIdle()

        // Then
        coVerify { saveUserSessionUseCase(newSession) }
        assertEquals(newSession, viewModel.uiState.value.userSession)
        assertTrue(viewModel.uiState.value.saveSuccess)
    }

    @Test
    fun `logout should clear session and update state`() = runTest {
        // Given
        coEvery { logoutUseCase() } just Runs

        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.onEvent(SettingsUiEvent.Logout)
        advanceUntilIdle()

        // Then
        coVerify { logoutUseCase() }
        assertNull(viewModel.uiState.value.userSession)
        assertTrue(viewModel.uiState.value.logoutSuccess)
    }

    @Test
    fun `saveAuthToken should update session with new token`() = runTest {
        // Given
        val newToken = "new_auth_token"
        coEvery { saveUserSessionUseCase(any()) } just Runs

        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.saveAuthToken(newToken)
        advanceUntilIdle()

        // Then
        coVerify {
            saveUserSessionUseCase(
                withArg { session ->
                    assertEquals(newToken, session.authToken)
                }
            )
        }
    }

    @Test
    fun `updateAllSettings should update all settings at once`() = runTest {
        // Given
        coEvery { updateAppSettingsUseCase(any()) } just Runs

        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.updateAllSettings(
            themeMode = ThemeMode.LIGHT,
            notificationsEnabled = false,
            autoPlayVideos = true,
            showOnlineStatus = false,
            readReceiptsEnabled = false,
            feedLayout = FeedLayout.LIST
        )
        advanceUntilIdle()

        // Then
        coVerify {
            updateAppSettingsUseCase(
                withArg { settings ->
                    assertEquals(ThemeMode.LIGHT, settings.themeMode)
                    assertFalse(settings.notificationsEnabled)
                    assertTrue(settings.autoPlayVideos)
                    assertFalse(settings.showOnlineStatus)
                    assertFalse(settings.readReceiptsEnabled)
                    assertEquals(FeedLayout.LIST, settings.feedLayout)
                }
            )
        }
    }

    @Test
    fun `error in saveUserSession should update error state`() = runTest {
        // Given
        val errorMessage = "Failed to save session"
        coEvery { saveUserSessionUseCase(any()) } throws Exception(errorMessage)

        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.onEvent(SettingsUiEvent.SaveUserSession(mockSession))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.error?.contains("Failed to save session") == true)
        assertFalse(viewModel.uiState.value.saveSuccess)
    }

    @Test
    fun `clearError should clear error state`() = runTest {
        // Given
        coEvery { saveUserSessionUseCase(any()) } throws Exception("Error")

        viewModel = SettingsViewModel(
            getAppSettingsUseCase,
            updateAppSettingsUseCase,
            saveUserSessionUseCase,
            getUserSessionUseCase,
            logoutUseCase
        )
        advanceUntilIdle()

        viewModel.onEvent(SettingsUiEvent.SaveUserSession(mockSession))
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        // When
        viewModel.onEvent(SettingsUiEvent.ClearError)

        // Then
        assertNull(viewModel.uiState.value.error)
    }
}