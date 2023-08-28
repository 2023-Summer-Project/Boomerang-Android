package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.MainUiState
import com.blackbunny.boomerang.data.authentication.User
import com.blackbunny.boomerang.data.authentication.UserRepository
import com.blackbunny.boomerang.domain.authentication.LoginUserToFirebaseImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RegisterScreen {
    LOG_IN, LOGGED_IN
}

@Deprecated("Deprecated. Use SignInViewModel")
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userLoginUseCase: LoginUserToFirebaseImpl,
    private val userRepository: UserRepository
) : ViewModel() {

    // AppScreenStat
    private val _appScreenState = MutableStateFlow(RegisterScreen.LOG_IN)
    val appScreenState = _appScreenState.asStateFlow()

    fun updateAppScreenState(sessionUser: User?) {
        if (sessionUser != null) {
            _appScreenState.update {
                RegisterScreen.LOGGED_IN
            }
        } else {
            _appScreenState.update {
                RegisterScreen.LOG_IN
            }
        }

        Log.d("RegisterViewModel", "Current AppScreenState: ${appScreenState.value}")
    }

    // uiState
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()


    // User Information (Email)
    private val _currentUser = MutableStateFlow<User?>(getCurrentUser())
    val currentUser: StateFlow<User?> = _currentUser

    // Firebase Authentication
    suspend fun loginWithEmail(email: String, password: String) {
        // Received User
        var receivedUser: User? = null
        viewModelScope.launch {
            userLoginUseCase.invoke(email, password)
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .cancellable()
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { loggedInUser ->
                            // Internal Access
                            _currentUser.update { loggedInUser }
                            _uiState.update { currentState ->
                                currentState.copy(currentUser = loggedInUser)
                            }
//                            receivedUser = loggedInUser
                            Log.d("RegisterViewModel", "Received User: ${loggedInUser?.uid}")
                        },
                        onFailure = {
                            // Do nothing
                        }
                    )
                }
        }
    }

    fun getCurrentUser(): User? {
        return userRepository.fetchCurrentUser().also {
            Log.d("RegisterViewModel", "Current User: ${it?.email}")
            updateAppScreenState(it)
        }
    }
}