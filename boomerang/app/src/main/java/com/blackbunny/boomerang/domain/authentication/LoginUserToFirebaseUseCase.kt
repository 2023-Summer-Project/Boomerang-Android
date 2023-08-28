package com.blackbunny.boomerang.domain.authentication

import com.blackbunny.boomerang.data.authentication.User
import kotlinx.coroutines.flow.Flow

interface LoginUserToFirebaseUseCase {

    // Login with Email and Password
    fun loginWithEmailAndPassword(email: String, password: String): Flow<User?>

    // Login via Google

    // Login via Facebook

    // Login via Twitter

}

interface ValidateEmailUseCase {
    fun emailValidation(email: String, onSuccess: (Result<Boolean>) -> Unit)
}