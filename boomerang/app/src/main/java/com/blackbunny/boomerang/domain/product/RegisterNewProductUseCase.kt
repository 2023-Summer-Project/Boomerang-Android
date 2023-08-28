package com.blackbunny.boomerang.domain.product

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import com.blackbunny.boomerang.data.product.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class RegisterNewProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    val localCoroutineScope = CoroutineScope(Dispatchers.IO)
    private fun registerNewProduct(newProduct: Product, productImages: List<File>) {
        localCoroutineScope.launch {
            val map = uploadImages(productImages)

            // Log (Subject to be removed.)
            map.forEach {
                Log.d("RegisterNewProductUseCase", "${it.key}, ${it.value}")
            }
            Log.d("RegisterNewProductUseCase", "Product Info: ${newProduct.toString()}")

            // update new product.
            newProduct.copy(
                coverImage = map.keys.first(),
                images = map,
                availability = true
            )

            // Post request.
            repository.addNewProduct(newProduct)

        }
    }

    private fun registerNewProductAsync(newProduct: Product, productImages: List<File>) = localCoroutineScope.async {
        // Post request.
        repository.addNewProduct(newProduct.copy(
            images = uploadImagesAsync(productImages).await()
        ))

    }

    // Upload new image to Firebase Cloud Storage could be related to the 'UseCase' register new product.
    private suspend fun uploadImages(images: List<File>): Map<String, String> {
        var result = mutableMapOf<String, String>().also { map ->
            localCoroutineScope.async {
                images.map { repository.uploadNewImage(it) }.awaitAll()
                    .forEach {
                        if (it != null) map[it.first] = it.second
                    }
            }.await()
        }

        return result
    }

    private fun uploadImagesAsync(images: List<File>) = localCoroutineScope.async {
        mutableMapOf<String, String>().also { map ->
            images.map { repository.uploadNewImage(it) }.awaitAll()
                .forEach {
                    if (it != null) map[it.first] = it.second
                }
        }
    }

    /**
     * Use Coroutine Async
     */
    operator fun invoke(newProduct: Product, productImages: List<File>): Deferred<Result<Boolean>> {
        return registerNewProductAsync(newProduct, productImages)
    }
}