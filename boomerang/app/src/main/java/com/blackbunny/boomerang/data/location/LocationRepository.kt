package com.blackbunny.boomerang.data.location

import android.location.Address
import android.util.Log
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val remoteSource: LocationRemoteSource
) {
    private val TAG = "LocationRepository"

    suspend fun fetchCurrentLocation(lat: Double, long: Double): Result<Address> {
        remoteSource.fetchCurrentLocation(lat, long)
            .also {
                if (it != null) {
                    Log.d(TAG, "retrieve location: ${it.thoroughfare}")
                    return Result.success(it)
                } else {
                    Log.d(TAG, "Unable to fetch location")

                    return Result.failure(Exception("Unable to decode current location coordinates."))
                }
            }
    }

    fun _fetchCurrentLocation(lat: Double, long: Double): Result<Address> {
        remoteSource._fetchCurrentLocation(lat, long)
            .also {
                if (it != null) {
                    Log.d(TAG, "retrieve location: ${it.thoroughfare}")
                    return Result.success(it)
                } else {
                    Log.d(TAG, "Unable to fetch location")

                    return Result.failure(Exception("Unable to decode current location coordinates."))
                }
            }
    }

    fun fetchCurrentCoordinates(address: String): Result<Address> {
        remoteSource.fetchCurrentCoordinates(address)
            .also {
                if (it != null) {
                    Log.d(TAG, "Successfully retrieved Address $it")
                    return Result.success(it)
                } else {
                    Log.d(TAG, "Unable to fetch Address")
                    return Result.failure(Exception("Unable to decode current location to coordinates"))
                }
            }
    }

}