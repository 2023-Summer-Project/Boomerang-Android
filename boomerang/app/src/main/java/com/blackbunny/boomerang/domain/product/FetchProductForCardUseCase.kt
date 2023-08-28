package com.blackbunny.boomerang.domain.product

import android.util.Log
import com.blackbunny.boomerang.data.product.ProductRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class FetchProductForCardUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    private fun fetchProductInfoWithImage(): Flow<Product> {
        return productRepository.fetchProductOnRecord().map { document ->
            document.getObject()
        }
    }

    private fun fetchProductInfoWithId(userId: String): Flow<Product> {
        return productRepository.fetchProductsOwnedBy(userId).map {  document ->
            document.getObject()
        }
    }

    private fun QueryDocumentSnapshot.getObject(): Product {

        val images_map = this.data["IMAGES_MAP"] as Map<String, String>

        return Product(
            productId = this.id as String,
            coverImage = images_map[images_map.keys.sorted().first()] as String,
            images = images_map,
            title = this.data["POST_TITLE"] as String,
            content = this.data["POST_CONTENT"] as String,
            productName = this.data["PRODUCT_NAME"] as String,
            location = this.data["LOCATION"] as String,
            locationLatLng = LatLng(
                (this.data["LATITUDE"]).toString().toDouble(),
                (this.data["LONGITUDE"]).toString().toDouble()
            ) ?: null,
            price = this.data["PRICE"].toString(),
            ownerId = this.data["OWNER_ID"] as String,
            ownerName = this.data["OWNER_NAME"] as String,
            profileImage = this.data["PROFILE_IMAGE"] as String,
            availability = this.data["AVAILABILITY"] as Boolean,
            timestamp = this.data["TIMESTAMP"] as Timestamp,
            availableTime = this.data["AVAILABLE_TIME"] as List<String>
        )
    }

    operator fun invoke(): Flow<Product> {
        Log.d("FetchProductForCardUseCase", "Invoke Called")
        return fetchProductInfoWithImage()
    }

    operator fun invoke(userId: String): Flow<Product> {
        return fetchProductInfoWithId(userId)
    }

}

data class Product(
    val productId: String = "",
    val coverImage: String = "",
    val images: Map<String, String> = emptyMap(),
    val title: String = "",
    val content: String = "",
    val productName: String = "",
    val productType: String = "",
    val location: String = "",
    val locationLatLng: LatLng? = null,
    val price: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val profileImage: String = "",
    val availableTime: List<String> = emptyList(),
    val availability: Boolean = false,
    val timestamp: Timestamp? = null,            // Should be modified later.
    val isOwnedBySessionUser: Boolean = false
)


/*
val images = document.data["IMAGES"] as ArrayList<String>
            if (images.isNotEmpty()) {


                Product(
                    coverImage = images[0],
                    title = document.data["POST_TITLE"].toString(),
                    location = document.data["LOCATION"].toString(),
                    price = "${document.data["PRICE"].toString()} 원"
                )
            } else {
                Product(
                    coverImage = "",
                    title = document.data["POST_TITLE"].toString(),
                    location = document.data["LOCATION"].toString(),
                    price = "${document.data["PRICE"].toString()} 원"
                )
            }
 */