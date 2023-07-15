package com.blackbunny.boomerang.presentation.foregroundBeaconService

/**
 * BeaconServiceCallback
 * Callback function for establishing contract between Foreground Service and the rest of application.
 */

interface BeaconServiceCallback {

    fun onBeaconRangingStarted()

    fun onBeaconResultReceived()

    fun onBeaconRangingStopped()

}

