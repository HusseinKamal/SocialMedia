package com.hussein.socialmedia

import app.cash.turbine.test
import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.domain.auth.usecase.LoginUseCase
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.presentation.login.event.LoginEffect
import com.hussein.socialmedia.presentation.login.event.LoginUiEvent
import com.hussein.socialmedia.presentation.login.viewmodel.LoginViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive unit tests for LoginViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginUseCase: LoginUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ========== Username Validation Tests ==========

    @Test
    fun `updateUsername with valid username clears error`() = runTest {
        viewModel.onEvent(LoginUiEvent.UsernameChanged("john_doe"))
        advanceUntilIdle()

        assertEquals("john_doe", viewModel.uiState.value.username)
        assertNull(viewModel.uiState.value.usernameError)
    }

    @Test
    fun `updateUsername with short username shows error`() = runTest {
        viewModel.onEvent(LoginUiEvent.UsernameChanged("john"))
        advanceUntilIdle()
        viewModel.onEvent(LoginUiEvent.UsernameChanged("ab"))
        advanceUntilIdle()

        assertEquals("ab", viewModel.uiState.value.username)
        assertEquals("Username must be at least 3 characters", viewModel.uiState.value.usernameError)
    }

    @Test
    fun `updateUsername with empty string clears error`() = runTest {
        viewModel.onEvent(LoginUiEvent.UsernameChanged("ab"))
        advanceUntilIdle()
        viewModel.onEvent(LoginUiEvent.UsernameChanged(""))
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.usernameError)
    }

    // ========== Password Validation Tests ==========

    @Test
    fun `updatePassword with valid password clears error`() = runTest {
        viewModel.onEvent(LoginUiEvent.PasswordChanged("password123"))
        advanceUntilIdle()

        assertEquals("password123", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `updatePassword with short password shows error`() = runTest {
        viewModel.onEvent(LoginUiEvent.PasswordChanged("password"))
        advanceUntilIdle()
        viewModel.onEvent(LoginUiEvent.PasswordChanged("pass"))
        advanceUntilIdle()

        assertEquals("Password must be at least 6 characters", viewModel.uiState.value.passwordError)
    }

    // ========== Password Visibility Tests ==========

    @Test
    fun `togglePasswordVisibility changes state`() = runTest {
        val initialState = viewModel.uiState.value.isPasswordVisible

        viewModel.onEvent(LoginUiEvent.TogglePasswordVisibility)
        advanceUntilIdle()

        assertEquals(!initialState, viewModel.uiState.value.isPasswordVisible)
    }

    // ========== Form Validation Tests ==========

    @Test
    fun `canSubmit is false when username is empty`() = runTest {
        viewModel.onEvent(LoginUiEvent.UsernameChanged(""))
        viewModel.onEvent(LoginUiEvent.PasswordChanged("password123"))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `canSubmit is false when password is empty`() = runTest {
        viewModel.onEvent(LoginUiEvent.UsernameChanged("hussein_kamal"))
        viewModel.onEvent(LoginUiEvent.PasswordChanged(""))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `canSubmit is true when both fields are valid`() = runTest {
        viewModel.onEvent(LoginUiEvent.UsernameChanged("hussein_kamal"))
        viewModel.onEvent(LoginUiEvent.PasswordChanged("password123"))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.canSubmit)
    }

    // ========== Login Success Tests ==========

    @Test
    fun `login with valid credentials returns success`() = runTest {
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { loginUseCase("hussein_kamal", "password123") } returns Resource.Success(authResult)

        viewModel.onEvent(LoginUiEvent.UsernameChanged("hussein_kamal"))
        viewModel.onEvent(LoginUiEvent.PasswordChanged("password123"))
        advanceUntilIdle()

        viewModel.onEvent(LoginUiEvent.Login)
        advanceUntilIdle()

        coVerify { loginUseCase("hussein_kamal", "password123") }
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.loginSuccess)
    }

    @Test
    fun `login success emits NavigateToHome effect`() = runTest {
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { loginUseCase(any(), any()) } returns Resource.Success(authResult)

        viewModel.onEvent(LoginUiEvent.UsernameChanged("hussein_kamal"))
        viewModel.onEvent(LoginUiEvent.PasswordChanged("password123"))
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(LoginUiEvent.Login)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is LoginEffect.NavigateToHome)
        }
    }

    // ========== Login Error Tests ==========

    @Test
    fun `login with invalid credentials shows error`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Resource.Error("Invalid username or password")

        viewModel.onEvent(LoginUiEvent.UsernameChanged("hussein_kamal"))
        viewModel.onEvent(LoginUiEvent.PasswordChanged("wrongpass"))
        advanceUntilIdle()

        viewModel.onEvent(LoginUiEvent.Login)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.loginSuccess)
        assertEquals("Invalid username or password", viewModel.uiState.value.error)
    }

    @Test
    fun `login sets isLoading during API call`() = runTest {
        coEvery { loginUseCase(any(), any()) } coAnswers {
            delay(100) // Simulate network delay
            Resource.Success(AuthResult(createMockAuthUser(), "token", "refresh"))
        }

        viewModel.onEvent(LoginUiEvent.UsernameChanged("hussein_kamal"))
        viewModel.onEvent(LoginUiEvent.PasswordChanged("password123"))
        advanceUntilIdle()

        viewModel.onEvent(LoginUiEvent.Login)

        // FIX: runCurrent() triggers the code inside the ViewModel coroutine
        // to run until it hits the suspension point (the UseCase call).
        runCurrent()

        assertTrue("Loading state should be true after calling login", viewModel.uiState.value.isLoading)

        advanceUntilIdle() // Finish the delay and the rest of the coroutine
        assertFalse("Loading state should be false after API call finished", viewModel.uiState.value.isLoading)
    }

    // ========== Navigation Tests ==========

    @Test
    fun `navigateToRegister emits correct effect`() = runTest {
        viewModel.effect.test {
            viewModel.onEvent(LoginUiEvent.NavigateToRegister)

            val effect = awaitItem()
            assertTrue(effect is LoginEffect.NavigateToRegister)
        }
    }

    // ========== Helper Functions ==========

    private fun createMockAuthUser() = AuthUser(
        id = "1",
        username = "hussein_kamal",
        email = "test@example.com",
        firstName = "Hussein",
        lastName = "Kamal",
        fullName = "Hussein Kamal",
        image = null,
        gender = "male"
    )
}