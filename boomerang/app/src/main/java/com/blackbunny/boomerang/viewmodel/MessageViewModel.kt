package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.message.Chat
import com.blackbunny.boomerang.data.message.Message
import com.blackbunny.boomerang.data.message.MessageRemoteSource
import com.blackbunny.boomerang.data.message.MessageRepository
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

@HiltViewModel
class MessageViewModel @Inject constructor(
    // Test
    private val remoteSource: MessageRemoteSource,
    private val repository: MessageRepository
) : ViewModel() {
    private val TAG = "MessageViewModel"

    private val _uiState = MutableStateFlow(MessageScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        observeChatrooms()
    }

//    fun observableTest() {
//        viewModelScope.launch {
//            remoteSource.observeChatroomList()
//        }
//    }

    fun getAvailableChat() {
        _uiState.update {
            it.copy(
                isBeingLoaded = true
            )
        }


        viewModelScope.launch {
            repository.getAvailableChats()
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    Log.d(TAG, "New chat collected.")
                    result.fold(
                        onSuccess = { chat ->
                            Log.d(TAG, "Result: ${chat.id}")
                            _uiState.update {
                                it.copy(
                                    chatList = it.chatList.notifyDatasetChanged(chat),
                                    isBeingLoaded = false
                                )
                            }

                        },
                        onFailure = {
                            Log.d(TAG, "UNABLE: ${it.message}")
                            it.printStackTrace()
                        }
                    )
                }
        }
    }

    fun observeChatrooms() {
        _uiState.update {
            it.copy(
                isBeingLoaded = true
            )
        }

        viewModelScope.launch {
            repository.observeChatroomList()
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { action ->
                    action.fold(
                        onSuccess = { value ->
                            when(value.first) {
                                "ADDED" -> {
                                    _uiState.update {
                                        it.copy(
                                            chatList = it.chatList.add(value.second),
                                            isBeingLoaded = false
                                        )
                                    }
                                }
                                "CHANGED" -> {
                                    _uiState.update {
                                        it.copy(
                                            chatList = it.chatList.update(value.second)
                                        )
                                    }
                                }
                                "REMOVED" -> {
                                    _uiState.update {
                                        it.copy(
                                            chatList = it.chatList.remove(value.second)
                                        )
                                    }
                                }
                            }
                        },
                        onFailure = {
                            Log.d(TAG, "Unable to retrieve chatroom list.")
                            it.printStackTrace()
                        }
                    )
                }
        }
    }

    fun clearUnreadMessages() {
        _uiState.update {
            it.copy(
                unreadMessageCount = 0
            )
        }
    }

    fun removeChatroom(chatId: String) {
        viewModelScope.launch {
            repository.removeChatroom(chatId)
        }
    }

    // Builk Updates.
    private fun List<Chat>.notifyDatasetChanged(newData: Chat): List<Chat> {
        // TODO: should be refactored later in a better way.
        return if (this.contains(newData)) {
            Log.d(TAG, "Current Chat has new message")
            this
        } else {
            val raw = List(this.size + 1) {
                Log.d(TAG, "Has new chatroom")
                if (it < this.size){
                    this[it]
                } else {
                    newData
                }
            }
            return raw.sortedWith(
                compareByDescending { it.lastTimestamp }
            )
        }
    }


    /* generic approach of extension function is possible. */
    private fun <T> List<T>.add(newData: T): List<T> {
        return emptyList()
    }


    private fun List<Chat>.add(newData: Chat): List<Chat> {
        return if (this.contains(newData)) {
            Log.d(TAG, "Current chatroom list is up-to-date.")
            this
        } else {
            val raw = List(this.size + 1) {
                if (it < this.size) {
                    this[it]
                } else {
                    newData
                }
            }
            return raw.sortedWith(
                compareByDescending { it.lastTimestamp }
            )
        }
    }

    private fun List<Chat>.update(newData: Chat): List<Chat> {
        return List(this.size) {
            if (this[it].id == newData.id) {
                newData
            } else {
                this[it]
            }
        }.sortedWith(
            compareByDescending { value -> value.lastTimestamp }
        )
    }

    private fun List<Chat>.remove(newData: Chat): List<Chat> {
        return this.toMutableList()
            .also {
                it.remove(newData)
            }.toList()
    }
}

data class MessageScreenState(
    val chatList: List<Chat> = emptyList(),
    val isBeingLoaded: Boolean = false,
    val individualChatroomVisibility: Boolean = false,
    val selectedIndividualChatroom: Chat? = null,
    val chatroomMessages: List<Message> = emptyList(),          // ???
    val isNewMessageChecked: Boolean = true,
    val unreadMessageCount: Int = 0,
)