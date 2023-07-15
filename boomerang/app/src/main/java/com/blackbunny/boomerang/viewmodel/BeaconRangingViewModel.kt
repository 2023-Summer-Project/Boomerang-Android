package com.blackbunny.boomerang.viewmodel

import androidx.lifecycle.ViewModel
import com.blackbunny.boomerang.data.ScannedBeacon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BeaconRangingViewModel @Inject constructor(

) : ViewModel() {

    private val _detectedDevices = MutableStateFlow<MutableList<ScannedBeacon>>(mutableListOf())
    val detectedDevices = _detectedDevices.asStateFlow()

    fun getDetectedDevices() {

    }

}