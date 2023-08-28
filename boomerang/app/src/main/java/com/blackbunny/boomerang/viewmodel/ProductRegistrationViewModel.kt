package com.blackbunny.boomerang.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.location.LocationRepository
import com.blackbunny.boomerang.domain.product.Product
import com.blackbunny.boomerang.domain.product.RegisterNewProductUseCase
import com.blackbunny.boomerang.presentation.dataStore
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ProductRegistrationViewModel
 */

@HiltViewModel
class ProductRegistrationViewModel @Inject constructor(
    private val registerNewProductUseCase: RegisterNewProductUseCase,
    private val locationRepository: LocationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductRegisterUiState>(ProductRegisterUiState())
    val uiState: StateFlow<ProductRegisterUiState> = _uiState.asStateFlow()

    fun registerNewProduct() {
        updateDialogVisibility(true)
        updateInteractionEnabled(false)

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("productRegistrationViewModel", "Register new product")
            // TODO: The logic below should be isolated into separate UseCase.
            context.dataStore.data.map {
                Product(
                    title = _uiState.value.titleText.text,
                    content = _uiState.value.descriptionText.text,
                    productName = _uiState.value.productNameText.text,
                    productType = _uiState.value.productTypeText.text,
                    price = _uiState.value.priceText.text,
                    location = _uiState.value.locationText.text,
                    availability = true,
                    ownerName = it[stringPreferencesKey("username")] ?: "",
                    profileImage = it[stringPreferencesKey("profile_image")] ?: "",
                    locationLatLng = _uiState.value.locationLatLng,
                    availableTime = listOf(
                        "${_uiState.value.timeFrom.text} 00분", "${_uiState.value.timeUntil.text} 00분"
                    )
                )
            }.collectLatest { newProduct->
                Log.d("ProductRegistrationViewModel", "$newProduct")
                registerNewProductUseCase(
                    newProduct,
                    _uiState.value.productImages
                ).await()
                    .fold(
                        onSuccess = {
                            if (it) {
                                // Successful upload.
                                Log.d("ProductRegistrationViewModel", "Post result: $it")
                                updateDialogVisibility(false)
                                updateAlertDialogVisibility(true)
                            } else {
                                // Failure.
                                updateDialogVisibility(false)
                                updateInteractionEnabled(true)
                            }
                        },
                        onFailure = {
                            Log.d("ProductRegistrationViewModel", "Exception thrown: ${it.message}")
                            updateDialogVisibility(false)
                            updateInteractionEnabled(true)
                        }
                    )
            }
        }
    }

    fun updateTitleText(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                titleText = value
            )
        }
    }

    fun updateDescriptionText(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                descriptionText = value
            )
        }
    }

    fun updateProductNameText(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                productNameText = value
            )
        }
    }

    fun updateProductTypeText(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                productTypeText = value
            )
        }
    }

    fun updatePriceText(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                priceText = value
            )
        }
    }

    fun updateLocationText(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                locationText = value
            )
        }
    }

    fun updateTimeFrom(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                timeFrom = value,
                timeUntil = TextFieldValue(""),
                timeUntilVisibility = true
            )
        }
        Log.d("ProductRegistrationViewModel", "is TimeFrom Blank?: ${_uiState.value.timeFrom}")
    }

    fun updateTimeUntil(value: TextFieldValue) {
        _uiState.update {
            it.copy(
                timeUntil = value
            )
        }
    }

    fun updateCameraVisibility(value: Boolean) {
        _uiState.update {
            it.copy(
                cameraVisibility = value
            )
        }
    }

    fun updateMapVisibility(value: Boolean) {
        _uiState.update {
            it.copy(
                mapVisibility = value
            )
        }
    }

    fun updateDialogVisibility(value: Boolean) {
        _uiState.update {
            it.copy(
                dialogVisibility = value
            )
        }
    }

    fun updateAlertDialogVisibility(value: Boolean) {
        _uiState.update {
            it.copy(
                alertDialogVisibility = value
            )
        }
    }

    fun updateInteractionEnabled(value: Boolean) {
        _uiState.update {
            it.copy(
                interactionEnabled = value
            )
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    locationLatLng = LatLng(lat, lng)
                )
            }
        }
    }

    /* TODO: Subject to be removed after refactoring the code. */
    fun updateSelectionLimit(value: Int) {
        _uiState.update{
            it.copy(
                selectionLimit = value
            )
        }
    }


    fun selectLocation() {
        Log.d("ProductRegistrationViewModel", "Selected Location: ${_uiState.value.locationLatLng}")
        getStringAddress(_uiState.value.locationLatLng)
        updateMapVisibility(false)
    }

    fun addNewPhoto(photo: File) {
        _uiState.update {
            it.copy(
                productImages = _uiState.value.productImages.notifyProductImageSourceChanged(photo)
            )
        }

        Log.d("ProductRegistrationViewModel", "New Photo Added: ${photo.toString()}\n Current Size: ${_uiState.value.productImages.size}")
    }

    fun removePhoto(photo: File) {
        val temp = mutableListOf<File>()

        for (file in _uiState.value.productImages) {
            if (file != photo) temp.add(file)
        }

        _uiState.update {
            it.copy(
                productImages = temp.toList()
            )
        }
    }


    private fun List<File>.notifyProductImageSourceChanged(photo: File): List<File> {
        val updated = List<File>(this.size + 1) {
            if (it < this.size) this[it]
            else photo
        }

        return updated
    }

    /**
     * Fetch Current Location, and update the value.
     * TODO: Should be isolated to separate UseCase.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        viewModelScope.launch {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // At this point, permission is already granted.
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            Log.d("ProductRegistrationViewModel", "Lat: ${lastKnownLocation?.latitude} ${lastKnownLocation?.longitude}")

            if (lastKnownLocation != null) {
                _uiState.update {
                    it.copy(
                        locationLatLng = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                    )
                }
            }
        }
    }

    /**
     * Fetch Current Location, and update the value.
     * TODO: Should be isolated to separate UseCase. (+ remove context.)
     */
    @SuppressLint("MissingPermission")
    fun getStringAddress(coordinate: LatLng) {
        viewModelScope.launch {
            if (coordinate != null) {
                async {
                    locationRepository.fetchCurrentLocation(
                        coordinate.latitude,
                        coordinate.longitude
                    )
                }.await()
                    .fold(
                        onSuccess = {
                            if (it.thoroughfare == null) {
                                updateLocationText(
                                    TextFieldValue("${it.featureName}")
                                )
                            } else {
                                if (it.locality == null) {
                                    // 광역시/특별시
                                    updateLocationText(
                                        TextFieldValue("${it.adminArea} ${it.thoroughfare}")
                                    )
                                } else {
                                    updateLocationText(
                                        TextFieldValue("${it.locality} ${it.thoroughfare}")
                                    )
                                }
                            }
                        },
                        onFailure = {
                            Log.d("ProductRegistrationViewModel", "Unable to fetch current location\n${it.message}")
                            updateLocationText(TextFieldValue("Unavailable."))
                        }
                    )


            }
        }
    }

}

data class ProductRegisterUiState(
    val cameraVisibility: Boolean = false,
    val mapVisibility: Boolean = false,
    val dialogVisibility: Boolean = false,
    val alertDialogVisibility: Boolean = false,
    val titleText: TextFieldValue = TextFieldValue(),
    val descriptionText: TextFieldValue = TextFieldValue(),
    val productNameText: TextFieldValue = TextFieldValue(),
    val productTypeText: TextFieldValue = TextFieldValue(),
    val priceText: TextFieldValue = TextFieldValue(),
    val locationText: TextFieldValue = TextFieldValue("위치 선택하기"),
    val productImages: List<File> = emptyList(),
    val interactionEnabled: Boolean = true,
    val locationLatLng: LatLng = LatLng(37.5601, 126.9960),
    val timeFrom: TextFieldValue = TextFieldValue(),
    val timeUntil: TextFieldValue = TextFieldValue(),
    // Temporary Variable. Should be removed after a better solution applied for timezone selection
    val timeUntilVisibility: Boolean = false,
    val selectionLimit: Int = -1,
    val timeSelection: List<String> = listOf<String>(
        "오전 12시",
        "오전 1시",
        "오전 2시",
        "오전 3시",
        "오전 4시",
        "오전 5시",
        "오전 6시",
        "오전 7시",
        "오전 8시",
        "오전 9시",
        "오전 10시",
        "오전 11시",
        "오후 12시",
        "오후 1시",
        "오후 2시",
        "오후 3시",
        "오후 4시",
        "오후 5시",
        "오후 6시",
        "오후 7시",
        "오후 8시",
        "오후 9시",
        "오후 10시",
        "오후 11시"
    )
)