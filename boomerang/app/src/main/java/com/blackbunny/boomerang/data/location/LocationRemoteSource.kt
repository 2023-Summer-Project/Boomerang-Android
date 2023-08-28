package com.blackbunny.boomerang.data.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationRemoteSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun fetchCurrentLocation(lat: Double, long: Double) = suspendCoroutine<Address?> { continuation ->
        // TODO: Geocoder.GeocodeListener causes RuntimeException (java.lang.NoClassDefFoundError: Failed resolution of: Landroid/location/Geocoder$GeocodeListener;)

        /*
                val geocoderListener = Geocoder.GeocodeListener { result ->
            if (result.isNotEmpty()) {
                continuation.resume(result[0])
            } else {
                continuation.resume(null)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Geocoder(context).getFromLocation(lat, long, 1, geocoderListener)
        } else {
            val result = Geocoder(context).getFromLocation(lat, long, 1).also {
                if (!it.isNullOrEmpty()) {
                    continuation.resume(it[0])
                } else {
                    continuation.resume(null)
                }
            }
        }
         */

        val result = Geocoder(context).getFromLocation(lat, long, 1).also {
            if (!it.isNullOrEmpty()) {
                continuation.resume(it[0])
            } else {
                continuation.resume(null)
            }
        }
    }

    /**
     * fetchCurrentLocation
     * @param Long type of coordinate values.
     * @return instance of type Address based on the given coordinates
     * TODO: replace deprecated method.
     */
    fun _fetchCurrentLocation(lat: Double, long: Double): Address? {
        Geocoder(context).getFromLocation(lat, long, 1).also {
            if (!it.isNullOrEmpty()) {
                return it[0]
            } else {
                return null
            }
        }
    }

    /**
     * fetchCurrentCoordinates
     * @param String value of the address.
     * @return instance of type Address based on the given address.
     * TODO: replace deprecated method.
     */
    fun fetchCurrentCoordinates(address: String): Address? {
        Geocoder(context).getFromLocationName(address, 1).also {
            if (!it.isNullOrEmpty()) {
                return it[0]
            } else {
                return null
            }
        }
    }


}