package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.TransactionStatus
import com.blackbunny.boomerang.data.transaction.Transaction
import com.blackbunny.boomerang.data.transaction.TransactionRepository
import com.google.firebase.firestore.DocumentChange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 8/28/23 at 1:05 PM
 */
@HiltViewModel
class MyTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val TAG = "MyTransactionViewModel"

    private val _uiState = MutableStateFlow(MyTransactionUiState())
    val uiState = _uiState.asStateFlow()

    fun updateSelectedTab(value: Int) {
        _uiState.update {
            it.copy(
                selectedTab = value
            )
        }
    }

    fun getTransactionRequestSent(userId: String) {
        _uiState.update {
            it.copy(
                requestsSent = emptyList()
            )
        }
        viewModelScope.launch {
            transactionRepository.fetchTransactions(userId, "RENTEE")
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { transaction ->
                            // OnSucces
                            _uiState.update {
                                when(transaction.second) {
                                    DocumentChange.Type.ADDED -> {
                                        it.copy(
                                            requestsSent = _uiState.value.requestsSent.add(transaction.first)
                                        )
                                    }
                                    DocumentChange.Type.MODIFIED -> {
                                        it.copy(
                                            requestsSent = _uiState.value.requestsSent.update(transaction.first)
                                        )
                                    }
                                    DocumentChange.Type.REMOVED -> {
                                        it.copy(
                                            requestsSent = _uiState.value.requestsSent.remove(transaction.first)
                                        )
                                    }
                                }
                            }
                        },
                        onFailure = {
                            // OnFailure
                            Log.d(TAG, "Unable to fetch request")
                            it.printStackTrace()
                        }
                    )
                }
        }
    }

    fun getTransactionRequest(userId: String) {
        _uiState.update {
            it.copy(
                requestsReceived = emptyList()
            )
        }
        viewModelScope.launch {
            transactionRepository.fetchTransactions(userId, "RENTER")
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { transaction ->
                            // OnSucces
                            _uiState.update {
                                when(transaction.second) {
                                    DocumentChange.Type.ADDED -> {
                                        it.copy(
                                            requestsReceived = _uiState.value.requestsReceived.add(transaction.first)
                                        )
                                    }
                                    DocumentChange.Type.MODIFIED -> {
                                        it.copy(
                                            requestsReceived = _uiState.value.requestsReceived.update(transaction.first)
                                        )
                                    }
                                    DocumentChange.Type.REMOVED -> {
                                        it.copy(
                                            requestsReceived = _uiState.value.requestsReceived.remove(transaction.first)
                                        )
                                    }
                                }
                            }
                        },
                        onFailure = {
                            // OnFailure
                            Log.d(TAG, "Unable to fetch request")
                            it.printStackTrace()
                        }
                    )
                }
        }
    }

    fun updateTransactionStatus(transactionId: String, newStatue: TransactionStatus) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transactionId, newStatue)
        }
    }

    private fun List<Transaction>.notifyNewData(newData: Transaction): List<Transaction> {
        return if (this.contains(newData)) {
            Log.d(TAG, "STATUS: ${this.contains(newData)}")
            this
        } else {
            List<Transaction>(this.size + 1) {
                if (it < this.size) this[it]
                else newData
            }
        }
    }

    /**
     * Add New Data
     */
    private fun List<Transaction>.add(newData: Transaction): List<Transaction> {
        return List<Transaction>(this.size + 1) {
            if (it < this.size) this[it]
            else newData
        }
    }

    /**
     * Remove Selected Data
     */
    private fun List<Transaction>.remove(data: Transaction): List<Transaction> {
        val temp = this.toMutableList()
        return temp.apply {
            this.remove(data)
            this.toList()
        }
    }

    /**
     * Update Selected Data
     */
    private fun List<Transaction>.update(data: Transaction): List<Transaction> {
        val temp = this.toMutableList()
        return temp.map {
            if (it.transactionId == data.transactionId) {
                it.copy(
                    productId = data.productId,
                    productImage = data.productImage,
                    productName = data.productName,
                    renter = data.renter,
                    rentee = data.rentee,
                    price = data.price,
                    deposit = data.deposit,
                    startDate = data.startDate,
                    endDate = data.endDate,
                    location = data.location,
                    status = data.status
                )
            } else {
                it
            }
        }
    }

}

data class MyTransactionUiState(
    val selectedTab: Int = 0,
    val requestsReceived: List<Transaction> = emptyList(),
    val requestsSent: List<Transaction> = emptyList()
)