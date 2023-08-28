package com.blackbunny.boomerang.domain.location

import android.util.Log
import com.blackbunny.boomerang.data.location.LocationRepository
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DecodeLocationToCoordinatesUseCase @Inject constructor(
    private val repository: LocationRepository
) {

    private val TAG = "DecodeLocationToCoordinatesUseCase"

    suspend fun decodeLocationToCoordinates(address: String) = suspendCoroutine {  continuation ->
        CoroutineScope(Dispatchers.Default).launch {
            if (address.isNotBlank()) {
                repository.fetchCurrentCoordinates(address)
                    .fold(
                        onSuccess = {
                            Log.d(TAG, "Decode Successful")
                            continuation.resume(
                                LatLng(it.latitude, it.longitude)       // Naver maps geometry class.
                            )
                        },
                        onFailure = {
                            Log.e(TAG, "Unable to decode the given address")
                            it.printStackTrace()
                        }
                    )
            }
        }
    }

}