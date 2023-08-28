package com.blackbunny.boomerang.domain.product

import com.blackbunny.boomerang.data.product.ProductRepository
import com.google.firebase.Timestamp
import com.naver.maps.geometry.LatLng
import javax.inject.Inject

/**
 * RequestProductDetailUseCase
 * Request product detail screen from Chatroom.
 */
class RequestProductDetailUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend fun requestProductDetail(chatId: String): Result<Product> {
        val productId = chatId.split("_")[2]        // Chat ID scheme: userId1_userId2_productID

        return with(repository.fetchSingleProduct(productId)) {
            if (this != null) {
                val images = this["IMAGES_MAP"] as Map<String, String>

                val result = Product(
                    productId = productId,
                    coverImage = images[images.keys.sorted().first()] as String,
                    images = images,
                    title = this["POST_TITLE"] as String,
                    content = this["POST_CONTENT"] as String,
                    productName = this["PRODUCT_NAME"] as String,
                    productType = this["PRODUCT_TYPE"] as String,
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
                Result.success(result)
            } else {
                Result.failure(NoSuchElementException("Unable to find product"))
            }
        }

    }

    /*
    productId = this.id as String,
            coverImage = images_map[images_map.keys.sorted().first()] as String,
            images = images_map,
            title = this.data["POST_TITLE"] as String,
            content = this.data["POST_CONTENT"] as String,
            productName = this.data["PRODUCT_NAME"] as String,
            productType = this.data["PRODUCT_TYPE"] as String,
            location = this.data["LOCATION"] as String,
            price = this.data["PRICE"].toString(),
            ownerId = this.data["OWNER_ID"] as String,
            availability = this.data["AVAILABILITY"] as Boolean,
            timestamp = this.data["TIMESTAMP"] as Timestamp
     */

    /*
    data class Product(
        val coverImage: String = "",
        val images: Map<String, String> = emptyMap(),
        val title: String = "",
        val content: String = "",
        val productName: String = "",
        val productType: String = "",
        val location: String = "",
        val price: String = "",
        val ownerId: String = "",
        val availability: Boolean = false
    )
     */



    operator fun invoke(chatId: String) {

    }

}