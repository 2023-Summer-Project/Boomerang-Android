package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.ProfileServiceUiState
import com.blackbunny.boomerang.data.SignOutRequest
import com.blackbunny.boomerang.data.authentication.User
import com.blackbunny.boomerang.data.authentication.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Profile Service ViewModel
 */

@HiltViewModel
class ProfileServiceViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileServiceUiState())
    val uiState : StateFlow<ProfileServiceUiState> = _uiState.asStateFlow()

    fun updateOssVisibility(value: Boolean) {
        _uiState.update {
            it.copy(
                isOssNoticeVisible = value
            )
        }
    }


    // Log out.
    suspend fun requestLogOut() {
        // Pops up Dialog
        _uiState.update {
            it.copy(
                dialogVisibility = true,
                signOutRequestStatus = SignOutRequest.REQUESTING
            )
        }

//        val receiver = userRepository.requestLogout()
//        Log.d("ProfileServiceVM", "Receiver: $receiver")

        val result = userRepository.requestLogout()

        if (result) {
            Log.d("ProfileServiceVM", "Update status to SUCCESS")
            _uiState.update {
                it.copy(
                    signOutRequestStatus = SignOutRequest.SUCCESS
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    signOutRequestStatus = SignOutRequest.FAILED
                )
            }
        }

    }

    fun closeDialog() {
        _uiState.update {
            it.copy(
                dialogVisibility = false
            )
        }
    }


    fun cancelCurrentJob() {
        _uiState.update {
            it.copy(
                dialogVisibility = false,
                signOutRequestStatus = SignOutRequest.YET_REQUESTED
            )
        }

        viewModelScope.coroutineContext.cancelChildren()
        userRepository.cancelCurrentJob()
    }


     fun fetchCurrentUser() {
        userRepository.fetchCurrentUser().also { user ->
            _uiState.update {
                if (user != null) {
                    it.copy(sessionUser = user)
                } else {
                    // Might cause the problem if user somehow enter this screen, with null session User,
                    // and try to log out.
                    it.copy(sessionUser = User())
                }
            }
        }
    }

    fun fetchSessionUser() {
        viewModelScope.launch {
            userRepository.fetchSessionUser()
                .fold(
                    onSuccess = { user ->
                       Log.d("ProfileServiceViewModel", "User found.")
                        _uiState.update {
                            it.copy(
                                sessionUser = user
                            )
                        }
                    },
                    onFailure = {
                        Log.d("ProfileServiceViewModel", "Unable to find user.")
                        it.printStackTrace()
                    }
                )
        }

    }

}