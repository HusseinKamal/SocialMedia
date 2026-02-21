package com.hussein.socialmedia

import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.presentation.register.event.RegisterUiEvent


import app.cash.turbine.test
import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.usecase.RegisterUseCase
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.presentation.register.event.RegisterEffect
import com.hussein.socialmedia.presentation.register.viewmodel.RegisterViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive unit tests for RegisterViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var registerUseCase: RegisterUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        registerUseCase = mockk()
        viewModel = RegisterViewModel(registerUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ========== First Name Validation Tests ==========

    @Test
    fun `updateFirstName with valid name clears error`() = runTest {
        viewModel.onEvent(RegisterUiEvent.FirstNameChanged("Hussein"))
        runCurrent() // Use runCurrent for immediate state updates

        assertEquals("Hussein", viewModel.uiState.value.firstName) // Changed from "John"
        assertNull(viewModel.uiState.value.firstNameError)
    }

    @Test
    fun `updateFirstName with short name shows error`() = runTest {
        viewModel.onEvent(RegisterUiEvent.FirstNameChanged("H"))
        runCurrent()

        assertEquals("First name must be at least 2 characters", viewModel.uiState.value.firstNameError)
    }

    // ========== Username Validation Tests ==========

    @Test
    fun `updateUsername with short username shows error`() = runTest {
        viewModel.onEvent(RegisterUiEvent.UsernameChanged("hu")) // Changed from "asser_hussein"
        runCurrent()

        assertEquals("Username must be at least 3 characters", viewModel.uiState.value.usernameError)
    }

    // ========== Registration Success Tests ==========

    @Test
    fun `register with valid data returns success`() = runTest {
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery {
            registerUseCase(any(), any(), any(), any(), any(), any())
        } returns Resource.Success(authResult)

        fillValidRegistrationForm()
        runCurrent()

        viewModel.onEvent(RegisterUiEvent.Register)
        advanceUntilIdle()

        coVerify { registerUseCase("Hussein", "Kamal", "hussein_kamal", "test@example.com", "password123", "password123") }
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.registerSuccess)
    }

    // ========== Registration Error Tests ==========

    @Test
    fun `register sets isLoading during API call`() = runTest {
        coEvery {
            registerUseCase(any(), any(), any(), any(), any(), any())
        } coAnswers {
            delay(100) // Suspension point
            Resource.Success(AuthResult(createMockAuthUser(), "token", "refresh"))
        }

        fillValidRegistrationForm()
        runCurrent()

        viewModel.onEvent(RegisterUiEvent.Register)

        // Capture the state immediately after event starts but during the delay
        runCurrent()
        assertTrue("Loading should be true after triggering register", viewModel.uiState.value.isLoading)

        advanceUntilIdle() // Finish the delay
        assertFalse("Loading should be false after API completes", viewModel.uiState.value.isLoading)
    }

    // ========== Navigation Tests ==========

    @Test
    fun `navigateToLogin emits correct effect`() = runTest {
        viewModel.effect.test {
            viewModel.onEvent(RegisterUiEvent.NavigateToLogin)
            val effect = awaitItem()
            assertTrue(effect is RegisterEffect.NavigateToLogin)
        }
    }

    // ========== Validation Tests ==========

    @Test
    fun `validateFields shows all validation errors`() = runTest {
        viewModel.onEvent(RegisterUiEvent.FirstNameChanged("H"))
        viewModel.onEvent(RegisterUiEvent.LastNameChanged("K"))
        viewModel.onEvent(RegisterUiEvent.UsernameChanged("hu"))
        viewModel.onEvent(RegisterUiEvent.EmailChanged("invalid"))
        viewModel.onEvent(RegisterUiEvent.PasswordChanged("123"))
        viewModel.onEvent(RegisterUiEvent.ConfirmPasswordChanged("456"))
        runCurrent()

        viewModel.onEvent(RegisterUiEvent.ValidateFields)
        runCurrent()

        assertNotNull(viewModel.uiState.value.firstNameError)
        assertNotNull(viewModel.uiState.value.lastNameError)
        assertNotNull(viewModel.uiState.value.usernameError)
        assertNotNull(viewModel.uiState.value.emailError)
        assertNotNull(viewModel.uiState.value.passwordError)
        assertNotNull(viewModel.uiState.value.confirmPasswordError)
    }

    // ========== Helper Functions ==========

    private fun fillValidRegistrationForm() {
        viewModel.onEvent(RegisterUiEvent.FirstNameChanged("Hussein"))
        viewModel.onEvent(RegisterUiEvent.LastNameChanged("Kamal"))
        viewModel.onEvent(RegisterUiEvent.UsernameChanged("hussein_kamal"))
        viewModel.onEvent(RegisterUiEvent.EmailChanged("test@example.com"))
        viewModel.onEvent(RegisterUiEvent.PasswordChanged("password123"))
        viewModel.onEvent(RegisterUiEvent.ConfirmPasswordChanged("password123"))
    }

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