package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.transaction.Transaction
import com.blackbunny.boomerang.data.transaction.TransactionRepository
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
 * Created 8/28/23 at 4:44 PM
 */
@HiltViewModel
class CompletedTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val TAG = "CompletedTransactionViewModel"

    private val _uiState = MutableStateFlow(CompletedTransactionUiState())
    val uiState = _uiState.asStateFlow()

    fun getCompletedTransaction(userId: String) {
        _uiState.update {
            it.copy(
                completedTransactionList = emptyList()
            )
        }
        viewModelScope.launch {
            transactionRepository.fetchCompletedTransaction(userId)
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { transaction ->
                            _uiState.update {
                                it.copy(
                                    completedTransactionList = _uiState.value.completedTransactionList.notifyNewData(transaction)
                                )
                            }
                        },
                        onFailure = {
                            Log.d(TAG, "Unable to fetch completed transactions")
                            it.printStackTrace()
                        }
                    )
                }
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

}

data class CompletedTransactionUiState(
    val completedTransactionList: List<Transaction> = emptyList()
)