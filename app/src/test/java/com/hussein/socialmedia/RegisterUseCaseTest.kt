package com.hussein.socialmedia

import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.domain.auth.repository.AuthRepository
import com.hussein.socialmedia.domain.auth.usecase.RegisterUseCase
import com.hussein.socialmedia.domain.util.Resource
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.* // FIX: Use only JUnit 4 Assert
import org.junit.Before
import org.junit.Test

class RegisterUseCaseTest {

    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authRepository = mockk()
        registerUseCase = RegisterUseCase(authRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // ========== First Name Validation Tests ==========

    @Test
    fun `invoke with empty first name returns error`() = runTest {
        val result = registerUseCase("", "Kamal", "hussein_kamal", "test@example.com", "password123", "password123")

        assertTrue(result is Resource.Error)
        assertEquals("First name cannot be empty", (result as Resource.Error).message)
        coVerify(exactly = 0) { authRepository.register(any()) }
    }

    // ========== Email Validation Tests ==========

    @Test
    fun `invoke with invalid email returns error`() = runTest {
        val result = registerUseCase("Hussein", "Kamal", "hussein_kamal", "invalid-email", "password123", "password123")

        assertTrue(result is Resource.Error)
        assertEquals("Please enter a valid email address", (result as Resource.Error).message)
    }

    @Test
    fun `invoke with valid email formats succeeds`() = runTest {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { authRepository.register(any()) } returns Resource.Success(authResult)

        // Test various valid email formats (including + and subdomains)
        val validEmails = listOf(
            "hussein@example.com",
            "hussein.kamal@example.com",
            "hussein+test@example.co.uk",
            "hussein_kamal@sub.example.com"
        )

        validEmails.forEach { email ->
            val result = registerUseCase("Hussein", "Kamal", "hussein_kamal", email, "password123", "password123")
            assertTrue("Email $email should be valid", result is Resource.Success)
        }
    }

    // ========== Password Validation Tests ==========

    @Test
    fun `invoke with non-matching passwords returns error`() = runTest {
        val result = registerUseCase("Hussein", "Kamal", "hussein_kamal", "test@example.com", "password123", "different")

        assertTrue(result is Resource.Error)
        assertEquals("Passwords do not match", (result as Resource.Error).message)
    }

    // ========== Success Tests ==========

    @Test
    fun `invoke with valid data calls repository`() = runTest {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { authRepository.register(any()) } returns Resource.Success(authResult)

        // When
        val result = registerUseCase(
            "Hussein",
            "Kamal",
            "hussein_kamal",
            "test@example.com",
            "password123",
            "password123"
        )

        // Then
        coVerify {
            authRepository.register(
                withArg { registerData ->
                    assertEquals("Hussein", registerData.firstName)
                    assertEquals("Kamal", registerData.lastName)
                    assertEquals("hussein_kamal", registerData.username)
                    assertEquals("test@example.com", registerData.email)
                }
            )
        }
        assertTrue(result is Resource.Success)
        assertEquals(authResult, (result as Resource.Success).data)
    }

    @Test
    fun `invoke trims input fields before calling repository`() = runTest {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")

        // Use any() to ensure the mock matches regardless of spaces during the setup
        coEvery { authRepository.register(any()) } returns Resource.Success(authResult)

        // When
        registerUseCase(
            "  Hussein  ",
            "  Kamal  ",
            "  hussein_kamal  ",
            "  test@example.com  ",
            "password123",
            "password123"
        )

        // Then
        coVerify {
            authRepository.register(
                withArg { registerData ->
                    // These assertions will now pass because the UseCase trims them
                    assertEquals("Hussein", registerData.firstName)
                    assertEquals("Kamal", registerData.lastName)
                    assertEquals("hussein_kamal", registerData.username)
                    assertEquals("test@example.com", registerData.email)
                }
            )
        }
    }
    // ========== Edge Cases ==========

    @Test
    fun `invoke with special characters in name succeeds`() = runTest {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")
        coEvery { authRepository.register(any()) } returns Resource.Success(authResult)

        // When
        val result = registerUseCase(
            "Mary-Jane",
            "O'Connor",
            "maryjane",
            "mary@example.com",
            "password123",
            "password123"
        )

        // Then
        assertTrue(result is Resource.Success)
        coVerify {
            authRepository.register(
                withArg { registerData ->
                    assertEquals("Mary-Jane", registerData.firstName)
                    assertEquals("O'Connor", registerData.lastName)
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