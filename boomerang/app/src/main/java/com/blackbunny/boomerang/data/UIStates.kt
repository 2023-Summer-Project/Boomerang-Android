package com.blackbunny.boomerang.data

import androidx.compose.ui.text.input.TextFieldValue
import com.blackbunny.boomerang.data.authentication.User

data class MainUiState(
    val currentUser: User? = null
)

data class SignUpUiState(
    // User Input Credentials
    val userInputEmail: TextFieldValue = TextFieldValue(),
    val userInputPassword: TextFieldValue = TextFieldValue(),
    // For password confirmation
    val userInputPasswordConfirm: TextFieldValue = TextFieldValue(),
    // Email Validation State
    val emailValidationState: EmailValidation = EmailValidation.INITIAL,
    // Password Validation State
    val passwordValidationVisible: Boolean = false,
    val passwordConfirmState: PasswordValidation = PasswordValidation.INITIAL,
    val isEmailVerified: EmailVerification = EmailVerification.NOT_VERIFIED,
    // SignUp Status
    val signUpStatus: Boolean = false,
    val dialogVisibility: Boolean = false
)
