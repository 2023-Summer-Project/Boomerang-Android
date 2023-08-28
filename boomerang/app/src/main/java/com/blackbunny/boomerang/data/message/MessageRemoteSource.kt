package com.blackbunny.boomerang.data.message

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import com.blackbunny.boomerang.domain.product.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessageRemoteSource @Inject constructor() {
    private val TAG = "MessageRemoteSource"
    private val ref = Firebase.database

    private val localCoroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * fetchChatroom list.
     * Fetch chat room list, where current user is associated with.
     */
    fun fetchAvailableChatroom() = callbackFlow {
        ref.getReference("chat")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chats = snapshot.value as HashMap<String, HashMap<String, String>>

                    for (chat in chats) {
                        Log.d(TAG, "ID: ${chat.key}")
                        Log.d(TAG, "VALUE: ${chat.value.values} ")

                        trySend(chat)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Error: ${error.message}\n${error.details}")
                }

            })

        awaitClose { /* Do nothing currently */ }
    }

    fun observeChatroomList() = callbackFlow{
        ref.getReference("chat")
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildAdded: ${snapshot.key}")
                    trySend(Pair("ADDED", snapshot))
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildChanged: ${snapshot.key} ${snapshot.value}")
                    trySend(Pair("CHANGED", snapshot))
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.d(TAG, "onChildRemoved: ${snapshot.key} ${snapshot.value}")
                    trySend(Pair("REMOVED", snapshot))
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        awaitClose { /* Do nothing currently */ }
    }


    /**
     * fetchChatroomMessages
     * Fetch all messages from the given chatroom.
     */
    fun fetchChatroomMessages(id: String) = callbackFlow {
        ref.getReference("messages").child(id)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val messages = snapshot.value as HashMap<String, HashMap<String, String>>

                        for (message in messages) {
                            trySend(message)
                        }
                    } catch(e: Exception) {
                        Log.d(TAG, "Unable to fetch messages: " +
                                "There could be various reason, but in most case, this exception means there is no message history.")
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        awaitClose { /* Do nothing rn */ }
    }

    fun observeMessages(chatId: String) = callbackFlow {
        ref.getReference("messages").child(chatId)
            .addChildEventListener(object: ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "Retrieve message under chatroom id: $chatId")
                    trySend(Pair("ADDED", snapshot))
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "Change has been made on messages under chatroom id: $chatId")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.d(TAG, "Message has been removed from chatroom id: $chatId")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "Message has been moved from chatroom id: $chatId")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Request has been cancelled.")
                    Log.d(TAG, error.message)
                    Log.d(TAG, error.details)
                }
            })
        awaitClose {/* Do nothing. */ }
    }

    fun sendNewMessage(chatId: String, message: Message) {
        val newMessage = message.copy(
            userName = "ados",      //Test value.
            userUid = Firebase.auth.currentUser?.uid
        )

        val messageNode = hashMapOf(
            "message" to newMessage.message,
            "timestamp" to newMessage.timestamp,
            "user_name" to newMessage.userName,
            "user_uid" to newMessage.userUid
        )

        val childUpdates = mapOf(
            "/chat/${chatId}/last_message" to newMessage.message,
            "/chat/${chatId}/last_timestamp" to newMessage.timestamp,
            "/messages/${chatId}/${newMessage.id}" to messageNode
        )

        ref.getReference().updateChildren(childUpdates)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully update new message")
            }
            .addOnFailureListener {
                Log.d(TAG, "Unable to update new message")
                it.printStackTrace()
            }

    }

    /**
     * Should be called under CoroutineScope.
     * @TODO: Should be refactored later in a better way. (add Sender / Receiver fields on Chat node.)
     */
    fun removeChatroom(chatId: String) {
        val senderId = Firebase.auth.currentUser!!.uid
        val receiverId = with(chatId.split("_")) {
            if (this[0] == senderId) this[1]
            else this[0]
        }

        val childrenUpdates = mapOf(
            "/chat/${chatId}" to null,
            "/messages/${chatId}" to null,
            "user/${senderId}/chat_list/${chatId}/" to null,
            "user/${receiverId}/chat_list/${chatId}/" to null
        )

        ref.getReference().updateChildren(childrenUpdates)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully remove given chatroom")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Unable to remove given chatroom")
                exception.printStackTrace()
            }


        /*
        Firebase.firestore.collection("Product")
            .document(chatId.split("_")[1]).get(Source.DEFAULT)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully retrieve receiver ID.")
                val senderId = Firebase.auth.currentUser!!.uid
                val receiverId = it.data!!["OWNER_ID"]

                val childrenUpdates = mapOf(
                    "/chat/${chatId}" to null,
                    "/messages/${chatId}" to null,
                    "user/${senderId}/chat_list/${chatId}/" to null,
                    "user/${receiverId}/chat_list/${chatId}/" to null
                )

                ref.getReference().updateChildren(childrenUpdates)
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully remove given chatroom")
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Unable to remove given chatroom")
                        exception.printStackTrace()
                    }

            }
            .addOnFailureListener {
                Log.d(TAG, "Unable retrieve receiver ID, therefore, chatroom removal request has been canceled")
                it.printStackTrace()
            }
         */
    }

    /**
     * TODO: Should be isolated into asynchronous solution.
     */
    fun requestSessionUser(): String {
        return Firebase.auth.currentUser?.uid ?: ""
    }

    /**
     * postNewChatroom_Test
     * Request multiple updates at once.
     */
    suspend fun postNewChatroom_Test(product: Product) = suspendCoroutine<Pair<String, Boolean>> { continuation ->
        val senderId = Firebase.auth.currentUser!!.uid
        val receiverId = product.ownerId

        // Chatroom ID: SENDER-ID_PRODUCT-ID
        // Scheme has been changed: SENDER-ID_RECEIVER-ID_PRODUCT_ID
        val chatroomId = "${senderId}_${receiverId}_${product.productId}"

        // 1. Check whether the current user already has a same chatroom.
        val currentSenderChatroom = ref.getReference("user").child(senderId).child("chat_list")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "Received DataSnapshot: $it")
//                val senderChatrooms = (it.value as HashMap<String, String>).values.toList()

                // NOTE: Scheme has been changed.
                val senderChatrooms = (it.value as HashMap<String, String>).keys.toList()


                if (senderChatrooms.contains(chatroomId)) {
                    continuation.resume(Pair(chatroomId, false))
                } else {
                    continuation.resume(Pair(chatroomId, true))
                }
            }
    }

    suspend fun chatroomConfiguration(chatroomId: String, receiverId: String, title: String) {
        val senderId = Firebase.auth.currentUser!!.uid

        // Key 1 (sender)   Note: Scheme has been changed. this value would be used as a 'value.'
        val senderKey = ref.getReference("user").child(senderId).child("chat_list").push().key
        // Key 2 (receiver) Note: Scheme has been changed. THis value would be used as a 'value.'
        val receiverKey = ref.getReference("user").child(receiverId).child("chat_list").push().key

        val childUpdates = mapOf<String, Any>(
            "/user/${senderId}/chat_list/${chatroomId}" to senderKey!!,       // Update sender-side chat_list.    NOTE: Scheme has been changed.
            "/user/${receiverId}/chat_list/${chatroomId}" to receiverKey!!,    // Update receiver-side chat_list  NOTE: Scheme has been changed.
            "/chat/${chatroomId}/last_message" to "",
            "/chat/${chatroomId}/last_timestamp" to System.currentTimeMillis(),
            "/chat/${chatroomId}/title" to title
        )

        // Update multiple nodes at the same time.
        ref.reference.updateChildren(childUpdates)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully update multiple nodes.")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to update multiple nodes.")
                it.printStackTrace()
            }
    }

    /**
     * postNewChatroom
     * Create new chatroom based on given product ID.
     * @return Chatroom Id.
     */
    @Deprecated("Subjected to be removed.")
    suspend fun postNewChatroom(product: Product) = suspendCoroutine<String>{ continuation ->
        val senderId = Firebase.auth.currentUser!!.uid
        val receiverId = product.ownerId

        val chatroomId = "${senderId}_${product.productId}"

        // Check whether sender already have a chatroom for given product.
        val currentSenderChatRoom = ref.getReference("user").child(senderId).child("chat_list")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "Received DataSnapshot: $it")
                val senderChatrooms = (it.value as HashMap<String, String>).values.toList()


                if (senderChatrooms.contains(chatroomId)) {
                    // Chatroom already exists -> Return detected chatroom.
                    continuation.resume(chatroomId)
                } else {
                    localCoroutineScope.launch {
                        if (updateUserChatroom(senderId, chatroomId)
                            && updateUserChatroom(receiverId, chatroomId)) {
                            if(createChatroom(chatroomId, product.productName)) {
                                Log.d(TAG, "Complete all task to create new chatroom")
                                continuation.resume(chatroomId)
                            } else {
                                continuation.resume("")
                            }
                        } else {
                            continuation.resume("")
                        }
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "Unable to fetch user chatrooms from Realtime Database.")
                it.printStackTrace()
                continuation.resume("")
            }

    }

    @Deprecated("Subjected to be removed.")
    suspend fun createChatroom(chatId: String, title: String) = suspendCoroutine { continuation ->
        ref.getReference("chat").child(chatId)
            .setValue(
                hashMapOf(
                    "last_message" to "",
                    "last_timestamp" to System.currentTimeMillis(),
                    "title" to title
                )
            )
            .addOnSuccessListener {
                Log.d(TAG, "Successfully add new chatroom")


                continuation.resume(true)
            }
            .addOnFailureListener {
                Log.d(TAG, "Unable to add new chatroom")
                it.printStackTrace()
                continuation.resume(false)
            }
    }

    @Deprecated("Subjected to be removed.")
    suspend fun updateUserChatroom(userId: String, chatroomId: String) = suspendCoroutine { continuation ->
        ref.getReference("user").child(userId).child("chat_list")
            .push().setValue(
                chatroomId
            )
            .addOnSuccessListener {
                Log.d(TAG, "Successfully add new chatroom to corresponding user.")
                continuation.resume(true)
            }
            .addOnFailureListener {
                Log.d(TAG, "Unable to add new chatroom to corresponding user.")
                it.printStackTrace()
                continuation.resume(false)
            }
    }


}

data class Chat(
    val id: String = "",
    val lastMessage: String? = "",
    val lastTimestamp: Long? = 0L,
    val title: String? = ""
)

data class Message(
    val id: String = "",
    val message: String? = "",
    val timestamp: Long? = 0L,
    val userName: String? = "",
    val userUid: String? = "",
    val fromOpponent: Boolean = false
)