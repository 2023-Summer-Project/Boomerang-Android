package com.blackbunny.boomerang.data.product

import android.net.Uri
import android.util.Log
import com.blackbunny.boomerang.domain.product.Product
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class RemoteProductDataSource @Inject constructor() {
    private val TAG = "RemoteProductDataSource"
    // Firestore access.
    private val ref = Firebase.firestore
    private val imageStorage = Firebase.storage.reference

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun getAllProduct() = callbackFlow<QueryDocumentSnapshot> {
        Log.d(TAG, "getAllProduct Called")
        ref.collection("Product")
            .orderBy("TIMESTAMP", Query.Direction.DESCENDING)
            .get(Source.DEFAULT)
            .addOnSuccessListener { result ->
                Log.d(TAG, "Result Size: ${result.size()}")
                for (document in result) {
                    Log.d(TAG, "Document: ${document.id}")
                    trySend(document)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Exception thrown: ${exception.message}")
            }

        awaitClose {  }
    }

    /**
     * fetchSingleProduct
     * Fetch single product using given Product ID.
     * @return QueryDocumentSnapshot..
     */
    suspend fun fetchSingleProduct(productId: String) = suspendCoroutine { continuation ->
        Log.d(TAG, "fetchSingleProduct called.")
        ref.collection("Product").document(productId)
            .get(Source.DEFAULT)
            .addOnSuccessListener { result ->
                continuation.resume(result)
            }
            .addOnFailureListener {
                Log.d(TAG, "Unable to fetch Product using given Product ID.")
                it.printStackTrace()
            }
    }

    /**
     * fetchProducts
     * Fetch products owned by given user.
     * @return QueryDocumentSnapshot
     * TODO: Rename function.
     */
    fun fetchProducts(userId: String) = callbackFlow {
        Log.d(TAG, "Fetch product owned by $userId")
        ref.collection("Product")
            .whereEqualTo("OWNER_ID", userId)
//            .orderBy("TIMESTAMP", Query.Direction.DESCENDING)
            .get(Source.DEFAULT)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully retrieved ${it.documents.size} products owned by $userId")

                for (document in it) {
                    trySend(document)       // TODO: Should be refactored later.
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Unable to retrieve products owned by $userId")
                it.printStackTrace()
            }



        awaitClose { /* Currently do nothing */ }
    }

    // Upload new images.
    suspend fun postNewProduct(newProduct: Product): Result<Boolean> = suspendCoroutine { continuation ->
        // Final modification before post new product to the collection.
        val sessionUser = Firebase.auth.currentUser?.uid.toString()     // need to be refactored later.

        Log.d(TAG, "Get size of the list; ${newProduct.images.size}, ${newProduct.coverImage}")
        Log.d(TAG, "Post new product requested by ${Firebase.auth.currentUser?.uid.toString()}")

        // Should be replaced later.
        val temp = hashMapOf(
            "AVAILABILITY" to newProduct.availability,
            "AVAILABLE_TIME" to newProduct.availableTime,
            "IMAGES_MAP" to newProduct.images,
            "LOCATION" to newProduct.location,
            "OWNER_ID" to sessionUser,
            "OWNER_NAME" to newProduct.ownerName,
            "PROFILE_IMAGE" to newProduct.profileImage,
            "POST_CONTENT" to newProduct.content,
            "POST_TITLE" to newProduct.title,
            "PRICE" to newProduct.price.toInt(),
            "PRODUCT_NAME" to newProduct.productName,
            "PRODUCT_TYPE" to newProduct.productType,
            "TIMESTAMP" to Timestamp.now(),
            "LATITUDE" to newProduct.locationLatLng!!.latitude.toDouble(),
            "LONGITUDE" to newProduct.locationLatLng!!.longitude.toDouble()
        )


        // Add new record on "Product" collection using set() function.
        ref.collection("Product")
            .document(UUID.randomUUID().toString())
            .set(temp)
            .addOnSuccessListener {
                // Successfully post new product on the collection.
                Log.d(TAG, "New product has been post to the collection successfully.")
                continuation.resume(Result.success(true))
            }
            .addOnFailureListener {
                Log.d(TAG, "Unable to post new product to collection\n\t${it.message}")
                continuation.resume(Result.success(false))      // Need to ne validate whether it is proper way to handle this.
            }
    }

    // Implement Promise-like pattern.
    suspend fun uploadNewImage(file: File): Pair<String, String>? = suspendCoroutine { continuation ->
        // Storage Reference.
        var uri  = Uri.fromFile(file)
        val storageRef = imageStorage.child("${uri.lastPathSegment}")

        val uploadTask = storageRef.putFile(uri, storageMetadata {
            contentType = "image/jpg"
        })

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Exception thrown: ${task.exception?.message}")
                continuation.resume(null)
            }
            storageRef.downloadUrl
        }.addOnCompleteListener {task ->
            if (task.isSuccessful) {
                continuation.resume(Pair(file.nameWithoutExtension, task.result.toString()))
            }
        }
    }

    @Deprecated("No longer in used.")
    suspend fun getImageUrlAsync(filename: String): Uri? {
        return imageStorage.child(filename).downloadUrl
            .addOnSuccessListener {
                Log.d(TAG, "FETCH IMAGE SUCCESSFULLY: ${it ?: "NO URL FOUND"}")
                it
            }
            .addOnFailureListener {
                Log.d(TAG, "Unable to fetch the image\n\tReason: ${it.message}")
                null
            }.await()
    }

    /**
     * searchProduct Function
     * @limit: Currently only targeted to name of the post.
     * TODO: Find alternative way to support advanced search feature.
     */

    fun searchProductByKeyword(keyword: String) = callbackFlow {
        ref.collection("Product")
            .orderBy("POST_TITLE")
            .whereGreaterThanOrEqualTo("POST_TITLE", keyword)
            // "\uf8ff" is at an extremely high boundary, therefore, any unicode character with this keyword would greater than any other
            // unicode based text.
            .whereLessThanOrEqualTo("POST_TITLE", keyword + "\uf8ff")
            .get(Source.CACHE)
            .addOnSuccessListener {
                // Successfully received data.
                for (document in it.documents) {
                    trySend(document)
                }
            }
            .addOnFailureListener {
                // failed to receive data.
                Log.d(TAG, "Unable to fetch the data")
                it.printStackTrace()
            }

        awaitClose { /* currently do nothing. */ }
    }

}
