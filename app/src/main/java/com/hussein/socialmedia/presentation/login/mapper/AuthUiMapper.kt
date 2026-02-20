package com.hussein.socialmedia.presentation.login.mapper
import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.presentation.login.model.AuthUserUiModel
import com.hussein.socialmedia.presentation.login.model.LoginResultUiModel
import com.hussein.socialmedia.presentation.login.model.ValidationResultUiModel
import com.hussein.socialmedia.presentation.register.model.PasswordFieldUiModel
import com.hussein.socialmedia.presentation.register.model.PasswordStrength
import com.hussein.socialmedia.presentation.register.model.RegisterResultUiModel
import java.util.Locale

/**
 * Maps AuthUser domain model to AuthUserUiModel
 */
fun AuthUser.toUiModel(): AuthUserUiModel {
    return AuthUserUiModel(
        id = id,
        username = username,
        email = email,
        fullName = fullName,
        firstName = firstName,
        lastName = lastName,
        avatarUrl = image,
        displayGender = formatGender(gender),
        welcomeMessage = createWelcomeMessage(firstName)
    )
}

/**
 * Maps AuthResult to LoginResultUiModel
 */
fun AuthResult.toLoginUiModel(): LoginResultUiModel {
    return LoginResultUiModel(
        user = user.toUiModel(),
        isSuccess = true,
        welcomeMessage = "Welcome back, ${user.firstName}!"
    )
}

/**
 * Maps AuthResult to RegisterResultUiModel
 */
fun AuthResult.toRegisterUiModel(): RegisterResultUiModel {
    return RegisterResultUiModel(
        user = user.toUiModel(),
        isSuccess = true,
        successMessage = "Account created successfully! Welcome, ${user.firstName}!"
    )
}

/**
 * Validates username and returns UI model
 */
fun String.validateUsername(): ValidationResultUiModel {
    return when {
        this.isBlank() -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Username cannot be empty"
        )
        this.length < 3 -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Username must be at least 3 characters"
        )
        this.length > 20 -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Username must be less than 20 characters"
        )
        !this.matches(Regex("^[a-zA-Z0-9._]+$")) -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Username can only contain letters, numbers, dots and underscores"
        )
        else -> ValidationResultUiModel(isValid = true)
    }
}

/**
 * Validates email and returns UI model
 */
fun String.validateEmail(): ValidationResultUiModel {
    return when {
        this.isBlank() -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Email cannot be empty"
        )
        !android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches() -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Please enter a valid email address"
        )
        else -> ValidationResultUiModel(isValid = true)
    }
}

/**
 * Validates password and returns UI model with strength
 */
fun String.validatePassword(): PasswordFieldUiModel {
    val strength = calculatePasswordStrength(this)
    val strengthMessage = getPasswordStrengthMessage(strength)

    val validation = when {
        this.isBlank() -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Password cannot be empty"
        )
        this.length < 6 -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Password must be at least 6 characters"
        )
        this.length < 8 && strength == PasswordStrength.WEAK -> ValidationResultUiModel(
            isValid = true,
            errorMessage = "Consider using a stronger password"
        )
        else -> ValidationResultUiModel(isValid = true)
    }

    return PasswordFieldUiModel(
        value = this,
        error = validation.errorMessage,
        isValid = validation.isValid,
        strengthLevel = strength,
        strengthMessage = strengthMessage
    )
}

/**
 * Validates confirm password matches original password
 */
fun String.validateConfirmPassword(originalPassword: String): ValidationResultUiModel {
    return when {
        this.isBlank() -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Please confirm your password"
        )
        this != originalPassword -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "Passwords do not match"
        )
        else -> ValidationResultUiModel(isValid = true)
    }
}

/**
 * Validates name field (first name or last name)
 */
fun String.validateName(fieldName: String = "Name"): ValidationResultUiModel {
    return when {
        this.isBlank() -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "$fieldName cannot be empty"
        )
        this.length < 2 -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "$fieldName must be at least 2 characters"
        )
        this.length > 50 -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "$fieldName must be less than 50 characters"
        )
        !this.matches(Regex("^[a-zA-Z\\s'-]+$")) -> ValidationResultUiModel(
            isValid = false,
            errorMessage = "$fieldName can only contain letters, spaces, hyphens and apostrophes"
        )
        else -> ValidationResultUiModel(isValid = true)
    }
}

/**
 * Calculates password strength based on various criteria
 */
private fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.NONE

    var score = 0

    // Length check
    when {
        password.length >= 12 -> score += 2
        password.length >= 8 -> score += 1
    }

    // Uppercase check
    if (password.any { it.isUpperCase() }) score++

    // Lowercase check
    if (password.any { it.isLowerCase() }) score++

    // Digit check
    if (password.any { it.isDigit() }) score++

    // Special character check
    if (password.any { !it.isLetterOrDigit() }) score++

    return when (score) {
        0, 1, 2 -> PasswordStrength.WEAK
        3, 4 -> PasswordStrength.MEDIUM
        else -> PasswordStrength.STRONG
    }
}

/**
 * Gets user-friendly message for password strength
 */
private fun getPasswordStrengthMessage(strength: PasswordStrength): String {
    return when (strength) {
        PasswordStrength.NONE -> ""
        PasswordStrength.WEAK -> "Weak - Add uppercase, numbers, and special characters"
        PasswordStrength.MEDIUM -> "Medium - Good, but could be stronger"
        PasswordStrength.STRONG -> "Strong - Excellent password!"
    }
}

/**
 * Formats gender for display
 */
private fun formatGender(gender: String?): String {
    return when (gender?.lowercase(Locale.getDefault())) {
        "male" -> "Male"
        "female" -> "Female"
        "other" -> "Other"
        else -> "Not specified"
    }
}

/**
 * Creates personalized welcome message
 */
private fun createWelcomeMessage(firstName: String): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
    return "$greeting, $firstName!"
}

/**
 * Extension function to check if form is complete
 */
fun isLoginFormComplete(username: String, password: String): Boolean {
    return username.isNotBlank() && password.isNotBlank()
}

/**
 * Extension function to check if register form is complete
 */
fun isRegisterFormComplete(
    firstName: String,
    lastName: String,
    username: String,
    email: String,
    password: String,
    confirmPassword: String
): Boolean {
    return firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            username.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank()
}

/**
 * Extension function to validate entire login form
 */
fun validateLoginForm(username: String, password: String): Map<String, String?> {
    val errors = mutableMapOf<String, String?>()

    val usernameValidation = username.validateUsername()
    if (!usernameValidation.isValid) {
        errors["username"] = usernameValidation.errorMessage
    }

    val passwordValidation = password.validatePassword()
    if (!passwordValidation.isValid) {
        errors["password"] = passwordValidation.error
    }

    return errors
}

/**
 * Extension function to validate entire register form
 */
fun validateRegisterForm(
    firstName: String,
    lastName: String,
    username: String,
    email: String,
    password: String,
    confirmPassword: String
): Map<String, String?> {
    val errors = mutableMapOf<String, String?>()

    val firstNameValidation = firstName.validateName("First name")
    if (!firstNameValidation.isValid) {
        errors["firstName"] = firstNameValidation.errorMessage
    }

    val lastNameValidation = lastName.validateName("Last name")
    if (!lastNameValidation.isValid) {
        errors["lastName"] = lastNameValidation.errorMessage
    }

    val usernameValidation = username.validateUsername()
    if (!usernameValidation.isValid) {
        errors["username"] = usernameValidation.errorMessage
    }

    val emailValidation = email.validateEmail()
    if (!emailValidation.isValid) {
        errors["email"] = emailValidation.errorMessage
    }

    val passwordValidation = password.validatePassword()
    if (!passwordValidation.isValid) {
        errors["password"] = passwordValidation.error
    }

    val confirmPasswordValidation = confirmPassword.validateConfirmPassword(password)
    if (!confirmPasswordValidation.isValid) {
        errors["confirmPassword"] = confirmPasswordValidation.errorMessage
    }

    return errors
}

/**
 * Gets color for password strength indicator
 */
fun PasswordStrength.toColor(): androidx.compose.ui.graphics.Color {
    return when (this) {
        PasswordStrength.NONE -> androidx.compose.ui.graphics.Color.Gray
        PasswordStrength.WEAK -> androidx.compose.ui.graphics.Color.Red
        PasswordStrength.MEDIUM -> androidx.compose.ui.graphics.Color(0xFFFFA500) // Orange
        PasswordStrength.STRONG -> androidx.compose.ui.graphics.Color.Green
    }
}

/**
 * Gets progress value (0.0 to 1.0) for password strength
 */
fun PasswordStrength.toProgress(): Float {
    return when (this) {
        PasswordStrength.NONE -> 0f
        PasswordStrength.WEAK -> 0.33f
        PasswordStrength.MEDIUM -> 0.66f
        PasswordStrength.STRONG -> 1f
    }
}

/**
 * Formats error message for display
 */
fun String?.toDisplayError(): String {
    return this ?: ""
}

/**
 * Masks email for privacy display
 */
fun String.maskEmail(): String {
    val parts = this.split("@")
    if (parts.size != 2) return this

    val username = parts[0]
    val domain = parts[1]

    val maskedUsername = when {
        username.length <= 2 -> username
        username.length <= 4 -> "${username[0]}**${username.last()}"
        else -> "${username.take(2)}***${username.takeLast(2)}"
    }

    return "$maskedUsername@$domain"
}

/**
 * Formats user initials for avatar placeholder
 */
fun AuthUserUiModel.getInitials(): String {
    val firstInitial = firstName.firstOrNull()?.uppercaseChar() ?: ""
    val lastInitial = lastName.firstOrNull()?.uppercaseChar() ?: ""
    return "$firstInitial$lastInitial"
}

/**
 * Gets avatar color based on user ID (for consistent colors)
 */
fun AuthUserUiModel.getAvatarColor(): androidx.compose.ui.graphics.Color {
    val colors = listOf(
        androidx.compose.ui.graphics.Color(0xFF6750A4),
        androidx.compose.ui.graphics.Color(0xFF00BCD4),
        androidx.compose.ui.graphics.Color(0xFF4CAF50),
        androidx.compose.ui.graphics.Color(0xFFFF9800),
        androidx.compose.ui.graphics.Color(0xFFE91E63),
        androidx.compose.ui.graphics.Color(0xFF9C27B0)
    )
    val index = id.hashCode() % colors.size
    return colors[index.coerceIn(0, colors.size - 1)]
}