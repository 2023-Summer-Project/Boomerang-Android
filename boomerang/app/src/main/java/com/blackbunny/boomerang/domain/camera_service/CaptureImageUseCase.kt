package com.blackbunny.boomerang.domain.camera_service

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject

class CaptureImageUseCase @Inject constructor(
    // No parameter required.
){
    private fun captureImage(context: Context, imageCapture: ImageCapture, callback: OnImageSavedCallback)  {
        val filename = System.currentTimeMillis().toString()
        val file = File("${context.externalCacheDir}${File.separator}${filename}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        // Take Image from the preview.
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            callback
        )
    }



    private fun captureImage(
        context: Context,
        cameraController: LifecycleCameraController,
        callback: OnImageCapturedCallback
    ) {
        val mainExecutor = ContextCompat.getMainExecutor(context)

        cameraController.takePicture(mainExecutor, callback)
    }


    operator fun invoke(context: Context, imageCapture: ImageCapture, callback: OnImageSavedCallback) {
        captureImage(context, imageCapture, callback)
    }

    operator fun invoke(context: Context, cameraController: LifecycleCameraController, callback: OnImageCapturedCallback) {
        captureImage(context, cameraController, callback)
    }

}