package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // Firebase Authentication
    fun loginWithEmail(email: String, password: String) {
        // Received User
        var receivedUser: User? = null

        viewModelScope.launch {
            userLoginUseCase.invoke(email, password)
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { loggedInUser ->
                            // Internal Access
                            _currentUser.value = loggedInUser
                            Log.d("SignInViewModel", "Received User: ${loggedInUser?.uid}")
                        },
                        onFailure = {
                            // Do nothing
                        }
                    )
                }
        }
    }
}