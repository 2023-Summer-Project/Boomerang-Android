package com.blackbunny.boomerang.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.blackbunny.boomerang.data.ScannedBeacon
import com.blackbunny.boomerang.presentation.foregroundBeaconService.BeaconRangingService
import com.blackbunny.boomerang.presentation.foregroundBeaconService.BeaconServiceCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BeaconServiceViewModel @Inject constructor(

) : ViewModel(), BeaconServiceCallback {

    // Beacon Devices.
    private val _beaconDevices = MutableStateFlow<MutableList<ScannedBeacon>>(mutableListOf())
    val beaconDevices = _beaconDevices.asStateFlow()

    // Passing Context
    // Later use. (Beacon Implementation
    fun startBeaconRangingService(context: Context) {
        val beaconRangingServiceIntent = Intent(context, BeaconRangingService::class.java)
        beaconRangingServiceIntent.component = ComponentName(context, RegisterViewModel::class.java)
        ContextCompat.startForegroundService(context, beaconRangingServiceIntent)
    }



    override fun onBeaconRangingStarted() {
        TODO("Not yet implemented")
    }

    override fun onBeaconResultReceived() {
        TODO("Not yet implemented")
    }

    override fun onBeaconRangingStopped() {
        TODO("Not yet implemented")
    }

}