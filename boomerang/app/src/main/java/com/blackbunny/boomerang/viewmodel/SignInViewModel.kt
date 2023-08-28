package com.blackbunny.boomerang.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.EmailValidation
import com.blackbunny.boomerang.data.SignInUiState
import com.blackbunny.boomerang.data.authentication.User
import com.blackbunny.boomerang.domain.authentication.LoginUserToFirebaseImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SignInViewModel
 * ViewModel for Sign in page.
 */

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userLoginUseCase: LoginUserToFirebaseImpl
) : ViewModel() {

    // User Information (Email)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

    fun updateEmailInput(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                inputEmail = value
            )
        }
        validateEmailAddress(value.text)
    }

    fun updatePasswordInput(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                inputPassword = value
            )
        }
    }

    // Firebase Authentication
    fun loginWithEmail(email: String, password: String) {
        // Received User
        var receivedUser: User? = null
        _uiState.update {
            it.copy(
                dialogVisibility = true
            )
        }
        viewModelScope.launch {
            userLoginUseCase.invoke(email, password)
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { loggedInUser ->
                            // Internal Access
                            _uiState.update {
                                it.copy(
                                    currentUser = loggedInUser,
                                    dialogVisibility = false
                                )
                            }
                            Log.d("SignInViewModel", "Received User: ${loggedInUser?.uid}")
                        },
                        onFailure = {
                            _uiState.update {
                                it.copy(
                                    dialogVisibility = false
                                )
                            }
                        }
                    )
                }
        }
    }

    fun validateEmailAddress(email: String) {
        val emailRegex = Regex(Patterns.EMAIL_ADDRESS.pattern())
        viewModelScope.launch {
            _uiState.update {
                if (emailRegex.containsMatchIn(email)) {
                    it.copy(
                        isProvidedEmailValid = EmailValidation.VALID
                    )
                } else {
                    it.copy(
                        isProvidedEmailValid = EmailValidation.INVALID_FORMAT
                    )
                }
            }

        }
    }
}