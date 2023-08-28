package com.blackbunny.boomerang.data.transaction

import com.blackbunny.boomerang.data.TransactionStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 8/28/23 at 11:47 AM
 */
class TransactionRepository @Inject constructor(
  private val remoteSource: RemoteTransactionDataSource
) {
    private val TAG = "TransactionRepository"

    suspend fun postRentRequest(transaction: Transaction): Result<Unit> {
        return if (remoteSource.postRentRequest(transaction)) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Unable to post new rent request"))
        }
    }

    fun fetchTransactions(userId: String, role: String): Flow<Pair<Transaction, DocumentChange.Type>> {
        return remoteSource.fetchTransactions(userId, role).map { change ->
            Pair(
                Transaction(
                    transactionId = change.document.id,
                    productId = change.document["PRODUCT_ID"] as String,
                    productImage = change.document["PRODUCT_IMAGE"] as String,
                    productName = change.document["PRODUCT_NAME"] as String,
                    renter = change.document["RENTER"] as String,
                    rentee = change.document["RENTEE"] as String,
                    price = (change.document["PRICE"] as Long).toInt(),
                    deposit = (change.document["DEPOSIT"] as Long).toInt(),
                    startDate = (change.document["START_DATE"] as Timestamp).toDate().time,
                    endDate = (change.document["END_DATE"] as Timestamp).toDate().time,
                    location = change.document["LOCATION"] as String,
                    status = when (change.document["STATUS"] as String) {
                        TransactionStatus.REQUESTED.name -> {
                            TransactionStatus.REQUESTED
                        }
                        TransactionStatus.ACCEPTED.name -> {
                            TransactionStatus.ACCEPTED
                        }
                        TransactionStatus.REJECTED.name -> {
                            TransactionStatus.REJECTED
                        }
                        TransactionStatus.NOT_RETURNED.name -> {
                            TransactionStatus.NOT_RETURNED
                        }
                        TransactionStatus.COMPLETED.name -> {
                            TransactionStatus.COMPLETED
                        }
                        else -> { TransactionStatus.YET_INITIALIZED }
                    }
                ),
                change.type
            )
        }
    }

    fun updateTransaction(transactionId: String, newStatus: TransactionStatus) {
        remoteSource.updateTransaction(transactionId, newStatus)
    }

    fun fetchCompletedTransaction(userId: String)
        = remoteSource.fetchCompletedTransaction(userId)
            .map {
                Transaction(
                    transactionId = it.id,
                    productId = it["PRODUCT_ID"] as String,
                    productImage = it["PRODUCT_IMAGE"] as String,
                    productName = it["PRODUCT_NAME"] as String,
                    renter = it["RENTER"] as String,
                    rentee = it["RENTEE"] as String,
                    price = (it["PRICE"] as Long).toInt(),
                    deposit = (it["DEPOSIT"] as Long).toInt(),
                    startDate = (it["START_DATE"] as Timestamp).toDate().time,
                    endDate = (it["END_DATE"] as Timestamp).toDate().time,
                    location = it["LOCATION"] as String,
                    status = when (it["STATUS"] as String) {
                        TransactionStatus.REQUESTED.name -> {
                            TransactionStatus.REQUESTED
                        }
                        TransactionStatus.ACCEPTED.name -> {
                            TransactionStatus.ACCEPTED
                        }
                        TransactionStatus.REJECTED.name -> {
                            TransactionStatus.REJECTED
                        }
                        TransactionStatus.NOT_RETURNED.name -> {
                            TransactionStatus.NOT_RETURNED
                        }
                        TransactionStatus.COMPLETED.name -> {
                            TransactionStatus.COMPLETED
                        }
                        else -> { TransactionStatus.YET_INITIALIZED }
                    }
                )
            }

}