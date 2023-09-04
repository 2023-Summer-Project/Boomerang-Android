package com.blackbunny.boomerang.data.product

import android.net.Uri
import android.util.Log
import com.blackbunny.boomerang.domain.product.Product
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    /**
     * RemoveProduct
     */
    suspend fun removeProduct(product: Product): Boolean {
        return remoteSource.removeProduct(product)
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


    /**
     * searchProductByKeyword
     * @param keyword String type keyword for product search.
     * TODO: 1. Process of converting raw data (DocumentSnapshot) 2. Refactor Remote source to fetch only required data.
     */
    fun searchProductByKeyword(keyword: String) = remoteSource.searchProductByKeyword(keyword)
        .map { documentSnapshot ->
            documentSnapshot.getObject()
        }

    private fun DocumentSnapshot.getObject(): Product {

        val imagesMap = this["IMAGES_MAP"] as Map<String, String>

        return Product(
            productId = this.id as String,
            coverImage = imagesMap[imagesMap.keys.sorted().first()] as String,
            images = imagesMap,
            title = this["POST_TITLE"] as String,
            content = this["POST_CONTENT"] as String,
            productName = this["PRODUCT_NAME"] as String,
            location = this["LOCATION"] as String,
            locationLatLng = LatLng(
                (this["LATITUDE"]).toString().toDouble(),
                (this["LONGITUDE"]).toString().toDouble()
            ) ?: null,
            price = this["PRICE"].toString(),
            ownerId = this["OWNER_ID"] as String,
            ownerName = this["OWNER_NAME"] as String,
            profileImage = this["PROFILE_IMAGE"] as String,
            availability = this["AVAILABILITY"] as Boolean,
            timestamp = this["TIMESTAMP"] as Timestamp,
            availableTime = this["AVAILABLE_TIME"] as List<String>
        )
    }
}