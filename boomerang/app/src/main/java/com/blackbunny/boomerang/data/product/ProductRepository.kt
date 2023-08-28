package com.blackbunny.boomerang.data.product

import android.net.Uri
import android.util.Log
import com.blackbunny.boomerang.domain.product.Product
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val remoteSource: RemoteProductDataSource
) {

    fun fetchProductOnRecord(): Flow<QueryDocumentSnapshot> {
        Log.d("ProductRepository", "Repository Function Called")
        return remoteSource.getAllProduct()
    }

    /**
     * fetchSingleProduct.
     * Fetch Single Product from the database using given product ID
     * @return Result<MutableMap<String, Any>?> (uses Result pattern.)
     */
    suspend fun fetchSingleProduct(productId: String): MutableMap<String, Any>? {
        return remoteSource.fetchSingleProduct(productId).data
    }

    fun fetchProductsOwnedBy(userId: String): Flow<QueryDocumentSnapshot> {
        return remoteSource.fetchProducts(userId)
    }

    suspend fun addNewProduct(newProduct: Product): Result<Boolean> {
        return remoteSource.postNewProduct(newProduct)
    }

    suspend fun fetchImageUri(filename: String): Result<Uri> {
        val uri = remoteSource.getImageUrlAsync(filename)
        if (uri == null) {
            return Result.failure(NoSuchElementException("No Uri Found."))
        } else {
            return Result.success(uri)
        }
    }

    // Subject to be refactored.
    suspend fun uploadNewImage(file: File) = CoroutineScope(Dispatchers.IO).async {
        remoteSource.uploadNewImage(file)
    }



}