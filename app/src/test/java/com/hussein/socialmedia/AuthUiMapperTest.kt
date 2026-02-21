package com.hussein.socialmedia

import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.presentation.login.mapper.getAvatarColor
import com.hussein.socialmedia.presentation.login.mapper.getInitials
import com.hussein.socialmedia.presentation.login.mapper.isLoginFormComplete
import com.hussein.socialmedia.presentation.login.mapper.isRegisterFormComplete
import com.hussein.socialmedia.presentation.login.mapper.maskEmail
import com.hussein.socialmedia.presentation.login.mapper.toColor
import com.hussein.socialmedia.presentation.login.mapper.toLoginUiModel
import com.hussein.socialmedia.presentation.login.mapper.toProgress
import com.hussein.socialmedia.presentation.login.mapper.toRegisterUiModel
import com.hussein.socialmedia.presentation.login.mapper.toUiModel
import com.hussein.socialmedia.presentation.login.mapper.validateConfirmPassword
import com.hussein.socialmedia.presentation.login.mapper.validateEmail
import com.hussein.socialmedia.presentation.login.mapper.validateLoginForm
import com.hussein.socialmedia.presentation.login.mapper.validateName
import com.hussein.socialmedia.presentation.login.mapper.validatePassword
import com.hussein.socialmedia.presentation.login.mapper.validateRegisterForm
import com.hussein.socialmedia.presentation.login.mapper.validateUsername
import com.hussein.socialmedia.presentation.register.model.PasswordStrength
import junit.framework.TestCase.assertTrue
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Auth UI Mappers
 */
class AuthUiMapperTest {

    // ========== Domain to UI Mapping Tests ==========

    @Test
    fun `toUiModel maps AuthUser correctly`() {
        // Given
        val authUser = AuthUser(
            id = "1",
            username = "john_doe",
            email = "john@example.com",
            firstName = "John",
            lastName = "Doe",
            fullName = "John Doe",
            image = "https://example.com/avatar.jpg",
            gender = "male"
        )

        // When
        val uiModel = authUser.toUiModel()

        // Then
        assertEquals("1", uiModel.id)
        assertEquals("john_doe", uiModel.username)
        assertEquals("john@example.com", uiModel.email)
        assertEquals("John", uiModel.firstName)
        assertEquals("Doe", uiModel.lastName)
        assertEquals("John Doe", uiModel.fullName)
        assertEquals("https://example.com/avatar.jpg", uiModel.avatarUrl)
        assertEquals("Male", uiModel.displayGender)
        assertTrue(uiModel.welcomeMessage.contains("John"))
    }

    @Test
    fun `toUiModel formats gender correctly`() {
        // Test different gender values
        val testCases = mapOf(
            "male" to "Male",
            "female" to "Female",
            "other" to "Other",
            null to "Not specified",
            "" to "Not specified"
        )

        testCases.forEach { (gender, expected) ->
            val authUser = createMockAuthUser(gender = gender)
            val uiModel = authUser.toUiModel()
            assertEquals(expected, uiModel.displayGender)
        }
    }

    @Test
    fun `toLoginUiModel creates correct result`() {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")

        // When
        val loginResult = authResult.toLoginUiModel()

        // Then
        assertEquals(authUser.firstName, loginResult.user.firstName)
        assertTrue(loginResult.isSuccess)
        assertEquals("Welcome back, John!", loginResult.welcomeMessage)
    }

    @Test
    fun `toRegisterUiModel creates correct result`() {
        // Given
        val authUser = createMockAuthUser()
        val authResult = AuthResult(authUser, "token", "refresh")

        // When
        val registerResult = authResult.toRegisterUiModel()

        // Then
        assertEquals(authUser.firstName, registerResult.user.firstName)
        assertTrue(registerResult.isSuccess)
        assertEquals("Account created successfully! Welcome, John!", registerResult.successMessage)
    }

    // ========== Username Validation Tests ==========

    @Test
    fun `validateUsername with valid username returns isValid true`() {
        val result = "john_doe".validateUsername()
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validateUsername with short username returns error`() {
        val result = "ab".validateUsername()
        assertFalse(result.isValid)
        assertEquals("Username must be at least 3 characters", result.errorMessage)
    }

    @Test
    fun `validateUsername with long username returns error`() {
        val result = "a".repeat(21).validateUsername()
        assertFalse(result.isValid)
        assertEquals("Username must be less than 20 characters", result.errorMessage)
    }

    @Test
    fun `validateUsername with invalid characters returns error`() {
        val result = "john@doe".validateUsername()
        assertFalse(result.isValid)
        assertTrue(result.errorMessage!!.contains("letters, numbers"))
    }

    @Test
    fun `validateUsername with empty string returns error`() {
        val result = "".validateUsername()
        assertFalse(result.isValid)
        assertEquals("Username cannot be empty", result.errorMessage)
    }

    // ========== Email Validation Tests ==========

    @Test
    fun `validateEmail with valid email returns isValid true`() {
        val validEmails = listOf(
            "john@example.com",
            "john.doe@example.com",
            "john+tag@example.co.uk"
        )

        validEmails.forEach { email ->
            val result = email.validateEmail()
            assertTrue("$email should be valid", result.isValid)
            assertNull(result.errorMessage)
        }
    }

    @Test
    fun `validateEmail with invalid email returns error`() {
        val invalidEmails = listOf(
            "invalid-email",
            "@example.com",
            "john@",
            "john.example.com"
        )

        invalidEmails.forEach { email ->
            val result = email.validateEmail()
            assertFalse("$email should be invalid", result.isValid)
            assertEquals("Please enter a valid email address", result.errorMessage)
        }
    }

    @Test
    fun `validateEmail with empty string returns error`() {
        val result = "".validateEmail()
        assertFalse(result.isValid)
        assertEquals("Email cannot be empty", result.errorMessage)
    }

    // ========== Password Validation Tests ==========

    @Test
    fun `validatePassword with valid password returns isValid true`() {
        val result = "password123".validatePassword()
        assertTrue(result.isValid)
        assertNull(result.error)
    }

    @Test
    fun `validatePassword with short password returns error`() {
        val result = "pass".validatePassword()
        assertFalse(result.isValid)
        assertEquals("Password must be at least 6 characters", result.error)
    }

    @Test
    fun `validatePassword with empty string returns error`() {
        val result = "".validatePassword()
        assertFalse(result.isValid)
        assertEquals("Password cannot be empty", result.error)
    }

    @Test
    fun `validatePassword calculates strength correctly`() {
        // Weak password
        val weak = "password".validatePassword()
        assertEquals(PasswordStrength.WEAK, weak.strengthLevel)

        // Medium password
        val medium = "Password123".validatePassword()
        assertEquals(PasswordStrength.MEDIUM, medium.strengthLevel)

        // Strong password
        val strong = "Password123!@#".validatePassword()
        assertEquals(PasswordStrength.STRONG, strong.strengthLevel)
    }

    @Test
    fun `validatePassword provides strength message`() {
        val weak = "password".validatePassword()
        assertTrue(weak.strengthMessage.contains("Weak"))

        val strong = "Password123!".validatePassword()
        assertTrue(strong.strengthMessage.contains("Strong") || strong.strengthMessage.contains("Excellent"))
    }

    // ========== Confirm Password Validation Tests ==========

    @Test
    fun `validateConfirmPassword with matching passwords returns isValid true`() {
        val result = "password123".validateConfirmPassword("password123")
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validateConfirmPassword with non-matching passwords returns error`() {
        val result = "password123".validateConfirmPassword("different")
        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `validateConfirmPassword with empty string returns error`() {
        val result = "".validateConfirmPassword("password123")
        assertFalse(result.isValid)
        assertEquals("Please confirm your password", result.errorMessage)
    }

    // ========== Name Validation Tests ==========

    @Test
    fun `validateName with valid name returns isValid true`() {
        val result = "John".validateName("First name")
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validateName with short name returns error`() {
        val result = "J".validateName("First name")
        assertFalse(result.isValid)
        assertEquals("First name must be at least 2 characters", result.errorMessage)
    }

    @Test
    fun `validateName with long name returns error`() {
        val result = "a".repeat(51).validateName("First name")
        assertFalse(result.isValid)
        assertEquals("First name must be less than 50 characters", result.errorMessage)
    }

    @Test
    fun `validateName with numbers returns error`() {
        val result = "John123".validateName("First name")
        assertFalse(result.isValid)
        assertTrue(result.errorMessage!!.contains("letters"))
    }

    @Test
    fun `validateName allows hyphens and apostrophes`() {
        val validNames = listOf("Mary-Jane", "O'Connor", "Jean-Paul")
        validNames.forEach { name ->
            val result = name.validateName("Name")
            assertTrue("$name should be valid", result.isValid)
        }
    }

    // ========== Form Validation Tests ==========

    @Test
    fun `isLoginFormComplete returns true when both fields filled`() {
        assertTrue(isLoginFormComplete("john_doe", "password123"))
    }

    @Test
    fun `isLoginFormComplete returns false when username empty`() {
        assertFalse(isLoginFormComplete("", "password123"))
    }

    @Test
    fun `isLoginFormComplete returns false when password empty`() {
        assertFalse(isLoginFormComplete("john_doe", ""))
    }

    @Test
    fun `isRegisterFormComplete returns true when all fields filled`() {
        assertTrue(
            isRegisterFormComplete(
                "John", "Doe", "john_doe",
                "john@example.com", "password123", "password123"
            )
        )
    }

    @Test
    fun `isRegisterFormComplete returns false when any field empty`() {
        assertFalse(
            isRegisterFormComplete(
                "", "Doe", "john_doe",
                "john@example.com", "password123", "password123"
            )
        )
    }

    // ========== Password Strength Tests ==========

    @Test
    fun `toColor returns correct colors for strength levels`() {
        assertTrue(PasswordStrength.NONE.toColor().value > 0u)
        assertTrue(PasswordStrength.WEAK.toColor().value > 0u)
        assertTrue(PasswordStrength.MEDIUM.toColor().value > 0u)
        assertTrue(PasswordStrength.STRONG.toColor().value > 0u)
    }

    @Test
    fun `toProgress returns correct values`() {
        assertEquals(0f, PasswordStrength.NONE.toProgress(), 0.01f)
        assertEquals(0.33f, PasswordStrength.WEAK.toProgress(), 0.01f)
        assertEquals(0.66f, PasswordStrength.MEDIUM.toProgress(), 0.01f)
        assertEquals(1f, PasswordStrength.STRONG.toProgress(), 0.01f)
    }

    // ========== Utility Function Tests ==========

    @Test
    fun `maskEmail masks email correctly`() {
        val masked = "john.doe@example.com".maskEmail()
        assertTrue(masked.contains("@example.com"))
        assertTrue(masked.contains("*"))
        assertFalse(masked.contains("john.doe"))
    }

    @Test
    fun `getInitials returns correct initials`() {
        val authUser = createMockAuthUser()
        val uiModel = authUser.toUiModel()
        assertEquals("JD", uiModel.getInitials())
    }

    @Test
    fun `getAvatarColor returns consistent color`() {
        val authUser1 = createMockAuthUser()
        val authUser2 = createMockAuthUser()

        val color1 = authUser1.toUiModel().getAvatarColor()
        val color2 = authUser2.toUiModel().getAvatarColor()

        // Same ID should return same color
        assertEquals(color1, color2)
    }

    @Test
    fun `validateLoginForm returns all errors`() {
        val errors = validateLoginForm("ab", "pass")

        assertTrue(errors.containsKey("username"))
        assertTrue(errors.containsKey("password"))
        assertNotNull(errors["username"])
        assertNotNull(errors["password"])
    }

    @Test
    fun `validateRegisterForm returns all errors`() {
        val errors = validateRegisterForm(
            "J", "D", "ab",
            "invalid", "pass", "different"
        )

        assertTrue(errors.size >= 4) // At least 4 errors
        assertNotNull(errors["firstName"])
        assertNotNull(errors["username"])
        assertNotNull(errors["email"])
        assertNotNull(errors["password"])
    }

    // ========== Helper Functions ==========

    private fun createMockAuthUser(gender: String? = "male") = AuthUser(
        id = "1",
        username = "john_doe",
        email = "john@example.com",
        firstName = "John",
        lastName = "Doe",
        fullName = "John Doe",
        image = null,
        gender = gender
    )
}