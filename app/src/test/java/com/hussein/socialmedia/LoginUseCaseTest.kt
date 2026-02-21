package com.hussein.socialmedia

import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.domain.auth.repository.AuthRepository
import com.hussein.socialmedia.domain.auth.usecase.LoginUseCase
import com.hussein.socialmedia.domain.util.Resource
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for LoginUseCase
 */
class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authRepository = mockk()
        loginUseCase = LoginUseCase(authRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // ========== Validation Tests ==========

    @Test
    fun `invoke with empty username returns error`() = runTest {
        // When
        val result = loginUseCase("", "password123")

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Username cannot be empty", (result as Resource.Error).message)
        coVerify(exactly = 0) { authRepository.login(any()) }
    }

    @Test
    fun `invoke with blank username returns error`() = runTest {
        // When
        val result = loginUseCase("   ", "password123")

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Username cannot be empty", (result as Resource.Error).message)
    }

    @Test
    fun `invoke with empty password returns error`() = runTest {
        // When
        val result = loginUseCase("john_doe", "")

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Password cannot be empty", (result as Resource.Error).message)
        coVerify(exactly = 0) { authRepository.login(any()) }
    }

    @Test
    fun `invoke with blank password returns error`() = runTest {
        // When
        val result = loginUseCase("john_doe", "   ")

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Password cannot be empty", (result as Resource.Error).message)
    }

    // ========== Success Tests ==========

    @Test
    fun `invoke with valid credentials calls repository`() = runTest {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { authRepository.login(any()) } returns Resource.Success(authResult)

        // When
        val result = loginUseCase("hussein_kamal", "password123") // Updated username

        // Then
        coVerify {
            authRepository.login(
                withArg { credentials ->
                    assertEquals("hussein_kamal", credentials.username) // Updated username
                    assertEquals("password123", credentials.password)
                }
            )
        }
        assertTrue(result is Resource.Success)
        assertEquals(authResult, (result as Resource.Success).data)
    }

    @Test
    fun `invoke trims username before calling repository`() = runTest {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { authRepository.login(any()) } returns Resource.Success(authResult)

        // When
        loginUseCase("  hussein_kamal  ", "password123") // Updated username

        // Then
        coVerify {
            authRepository.login(
                withArg { credentials ->
                    assertEquals("hussein_kamal", credentials.username) // Updated username
                }
            )
        }
    }

    @Test
    fun `invoke does not trim password`() = runTest {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { authRepository.login(any()) } returns Resource.Success(authResult)

        // When
        loginUseCase("hussein_kamal", "  password123  ") // Updated username

        // Then
        coVerify {
            authRepository.login(
                withArg { credentials ->
                    assertEquals("  password123  ", credentials.password)
                }
            )
        }
    }

    // ========== Error Tests ==========

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Given
        val errorMessage = "Invalid credentials"
        coEvery { authRepository.login(any()) } returns Resource.Error(errorMessage)

        // When
        val result = loginUseCase("hussein_kamal", "wrongpass") // Updated username

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)
    }

    @Test
    fun `invoke returns network error from repository`() = runTest {
        // Given
        val errorMessage = "Network error. Please check your connection."
        coEvery { authRepository.login(any()) } returns Resource.Error(errorMessage)

        // When
        val result = loginUseCase("hussein_kamal", "password123") // Updated username

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)
    }

    // ========== Edge Cases ==========

    @Test
    fun `invoke with special characters in password succeeds`() = runTest {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { authRepository.login(any()) } returns Resource.Success(authResult)

        // When
        val result = loginUseCase("hussein_kamal", "P@ssw0rd!#$") // Updated username

        // Then
        assertTrue(result is Resource.Success)
        coVerify {
            authRepository.login(
                withArg { credentials ->
                    assertEquals("P@ssw0rd!#$", credentials.password)
                }
            )
        }
    }

    @Test
    fun `invoke with unicode characters in username succeeds`() = runTest {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { authRepository.login(any()) } returns Resource.Success(authResult)

        // When
        val result = loginUseCase("jöhn_döe", "password123")

        // Then
        assertTrue(result is Resource.Success)
        coVerify {
            authRepository.login(
                withArg { credentials ->
                    assertEquals("jöhn_döe", credentials.username)
                }
            )
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