package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.DataFetchRequest
import com.blackbunny.boomerang.data.MainServiceUiState
import com.blackbunny.boomerang.data.TransactionStatus
import com.blackbunny.boomerang.data.authentication.UserRepository
import com.blackbunny.boomerang.data.message.MessageRepository
import com.blackbunny.boomerang.data.transaction.Transaction
import com.blackbunny.boomerang.data.transaction.TransactionRepository
import com.blackbunny.boomerang.domain.product.FetchProductForCardUseCase
import com.blackbunny.boomerang.domain.product.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainServiceViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cardDataUseCase: FetchProductForCardUseCase,
    private val messageRepository: MessageRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState= MutableStateFlow(MainServiceUiState())
    val uiState: StateFlow<MainServiceUiState> = _uiState.asStateFlow()

    init {
        getCurrentSessionUser()
        getAllProduct()
    }

    fun updateDetailViewVisibility(newStatus: Boolean) {
        _uiState.update {
            it.copy(
                isDetailViewVisible = newStatus
            )
        }
    }

    fun getDetailedView(product: Product) {
        _uiState.update {
            it.copy(
                isDetailViewVisible = true,
                currentProductOnDetailView = product,
                refreshEnabled = false
            )
        }
    }

    fun dismissDetailedView() {
        _uiState.update {
            it.copy(
                isDetailViewVisible = false,
                currentProductOnDetailView = null,
                refreshEnabled = true
            )
        }
    }


    fun getCurrentSessionUser() = viewModelScope.launch {
        userRepository.fetchSessionUser()
            .fold(
                onSuccess = { user ->
                    Log.d("MainServiceViewModel", "User found: ${user.email}")
                    _uiState.update {
                        it.copy(
                            sessionUser = user
                        )
                    }
                },
                onFailure = { exception ->
                    Log.d("MainServiceViewModel", "Error on fetching current session user." )
                }
            )
    }


    fun updateRefreshEnabled(value: Boolean) {
        _uiState.update {
            it.copy(
                refreshEnabled = value
            )
        }
    }

    fun dismissTemporaryChatroom() {
        _uiState.update {
            it.copy(
                temporaryChatroomVisibility = false,
                requestedChatroom = ""
            )
        }
    }

    fun requestRefresh() {
        _uiState.update {
            it.copy(
                isRefreshing = true
            )
        }

        getAllProduct().also {
            _uiState.update {
                it.copy(
                    isRefreshing = false
                )
            }
        }
    }

    fun getAllProduct() {

        _uiState.update {
            it.copy(
                initialDataFetching = DataFetchRequest.REQUESTED,
                lazyGridDataSource = emptyList()
            )
        }

        Log.d("MainServiceViewModel", "getAllProduct Called")

        viewModelScope.launch {
            cardDataUseCase.invoke()
                .flowOn(Dispatchers.IO)
                .map {
                    Result.success(it)
                }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { value ->
                            var newDataset = emptyList<Product>()
                            if (value.ownerId == _uiState.value.sessionUser.uid) {
                                newDataset = updateDataSet(
                                    _uiState.value.lazyGridDataSource,
                                    value.copy(
                                        isOwnedBySessionUser = true
                                    )
                                )
                            } else {
                                newDataset = updateDataSet(_uiState.value.lazyGridDataSource, value)
                            }
                            Log.d("MainServiceViewModel", "Dataset: ${newDataset.toString()}")
                            _uiState.update {
                                it.copy(
                                    lazyGridDataSource = newDataset
                                )
                            }
                        },
                        onFailure = {
                            Log.d("MainServiceViewModel", "Exception Thrown: ${it.message}")
                            it.printStackTrace()
                        }
                    )
                }
            delay(1000L)            // Subject to be removed.

            _uiState.update {
                it.copy(
                    initialDataFetching = DataFetchRequest.READY
                )
            }
        }
    }

    /**
     * Send Rent request.
     */
    fun sendRentRequest(product: Product, from: Long, until: Long) {
        viewModelScope.launch {
            transactionRepository.postRentRequest(
                Transaction(
                    productId = product.productId,
                    productImage = product.coverImage,
                    productName = product.productName,
                    renter = product.ownerId,
                    rentee = _uiState.value.sessionUser.uid,
                    price = getTotalPrice(from, until, product.price.toInt()),
                    startDate = from,
                    endDate = until,
                    location = product.location,
                    status = TransactionStatus.REQUESTED
                )
            ).fold(
                onSuccess = {
                    // Successfully post new rent request.
                    Log.d("MainServiceViewModel", "Successfully post new rent request.")
                },
                onFailure = {
                    // Unable to post new rent request.
                    Log.d("MainServiceViewModel", "Unable to post new rent request.")
                    it.printStackTrace()
                }
            )
        }
    }

    private fun getTotalPrice(from: Long, until: Long, pricePerDay: Int): Int {
        val dayDiff = (until - from) / 86400000

        return if (dayDiff <= 1) {
            pricePerDay
        } else {
            (dayDiff * pricePerDay).toInt()
        }
    }


    /**
     * requestNewChatroom
     * Request chatroom for given product.
     * TODO: Should be refactored later.
     */
    fun requestNewChatroom(product: Product) {
        viewModelScope.launch {
            val id = messageRepository.requestNewChatroom(product)
            if (id.isNotBlank()) {
                _uiState.update {
                    it.copy(
                        requestedChatroomId = id
                    )
                }
            }
        }
    }

    /**
     * Problem: chatroom validation takes too much time.
     */
    fun requestNewChatroom_Test(product: Product) {
        viewModelScope.launch {
            val chatroom = messageRepository.validateChatroomCreation(product)
            if (chatroom.first.isNotBlank()) {
                _uiState.update {
                    it.copy(
                        requestedChatroom = chatroom.first,
                        temporaryChatroomVisibility = chatroom.second,
                    )
                }
            }
        }
    }

    private fun updateDataSet(current: List<Product>, newValue: Product): List<Product> {
        val temp: List<Product> = List(current.size + 1) {
            if (it < current.size) current[it]
            else newValue
        }
        return temp
    }


}