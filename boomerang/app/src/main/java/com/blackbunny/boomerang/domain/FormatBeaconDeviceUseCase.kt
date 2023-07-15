package com.blackbunny.boomerang.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.altbeacon.beacon.Beacon
import javax.inject.Inject
import kotlin.math.pow

class FormatBeaconDeviceUseCase @Inject constructor(

) {

    private fun fetchNearbyBeaconList(): Flow<Beacon> = flowOf(

    )

//    operator fun invoke() : Flow<ScannedBeacon> {
//        return fetchNearbyBeaconList().map {
//
//        }
//    }

    private fun calculateDistance(device: Beacon): Double {
        if (device.rssi == 0) {
            return -1.0     // Cannot caluclate distance.
        }

        val ratio = device.rssi * 1.0 / device.txPower
        if (ratio < 1.0) {
            return ratio.pow(10)
        } else {
            val distance = 0.89976 * ratio.pow(7.7095) + 0.111
            return distance
        }
    }
}

