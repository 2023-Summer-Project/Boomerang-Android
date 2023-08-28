package com.blackbunny.boomerang.domain.location

import android.util.Log
import com.blackbunny.boomerang.data.location.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


// TODO: Isolate Detailed business logic in RegisterNewProductViewModel.
class DecodeCoordinateToLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    fun getCityAndCounty(lat: Double, long: Double) {
        // Return Object within Result pattern.
    }

    suspend fun getParsedLocation(lat: Double, long: Double) = suspendCoroutine<String> { continuation ->
        repository._fetchCurrentLocation(lat, long)
            .fold(
                onSuccess = {
                    if (it.locality == null) {
                        // 광역시/특별시
                        continuation.resume("${it.adminArea} ${it.thoroughfare}")
                    } else {
                        continuation.resume("${it.locality} ${it.thoroughfare}")
                    }
                },
                onFailure = {
                    Log.d("DecodeCoordinateToLocationUseCase", "Unable to fetch current location\n${it.message}")
                    continuation.resume("Unavaliable")
                }
            )
    }
}