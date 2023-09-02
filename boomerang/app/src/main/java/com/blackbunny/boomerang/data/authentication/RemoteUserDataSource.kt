package com.blackbunny.boomerang.data.authentication

import android.util.Log
import com.blackbunny.boomerang.data.EmailVerification
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RemoteUserDataSource @Inject constructor() {
    private val TAG = "RemoteUserDataSource"
    private val auth = Firebase.auth

    // Common CoroutineScope
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO)

    fun loginViaEmailAndPasswordTest(email: String, password: String): Flow<FirebaseUser?> = callbackFlow {
        val signInListener = OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful) {
                // Successful Login
                trySend(auth.currentUser)
            } else {
                // Login Fails
                Log.d(TAG, task.exception!!.message!!)
                trySend(null)
            }
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(signInListener)

        awaitClose { /* Task Already Completed. */ }

    }

    fun createNewUserWithEmail(email: String, password: String): Flow<FirebaseUser?> = callbackFlow {
        val createUserListener = OnCompleteListener<AuthResult> {task ->
            if (task.isSuccessful) {
                trySend(auth.currentUser)
            } else {
                Log.d(TAG, "Not Successful\n Reason: ${task.exception}")
            }
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createUserListener)

        awaitClose { /* Task Already Completed. */ }
    }

    fun emailExistenceCheck(email: String): Flow<List<String>> = callbackFlow {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "Successful")
                if (it.result.signInMethods != null) {
                    for (str in it.result.signInMethods!!) {
                        Log.d(TAG, "Email Found: $str")
                    }
                } else {
                    Log.d(TAG, "NULL")
                }
                trySend(it.result.signInMethods ?: emptyList())
            } else {
                Log.d(TAG, "Not Successful\n Reason: ${it.exception}")
//                trySend(emptyList())
            }
        }

        awaitClose { /* Task Already Completed. */ }
    }

    // Need to be sequential to other operations. UI and business logic should not be updated until
    // users verify their email.
    fun requestEmailVerification(): Flow<EmailVerification> = callbackFlow {
        val cookiedUser = Firebase.auth.currentUser

        // request email verification for cookied user in current Firebase auth object.
        if (cookiedUser == null) {
            trySend(EmailVerification.NOT_VERIFIED)
        } else {
            // In this case, the currentUser would never be null, therefore, it can be asserted.
            cookiedUser.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ioCoroutineScope.launch {
                            // 3 minute timeouts.
                            try {
                                // 180000
                                withTimeout(180000L) {
                                    while (true) {
                                        cookiedUser.reload()
                                        Log.d(TAG, "${cookiedUser.isEmailVerified}")
                                        if (Firebase.auth.currentUser!!.isEmailVerified) {
                                            trySend(EmailVerification.VERIFIED)
                                            break
                                        } else {
                                            trySend(EmailVerification.VERIFYING)
                                            delay(2000L)
                                            continue
                                        }
                                    }
                                }
                            } catch (e: TimeoutCancellationException) {
                                Log.d(TAG, "Email verification request timeout.")
                                removeUserFromDatabase()
                                trySend(EmailVerification.NOT_VERIFIED)
                            } catch (e: CancellationException) {
                                Log.d(TAG, "Current job was cancelled by user successfully.")
                                removeUserFromDatabase()
                            }
                        }
                    } else {
                        Log.d(TAG, "Failed to send email verification\n${task.exception?.message}")
                        removeUserFromDatabase()
                        trySend(EmailVerification.NOT_VERIFIED)
                    }
                }
        }

        awaitClose {  }
    }

    suspend fun postNewUser(nickname: String) = suspendCoroutine { continuation ->
        if (Firebase.auth.currentUser != null) {
            val temp = hashMapOf(
                "AREA" to emptyList<String>(),
                "EMAIL" to Firebase.auth.currentUser!!.email,
                "PROFILE_IMAGE" to "https://firebasestorage.googleapis.com/v0/b/ios-demo-ae41b.appspot.com/o/2023_summer_project.png?alt=media&token=692ddad2-7b3f-4b5d-b5aa-507a9d994d97",
                "UID" to Firebase.auth.currentUser!!.uid,
                "USERNAME" to nickname
            )
            // Register newly created user to Firebase Firestore collection called "User"
            Firebase.firestore.collection("User").document(Firebase.auth.currentUser!!.uid)
                .set(temp)
                .addOnSuccessListener {
                    Log.i(TAG, "Successfully register new user $temp")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w(TAG, "Unable to register new user.\n\tReason: ${it.printStackTrace()}")

                    // Error exists on registration process. Clear out the registration infomration.
                    removeUserFromDatabase()

                    continuation.resume(false)
                }
        } else {
            continuation.resume(false)
        }

    }


    fun fetchCurrentUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }

    suspend fun fetchSessionUser() = suspendCoroutine { continuation ->
        val currentUser = Firebase.auth.currentUser

        if (currentUser != null) {
            // Request user information to Firebase Firestore database (User Table)
            Firebase.firestore.collection("User").document(currentUser.uid)
                .get(Source.DEFAULT)
                .addOnSuccessListener { documentSnapshot ->
                    Log.d(TAG, "Successfully receive user information from the database.")
                    continuation.resume(documentSnapshot)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Unable to receive user information from the database.")
                    it.printStackTrace()

                    continuation.resume(null)
                }
        } else {
            continuation.resume(null)
        }
    }

    fun signOutFromFirebase(): Boolean {
        // Sign out current user and clear out a related disk cache.
        auth.signOut().run {
            return auth.currentUser == null
        }
    }

    // User modification - Delete User from the database.
    fun removeUserFromDatabase() {
        Firebase.auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "User has been deleted successfully due to unverified email.")
            } else {
                Log.d(TAG, "Failed to remove user from the database.\nReason: ${task.exception?.message}")
            }
        }
    }

    fun cancelCurrentJob() {
        ioCoroutineScope.coroutineContext.cancelChildren(
            CancellationException("User cancel the current ongoing jobs.")
        )
    }

    /*
    suspend fun loginViaEmailAndPassword(email: String, password: String): FirebaseUser? {
        var tempUser: FirebaseUser? = null

        val loginReq = CoroutineScope(Dispatchers.IO).async {
            val auth = Firebase.auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Successful Login
                        tempUser = auth.currentUser
                    } else {
                        // Login Fails

                    }
                }
        }

        loginReq.await()

        return tempUser
    }
     */

}