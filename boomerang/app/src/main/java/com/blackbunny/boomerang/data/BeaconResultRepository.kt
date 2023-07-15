package com.blackbunny.boomerang.data

import com.blackbunny.boomerang.presentation.foregroundBeaconService.BeaconRangingService
import org.altbeacon.beacon.Beacon

import javax.inject.Inject

/**
 * BeaconResultRepository
 * Repository for Beacon Result retrieved from BeaconRangingService.
 */
class BeaconResultRepository @Inject constructor(
    private val mBeaconService: BeaconRangingService
) {
//    fun scanNearbyBeacons(): Flow<ScannedBeacon> {
//
//    }
}

data class ScannedBeacon(
    val beacon: Beacon,
    val distance: Double
)