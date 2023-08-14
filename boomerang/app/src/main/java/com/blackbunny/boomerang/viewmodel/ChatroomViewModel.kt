package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.message.Message
import com.blackbunny.boomerang.data.message.MessageRepository
import com.blackbunny.boomerang.domain.product.Product
import com.blackbunny.boomerang.domain.product.RequestProductDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
 * ChatroomViewModel
 */
@HiltViewModel
class ChatroomViewModel @Inject constructor(
    private val repository: MessageRepository,
    private val requestProductDetail: RequestProductDetailUseCase
) : ViewModel(){

    private val TAG = "ChatroomViewModel"

    private val _uiState = MutableStateFlow(ChatroomState())
    val uiState = _uiState.asStateFlow()


    fun observeMessages(chatId: String) {
        viewModelScope.launch {
            repository.observeMessagesFrom(chatId)
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest {  result ->
                    result.fold(
                        onSuccess = { msg ->
                            _uiState.update {
                                it.copy(
                                    messages = it.messages.notifyDataChanged(msg)
                                )
                            }
                        },
                        onFailure = {
                            Log.d(TAG, "Unable to retrieve message from database.")
                            it.printStackTrace()
                        }
                    )
                }
        }
    }

    fun requestMessages(chatId: String) {
        // TODO: Should be refactored later.
        if (_uiState.value.messages.isNotEmpty()) {
            return
        }

        viewModelScope.launch {
            val clearCurrentList = async {
                _uiState.update {
                    it.copy(
                        messages = emptyList()
                    )
                }
            }.await()

            val receiveMessage = async {
                repository.getAllMessagesFrom(chatId)
                    .flowOn(Dispatchers.IO)
                    .map { Result.success(it) }
                    .catch { emit(Result.failure(it)) }
                    .collectLatest { result ->
                        result.fold(
                            onSuccess = { message ->
                                val temp = _uiState.value.messages

                                _uiState.update {
                                    it.copy(
                                        messages = temp.notifyDataChanged(message)
                                    )
                                }
                            },
                            onFailure = {
                                Log.d(TAG, "Unable to retrieve messages\n\tReason: ${it.stackTrace}")
                                it.printStackTrace()
                            }
                        )
                    }
            }.await()
        }
    }

    fun updateNewMessageTextField(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                newMessageText = value
            )
        }
    }

    @Deprecated("Old version of sendMessage function. Replaced with one has 3 parameters.")
    fun sendMessage(chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendNewMessage(chatId, _uiState.value.newMessageText.text)
        }.invokeOnCompletion {
            _uiState.update {
                it.copy(
                    newMessageText = TextFieldValue()
                )
            }
        }
    }

    /**
     * sendMessage
     */
    fun sendMessage(chatId: String, receiverId: String, title: String, isInitial: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_uiState.value.messages.isEmpty() && isInitial) {
                // Chatroom is in its initial status, and the very first message is being attempted to be sent.
                // TODO: 1. add given chatroom id
                repository.requestInitialChatroomSettings(chatId, receiverId, title)
            }
            // There is a message history in the given chatroom.
            repository.sendNewMessage(chatId, _uiState.value.newMessageText.text)
        }.invokeOnCompletion {
            _uiState.update {
                it.copy(
                    newMessageText = TextFieldValue()
                )
            }
        }
    }

    fun requestProductDetail(chatId: String) {
        viewModelScope.launch {
            requestProductDetail.requestProductDetail(chatId)
                .fold(
                    onSuccess = { result ->
                        _uiState.update {
                            it.copy(
                                requestedProduct =  result,
                                productDetailVisibility = true
                            )
                        }
                    },
                    onFailure = {
                        Log.d(TAG, "Unable to fetch the product detail.")
                        it.printStackTrace()
                    }
                )
        }
    }

    fun dismissProductDetailScreen() {
        _uiState.update {
            it.copy(
                requestedProduct = null,
                productDetailVisibility = false
            )
        }
    }


    private fun List<Message>.notifyDataChanged(newData: Message): List<Message> {
        return if (this.contains(newData)) {
            this
        } else {
            val raw = List<Message>(this.size + 1) {
                if (it < this.size) this[it]
                else newData
            }


//            return raw.sortedBy {
//                it.timestamp
//            }
            /* TODO: Should be revert to code snippet above, if the reverseLayout option set back to false. */
            return raw.sortedWith(
                compareByDescending { it.timestamp }
            )
        }
    }

}

data class ChatroomState(
    val messages: List<Message> = emptyList(),
    val newMessageText: TextFieldValue = TextFieldValue(),
    val requestedProduct: Product? = null,
    val productDetailVisibility: Boolean = false
)