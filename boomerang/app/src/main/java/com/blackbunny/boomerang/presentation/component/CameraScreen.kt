package com.blackbunny.boomerang.presentation.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.viewmodel.CameraServiceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Reference:
 *  https://medium.com/tech-takeaways/how-to-use-camerax-with-android-jetpack-compose-38a236e209a3
 *  https://github.com/YanneckReiss/JetpackComposeCameraXShowcase
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onCameraCanceled: () -> Unit,
    onCameraTaken: (File) -> Unit
) {
    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val storagePermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val storageReadPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    LaunchedEffect(key1 = Unit) {
        if (!cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale) {
            cameraPermissionState.launchPermissionRequest()
        }

        if (!storagePermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale) {
            storagePermissionState.launchPermissionRequest()
        }

        if (!storageReadPermissionState.status.isGranted && !storageReadPermissionState.status.shouldShowRationale) {
            storageReadPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted && storagePermissionState.status.isGranted && storageReadPermissionState.status.isGranted) {
        CameraPreviewView(onCameraCancelClicked = onCameraCanceled, onPhotoCaptured = onCameraTaken)
    } else {
        onCameraCanceled()
    }

}

@Composable
fun CameraPreviewView(
    viewModel: CameraServiceViewModel = hiltViewModel(),
    onCameraCancelClicked: () -> Unit,
    onPhotoCaptured: (File) -> Unit
) {

    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
//    val cameraController: LifecycleCameraController = remember { LifecycleCameraController(localContext) }


    val preview = androidx.camera.core.Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder().build()

    val previewView = remember { PreviewView(localContext) }
    val imageCapture = remember {
        ImageCapture.Builder().setTargetResolution(Size(700, 700)).build()
    }

    val systemUiController = rememberSystemUiController()

    LaunchedEffect(key1 = Unit) {
        // System UI controller (Hide system navigation bar when user enter the camera view (immerse mode.)
        // Google Accompanist SystemUiController Library.
        systemUiController.isNavigationBarVisible = false
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(localContext)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            preview.setSurfaceProvider(previewView.surfaceProvider)

            // Unibind all pre-existing CameraProvider.
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch(e: Exception) {
                Log.d("CameraScreen", "Exception thrown:\n${e.message}")
            }
        }, ContextCompat.getMainExecutor(localContext))
    }

    // Side-Effect handling when composable is dismissed.
    DisposableEffect(Unit) {
        onDispose {
            systemUiController.isNavigationBarVisible = true
            systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AndroidView(
            { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Camera Guideline
        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center)
        ) {
            Text(
                text = stringResource(R.string.camera_guide_info),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Red
            )
            Box(
                modifier = Modifier
                    .size((LocalConfiguration.current.screenWidthDp * 0.7).dp)
                    .clip(RectangleShape)
                    .background(Color.Transparent)
                    .border(2.dp, Color.Red)
            )
        }


        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 15.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Transparent),
            onClick =  {
                viewModel.takePhoto(localContext, imageCapture, object: OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.d("CameraScreen", "Photo Saved: ${outputFileResults.savedUri!!.path}")
                        onPhotoCaptured(File(outputFileResults.savedUri!!.path))
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.d("CameraScreen", "Exception thrown\n${exception.message}")
                    }

                })
            },
            colors = ButtonDefaults.buttonColors(Color.LightGray)
        ) {
            Icon(
                painterResource(R.drawable.baseline_camera_24),
                null,
                Modifier.padding(start = 20.dp, end = 20.dp)
            )
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 15.dp, start = 15.dp),
            onClick = { onCameraCancelClicked() }
        ) {
            Icon(Icons.Filled.ArrowBack, null)
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine {
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            it.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

private fun imageToBitmap(image: Image) = CoroutineScope(Dispatchers.IO).async {
    val byteBuffer = image.planes[0].buffer
    val bytes = ByteArray(byteBuffer.capacity())
    byteBuffer.get(bytes)

    val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val matrix = Matrix()
    matrix.postRotate(90f)

    Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
}

@Preview
@Composable
fun CameraPreviewView_Preview() {

    CameraPreviewView(onCameraCancelClicked = { /*TODO*/ }) {

    }

}


/* Previous attempt (Take Photo)

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {context ->
                PreviewView(context).apply {
                    setBackgroundColor(Color.White.toArgb())
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    scaleType = PreviewView.ScaleType.FILL_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }.also {previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
//                previewView
            },
            onRelease = {
                Log.d("CameraPreviewView", "Release CameraControl")
                cameraController.unbind()

            }
        )

viewModel.takePhoto(localContext, cameraController, object: OnImageCapturedCallback() {
//                    @SuppressLint("UnsafeOptInUsageError")
//                    override fun onCaptureSuccess(image: ImageProxy) {
//                        Log.d("CameraScreen", "${image.imageInfo!!}\n${image.image!!.timestamp}")
//                        Log.d("CameraScreen", "${image.image!!.planes[0].buffer}")
////                        if (image.image != null) onPhotoCaptured(image.image!!.toBitmap())
//                        localCoroutineScope.launch {
//                            cameraController.unbind()
////                            if (image.image != null) onPhotoCaptured(imageToBitmap(image.image!!).await())
//                            if (image.image != null){
//                                viewModel.imageToFile(image.image!!).await() ?.let { onPhotoCaptured(it) }
//                            }
//                        }
//                    }
//                })

 */