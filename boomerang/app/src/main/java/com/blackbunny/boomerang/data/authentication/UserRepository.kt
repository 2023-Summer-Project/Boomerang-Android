package com.blackbunny.boomerang.data.authentication

import android.util.Log
import com.blackbunny.boomerang.data.EmailVerification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
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
     * postNewUser
     * Register newly created user with successful email verification to Firebase Firestore collection "User"
     */
    suspend fun postNewUser(nickname: String): Result<Unit> {
        val registrationResult = remoteSource.postNewUser(nickname)
        return if (registrationResult) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Unable to register new user to Firestore database."))
        }
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

    suspend fun fetchSessionUser(): Result<User> {
        remoteSource.fetchSessionUser()
            .also { snapshot ->
                if (snapshot != null) {
                    // User found.
                    val data = snapshot.data
                    if (data != null) {
                        return Result.success(
                            User(
                                email = data["EMAIL"] as String,
                                uid = data["UID"] as String,
                                isEmailVerified = true,
                                userName = data["USERNAME"] as String,
                                profileImage = data["PROFILE_IMAGE"] as String
                            )
                        )
                    } else {
                        return Result.failure(Exception("Unable to find the data under document."))
                    }
                } else {
                    return Result.failure(Exception("No such user under the collection User."))
                }
            }
    }

    fun requestLogout(): Boolean {
        val channel = Channel<Boolean>()
        val result = remoteSource.signOutFromFirebase()

//        channel.send(result)
//
//        Log.d("UserRepository", "Size of channel $channel")

        return result
    }

    fun cancelCurrentJob() {
        remoteSource.cancelCurrentJob()
    }

}

data class User(
    val email: String = "",
    val password: String = "",
    val uid: String = "",
    val isEmailVerified: Boolean = false,
    val userName: String = "",
    val profileImage: String = ""
)