package com.blackbunny.boomerang.viewmodel

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.domain.camera_service.CaptureImageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CameraServiceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val captureImage: CaptureImageUseCase
) : ViewModel() {

    // Capture Image
    fun takePhoto(context: Context, imageCapture: ImageCapture, callback: OnImageSavedCallback) {
        captureImage(context, imageCapture, callback)
    }

    fun takePhoto(context: Context, controller: LifecycleCameraController, callback: OnImageCapturedCallback) {
        captureImage(context, controller, callback)
    }

    fun imageToFile(image: Image) = viewModelScope.async {
        val byteBuffer = image.planes[0].buffer
        val bytes = ByteArray(byteBuffer.capacity())    // Bytes
        byteBuffer.get(bytes)

        // Initialize a new file instance to save Bitmap object.
        var file = ContextWrapper(context).getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${System.currentTimeMillis()}.jpeg")

//        file.writeBytes(bytes)

        try {
            val stream = FileOutputStream(file)
            stream.write(bytes)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            Log.d("CameraServiceViewModel", "IOException thrown while creating a file.\n\t${e.message}")
        } finally {
            image.close()       // Close image source for preventing memory leak.
        }


        file ?: null        // Return file asynchronously
    }

}

/*
// Manage Bitmap and its memory consumption.
        // Reference: https://developer.android.com/topic/performance/graphics/manage-memory

        val decodeOption = BitmapFactory.Options()

        val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // If we're running on Hnoeycomb or newer, try to use inBitmap


        val matrix = Matrix()
        matrix.postRotate(90f)

        Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
 */