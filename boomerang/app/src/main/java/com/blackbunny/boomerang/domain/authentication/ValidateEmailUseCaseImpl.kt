package com.blackbunny.boomerang.domain.authentication

import com.blackbunny.boomerang.data.authentication.UserRepository
import javax.inject.Inject


class ValidateEmailUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : ValidateEmailUseCase {

    override fun emailValidation(email: String, onSuccess: (Result<Boolean>) -> Unit) {

    }

    operator fun invoke(email: String, onSuccess: (Result<Boolean>) -> Unit, onFailure: () -> Unit) {
        emailValidation(email, onSuccess)
    }

}