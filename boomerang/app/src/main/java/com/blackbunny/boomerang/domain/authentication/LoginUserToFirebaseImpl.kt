package com.blackbunny.boomerang.domain.authentication

import com.blackbunny.boomerang.data.authentication.User
import com.blackbunny.boomerang.data.authentication.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUserToFirebaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : LoginUserToFirebaseUseCase {

    override fun loginWithEmailAndPassword(email: String, password: String): Flow<User?> {
        return userRepository.loginToFirebaseWithEmail(email, password)
    }

    operator fun invoke(email: String, password: String): Flow<User?> {
        return loginWithEmailAndPassword(email, password)
    }

}