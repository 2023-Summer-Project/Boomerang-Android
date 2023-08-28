package com.blackbunny.boomerang.data.transaction

import android.util.Log
import com.blackbunny.boomerang.data.TransactionStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author kimdoyoon
 * Created 8/28/23 at 11:47 AM
 */
class RemoteTransactionDataSource @Inject constructor() {
    private val TAG = "RemoteTransactionDataSource"

    // Firebase Firestore related variables
    private val ref = Firebase.firestore

    suspend fun postRentRequest(transaction: Transaction) = suspendCoroutine { continuation ->
        ref.collection("Transaction")
            .document(UUID.randomUUID().toString())
            .set(transaction.toMap())
            .addOnSuccessListener {
                // Successfully upload new transaction
                continuation.resume(true)
            }
            .addOnFailureListener {
                continuation.resume(false)
                it.printStackTrace()
            }
    }

    fun fetchTransactions(userId: String, role: String) = callbackFlow {
        ref.collection("Transaction")
            .whereEqualTo(role, userId)
            .whereNotEqualTo("STATUS", TransactionStatus.COMPLETED.name)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    Log.d(TAG, "Current data: ${value.documents}")
                    for (doc in value.documentChanges) {
                        trySend(doc)
                    }

                }
            }

        awaitClose { /* Do nothing currently */ }
    }

    fun fetchCompletedTransaction(userId: String) = callbackFlow {
        ref.collection("Transaction")
            .whereEqualTo("RENTER", userId)
//            .whereEqualTo("RENTEE", userId)
            .whereEqualTo("STATUS", TransactionStatus.COMPLETED.name)
            .get(Source.DEFAULT)
            .addOnSuccessListener {
                for(document in it.documents) {
                   trySend(document)
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Unable to fetch document")
                it.printStackTrace()
            }
        awaitClose {  }
    }



    fun updateTransaction(transactionId: String, newStatus: TransactionStatus) {
        ref.collection("Transaction")
            .document(transactionId)
            .update("STATUS", newStatus.name)
            .addOnSuccessListener {
                // Successful
                Log.d(TAG, "Update Document $transactionId successful")
            }
            .addOnFailureListener {
                Log.d(TAG, "Unable to update document $transactionId")
                it.printStackTrace()
            }
    }

}

// Transaction
data class Transaction(
    val transactionId: String = "",
    val productId: String = "",
    val productImage: String = "",
    val productName: String = "",
    val renter: String = "",
    val rentee: String = "",
    val price: Int = 0,
    val deposit: Int = 0,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val location: String = "",
    val status: TransactionStatus = TransactionStatus.YET_INITIALIZED
) {
    // To Map
    fun toMap() = hashMapOf(
        "PRODUCT_ID" to productId,
        "PRODUCT_IMAGE" to productImage,
        "PRODUCT_NAME" to productName,
        "RENTER" to renter,
        "RENTEE" to rentee,
        "PRICE" to price,
        "DEPOSIT" to deposit,
        "START_DATE" to Timestamp(Date(startDate)),
        "END_DATE" to Timestamp(Date(endDate)),
        "LOCATION" to location,
        "STATUS" to status.name
    )
}