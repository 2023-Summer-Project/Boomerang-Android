package com.blackbunny.boomerang.presentation.screen.main

import android.Manifest
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.presentation.component.AnimatedAlertDialog
import com.blackbunny.boomerang.presentation.component.ButtonSolid
import com.blackbunny.boomerang.presentation.component.CameraScreen
import com.blackbunny.boomerang.presentation.component.CircularProgressDialog
import com.blackbunny.boomerang.presentation.component.DropdownSelection
import com.blackbunny.boomerang.presentation.component.DropdownTimePeriodPicker
import com.blackbunny.boomerang.presentation.component.ImagePreviewWithDeleteButton
import com.blackbunny.boomerang.presentation.component.TextFieldOutlined
import com.blackbunny.boomerang.viewmodel.ProductRegistrationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Product Registration
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProductRegistrationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    // Handle location permission.
    // Required permission: android.permission.ACCESS_FINE_LOCATION and android.permission.ACCESS_COARSE_LOCATION
    // android.permission.ACCESS_COARSE_LOCATION is required when the external location provider is
    // being used for fetching location.
    // android.permission.ACCESS_FINE_LOCATION provides permission to both (COARSE_LOCATION and itself)
    // providers.

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted && !locationPermissionState.status.shouldShowRationale) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    if (locationPermissionState.status.isGranted) {
        ProductRegistration(navController = navController)
    } else {
        // DO NOTHING CURRENTLY
    }

}

@Composable
@OptIn(androidx.camera.core.ExperimentalGetImage::class)
fun ProductRegistration(
    modifier: Modifier = Modifier,
    viewModel: ProductRegistrationViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState = viewModel.uiState.collectAsState()
    val localContext = LocalContext.current
    val localCoroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.getCurrentLocation(localContext)
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.title_text_add_product),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "상품 정보 입력",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 20.dp)
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp))

            TextFieldOutlined(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                value = uiState.value.titleText,
                label = R.string.product_text_title,
                lines = 1,
                enabled = uiState.value.interactionEnabled
            ) {
                viewModel.updateTitleText(it)
            }

            TextFieldOutlined(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                value = uiState.value.productNameText,
                label = R.string.product_name,
                lines = 1,
                enabled = uiState.value.interactionEnabled
            ) {
                viewModel.updateProductNameText(it)
            }

            TextFieldOutlined(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .height(150.dp),
                value = uiState.value.descriptionText,
                label = R.string.product_text_description,
                lines = 10,
                enabled = uiState.value.interactionEnabled
            ) {
                viewModel.updateDescriptionText(it)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                TextFieldOutlined(
                    modifier = Modifier.weight(1f),
                    value = uiState.value.priceText,
                    label = R.string.text_price,
                    lines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = uiState.value.interactionEnabled
                ) {
                    viewModel.updatePriceText(it)
                }

                TextFieldOutlined(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.updateMapVisibility(true)
                        },
                    value = uiState.value.locationText,
                    label = R.string.text_location,
                    lines = 1,
                    enabled = false
                ) {
                    viewModel.updateLocationText(it)
                }
            }

            /* Preferred timezone selection */
            Text(
                text = "거래 희망 시간",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 20.dp)
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp))

            DropdownTimePeriodPicker(
                labelFrom = R.string.text_field_hint_from,
                labelUntil = R.string.text_field_hint_until,
                suggestions = uiState.value.timeSelection,
                onTimeFromSelected = { item ->
                    viewModel.updateTimeFrom(TextFieldValue(item))
                },
                onTimeUntilSelected = { item ->
                    viewModel.updateTimeUntil(TextFieldValue(item))
                }
            )

            Text(
                text = "삼품 사진 등록하기",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 20.dp)
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                //item
                items(uiState.value.productImages) {
                    ImagePreviewWithDeleteButton(
                        image = it,
                        enabled = uiState.value.interactionEnabled
                    ) {
                        viewModel.removePhoto(it)
                    }
                }

                item {
                    Surface(
                        Modifier
                            .wrapContentSize()
                            .background(Color.Transparent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .size(100.dp, 100.dp),
                            onClick = { viewModel.updateCameraVisibility(true) },
                            enabled = uiState.value.productImages.size < 5 && uiState.value.interactionEnabled
                        ) {
                            Column(
                                Modifier.wrapContentSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Filled.Add, null)
                                Text(stringResource(R.string.button_text_add_photo))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            ButtonSolid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                buttonText = stringResource(R.string.button_text_register_product),
                enabled =
                    uiState.value.titleText.text.isNotBlank() &&
                    uiState.value.descriptionText.text.isNotBlank() &&
                    uiState.value.productNameText.text.isNotBlank() &&
                    uiState.value.priceText.text.isNotBlank() &&
                    uiState.value.locationText.text.isNotBlank() &&
                    uiState.value.timeFrom.text.isNotBlank() &&
                    uiState.value.timeUntil.text.isNotBlank()
            ) {
                // TODO: Block button operation if any of the required input value is empty.
                viewModel.registerNewProduct()
            }

        }

        // Camera
        AnimatedVisibility(
            visible = uiState.value.cameraVisibility,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CameraScreen(
                onCameraCanceled = {
                    viewModel.updateCameraVisibility(false)
                }
            ) { image ->
                if (image != null) {
                    viewModel.addNewPhoto(image)
                    viewModel.updateCameraVisibility(false)
                }
            }
        }

        // Location Selection
        AnimatedVisibility(
            visible = uiState.value.mapVisibility,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally()
        ) {
            val mapView = MapView(localContext).also {
                it.getMapAsync { naverMap ->
                    val marker = Marker().apply {
                        this.position = uiState.value.locationLatLng
                        this.map = naverMap
                    }
                    naverMap.moveCamera(CameraUpdate.scrollTo(uiState.value.locationLatLng))

                    naverMap.addOnCameraChangeListener { i, b ->
                        val position = naverMap.cameraPosition
                        marker.position = position.target
                        viewModel.updateLocation(position.target.latitude, position.target.longitude)
                    }

                    // Naver map UI settings
                    naverMap.uiSettings.apply {
                        this.isZoomControlEnabled = false
                        this.isTiltGesturesEnabled = false
                        this.isLogoClickEnabled = false
                        this.logoGravity = 0
                    }
                }
            }

            // Lifecycle
            val lifecycleObserver = remember {
                LifecycleEventObserver { source, event ->
                    localCoroutineScope.launch {
                        when(event) {
                            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                            Lifecycle.Event.ON_START -> mapView.onStart()
                            Lifecycle.Event.ON_RESUME -> mapView.onResume()
                            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                            Lifecycle.Event.ON_STOP -> mapView.onStop()
                            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                            else -> { /* DO NOTHING */ }
                        }
                    }
                }
            }

            // Bind/unbind event observer.
            DisposableEffect(Unit) {
                lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                }
            }

            // Box
            Box(
                Modifier.fillMaxSize()
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    factory = { mapView }
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Transparent)
                        .padding(15.dp)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(25.dp),
                    onClick = {
                        viewModel.selectLocation()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.text_select_location),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(15.dp)
                    )
                }
            }

        }




        // Uploading Dialog
        AnimatedVisibility(
            visible = uiState.value.dialogVisibility,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            CircularProgressDialog(
                titleText = stringResource(R.string.text_upload_product),
                contentText = stringResource(R.string.text_support_upload_product)
            ) {
                
            }
        }

        // Upload Completion Alert
        AnimatedAlertDialog(
            text = stringResource(R.string.text_successful_product_upload),
            buttonText = stringResource(R.string.btn_text_confirm),
            iconImageSource = Icons.Filled.CheckCircle,
            dialogVisibility = uiState.value.alertDialogVisibility
        ) {
            viewModel.updateAlertDialogVisibility(false)
            viewModel.updateInteractionEnabled(true)
            navController.popBackStack()
        }

    }

}


@Preview
@Composable
fun ProductRegistration_Preview() {
    val navController = rememberNavController()
    val viewModel: ProductRegistrationViewModel = hiltViewModel()

    ProductRegistration(navController = navController, viewModel = viewModel)
}