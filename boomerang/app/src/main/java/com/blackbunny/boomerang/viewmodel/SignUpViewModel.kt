package com.blackbunny.boomerang.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.EmailValidation
import com.blackbunny.boomerang.data.EmailVerification
import com.blackbunny.boomerang.data.PasswordValidation
import com.blackbunny.boomerang.data.SignUpUiState
import com.blackbunny.boomerang.data.authentication.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val TAG = "SignUpViewModel"

    // UIStates
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    // updates
    fun setEmailInput(input: TextFieldValue) {
        _uiState.update {
            it.copy(userInputEmail = input)
        }
        emailValidation()
    }

    fun setPasswordInput(input: TextFieldValue) {
        _uiState.update {
            it.copy(
                userInputPassword = input,
                passwordValidationVisible = input.text.isNotEmpty()
            )
        }
    }

    fun setPasswordConfirmationInput(input: TextFieldValue) {
        _uiState.update {
            it.copy(userInputPasswordConfirm = input)
        }
        passwordValidation(viewModelScope) {
            if (_uiState.value.userInputPassword.text == _uiState.value.userInputPasswordConfirm.text) {
                setPasswordValidationState(PasswordValidation.VALID)
            } else {
                setPasswordValidationState(PasswordValidation.NOT_VALID)
            }
        }
    }

    fun setEmailValidationState(state: EmailValidation) {
        _uiState.update {
            it.copy(emailValidationState = state)
        }
    }

    fun setPasswordValidationState(state: PasswordValidation) {
        _uiState.update {
            it.copy(passwordConfirmState = state)
        }
    }

    fun createNewUser(email: String, password: String): Channel<Boolean> {
        _uiState.update {
            it.copy(
                dialogVisibility = true
            )
        }

        val channel = Channel<Boolean>()
        viewModelScope.launch {
            userRepository.createNewUserWithEmail(email, password)
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            _uiState.update { state ->
                                state.copy(
                                    signUpStatus = it
                                )
                            }
                            channel.trySend(it)
                        },
                        onFailure = {
                            Log.d(TAG, "Create user failed with exception: ${it.message}")
                            _uiState.update {
                                it.copy(
                                    dialogVisibility = false
                                )
                            }
                        }
                    )
                }
        }
        return channel
    }

    fun requestEmailVerification(onSuccess: (EmailVerification) -> Unit) {
        _uiState.update {
            it.copy(
                isEmailVerified = EmailVerification.VERIFYING
            )
        }

        viewModelScope.launch {
            userRepository.requestEmailVerification()
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collect {  result ->
                    result.fold(
                        onSuccess = { isVerified ->
                            _uiState.update {
                                if (isVerified == EmailVerification.NOT_VERIFIED) {
                                    it.copy(
                                        isEmailVerified = isVerified,
                                        dialogVisibility = false
                                    )
                                } else {
                                    it.copy(
                                        isEmailVerified = isVerified
                                    )
                                }
                            }.also {
                                onSuccess(isVerified).also {
                                    Log.d(TAG, "Verification: $isVerified")
                                }
                            }
                        },
                        onFailure = {

                        }
                    )
                }
        }
    }


    // Email Validation
    private fun emailValidation(coroutineScope: CoroutineScope, operation: () -> Unit) {
        coroutineScope.launch(Dispatchers.Default) {
            operation()
        }
    }

    private fun emailValidation() {
        viewModelScope.launch {
            val emailRegex = Regex(Patterns.EMAIL_ADDRESS.pattern())
            if (emailRegex.containsMatchIn(_uiState.value.userInputEmail.text)) {
                userRepository.emailExistenceCheck(_uiState.value.userInputEmail.text)
                    .flowOn(Dispatchers.IO)
                    .map { Result.success(it) }
                    .catch { emit(Result.failure(it)) }
                    .collectLatest { result ->
                        result.fold(
                            onSuccess = {
                                Log.d(TAG, "Provided Email Existed? $result")
                                if (!it) {
                                    setEmailValidationState(EmailValidation.VALID)
                                } else {
                                    setEmailValidationState(EmailValidation.EXISTS)
                                }
                            },
                            onFailure = {

                            }
                        )
                    }
            } else {
                setEmailValidationState(EmailValidation.INVALID_FORMAT)
            }
        }
    }

    // password validation
    private fun passwordValidation(coroutineScope: CoroutineScope, operation: () -> Unit) {
        coroutineScope.launch(Dispatchers.Default) {
            operation()
        }
    }
}