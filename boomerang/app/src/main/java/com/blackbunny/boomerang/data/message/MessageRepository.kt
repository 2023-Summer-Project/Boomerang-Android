package com.blackbunny.boomerang.data.message

import android.util.Log
import com.blackbunny.boomerang.domain.product.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val remoteSource: MessageRemoteSource
) {
    private val TAG = "MessageRepository"

    /**
     * getAvailableChats
     * Note: Scheme has been changed.
     */
    fun getAvailableChats(): Flow<Chat> {
        return remoteSource.fetchAvailableChatroom()
            .map {
                Chat(
                    id = it.key,
                    lastMessage = it.value["last_message"] ?: "메시지 없음",
                    lastTimestamp = it.value["last_timestamp"] as Long ?: 0L,
                    title = it.value["title"] ?: "새로운 채팅방"
                ).also {
                    Log.d(TAG, "Current Chat: ${it.toString()}")
                }
            }
    }

    /**
     * observeChatroomList
     * Observe by event on its children.
     */
    fun observeChatroomList(): Flow<Pair<String, Chat>> {
        return remoteSource.observeChatroomList()
            .map {
                val values = it.second.value as HashMap<String, String>
                Pair(
                    it.first,
                    Chat(
                        id = it.second.key!!,
                        lastMessage = values["last_message"] ?: "메세지 없음",
                        lastTimestamp = values["last_timestamp"] as Long ?: 0L,
                        title = values["title"] ?: "새로운 채팅방"
                    )
                )
            }
    }

    fun getAllMessagesFrom(id: String): Flow<Message> {
        val sessionUser = remoteSource.requestSessionUser()
        return remoteSource.fetchChatroomMessages(id)
            .map {
                Message(
                    id = it.key,
                    message = it.value["message"],
                    timestamp = it.value["timestamp"] as Long,
                    userName = it.value["user_name"],
                    userUid = it.value["user_uid"],
                    fromOpponent = it.value["user_uid"] != sessionUser
                ).also {receivedMessage ->
                    Log.d(TAG, "Received Message: ${receivedMessage.message}")
                }
            }
    }

    fun observeMessagesFrom(chatId: String): Flow<Message> {
        val sessionUser = remoteSource.requestSessionUser()
        return remoteSource.observeMessages(chatId)
            .map {
                val values = it.second.value as HashMap<String, String>
                Message(
                    id = it.second.key as String,
                    message = values["message"],
                    timestamp = values["timestamp"] as Long,
                    userName = values["user_name"],
                    userUid = values["user_uid"],
                    fromOpponent = values["user_uid"] != sessionUser
                ).also {receivedMessage ->
                    Log.d(TAG, "Received Message: ${receivedMessage.message}")
                }
            }
    }

    suspend fun sendNewMessage(chatId: String, newMessage: String) {
        remoteSource.sendNewMessage(chatId, Message(
            id = UUID.randomUUID().toString(),
            message = newMessage,
            timestamp = System.currentTimeMillis()
        ))
    }

    /**
     * requestNewChatroom
     * Request New Chatroom from product.
     */
    suspend fun requestNewChatroom(product: Product): String {
        return remoteSource.postNewChatroom(product)
    }

    suspend fun validateChatroomCreation(product: Product): Pair<String, Boolean> {
        return remoteSource.postNewChatroom_Test(product)
    }

    suspend fun requestInitialChatroomSettings(chatroomId: String, receiverId: String, title: String) {
        remoteSource.chatroomConfiguration(chatroomId, receiverId, title)
    }

    /**
     * Remove Chatroom
     */
    fun removeChatroom(chatId: String) {
        remoteSource.removeChatroom(chatId)
    }

    /*
    data class Message(
    val id: String = "",
    val message: String? = "",
    val timestamp: Long? = 0L,
    val userName: String? = "",
    val userUid: String? = "",
    val fromOpponent: Boolean = false
)
     */

}