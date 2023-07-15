package com.blackbunny.boomerang.data.authentication

import android.util.Log
import com.blackbunny.boomerang.data.EmailVerification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteSource: RemoteUserDataSource
) {

    fun loginToFirebaseWithEmail(email: String, password: String): Flow<User?> {
        return remoteSource.loginViaEmailAndPasswordTest(email, password)
            .flowOn(Dispatchers.IO)
            .map {
                if (it != null) {
                    Log.d("UserRepository", "Received User: ${it.uid}")
                    User(email = it.email ?: "",  uid = it.uid, isEmailVerified = it.isEmailVerified)
                } else {
                    null
                }
            }
    }

    /**
     * Create new user using Email and Password.
     * @return true, when new user creation is successful, false, when user creation fails
     */
    fun createNewUserWithEmail(email: String, password: String): Flow<Boolean> {
        return remoteSource.createNewUserWithEmail(email, password)
            .flowOn(Dispatchers.IO)
            .map { user ->
                user != null && user.email.equals(email)
            }
    }

    fun requestEmailVerification(): Flow<EmailVerification> {
        // Return result of email verification.
        return remoteSource.requestEmailVerification()
    }

    /**
     * @return true: when given email is already taken, false: when given email is available.
     */
    fun emailExistenceCheck(email: String): Flow<Boolean> {
        return remoteSource.emailExistenceCheck(email)
            .flowOn(Dispatchers.IO)
            .map {
                it.isNotEmpty()
            }
    }

    fun fetchCurrentUser(): User? {
        val currentUser = remoteSource.fetchCurrentUser()
        return if (currentUser != null) {
            User(email = currentUser.email!!, uid = currentUser.uid, isEmailVerified = currentUser.isEmailVerified)
        } else {
            null
        }
    }

}

data class User(
    val email: String = "",
    val password: String = "",
    val uid: String = "",
    val isEmailVerified: Boolean
)