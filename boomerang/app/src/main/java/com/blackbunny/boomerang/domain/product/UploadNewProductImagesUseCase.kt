package com.blackbunny.boomerang.domain.product

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class UploadNewProductImagesUseCase @Inject constructor(

) {

    private fun uploadNewProductPhoto(bitmap: Bitmap)= CoroutineScope(Dispatchers.IO).async {

    }


//    operator fun invoke(bitmap: Bitmap): Deferred<Pair<String, String>> {
//        uploadNewProductPhoto(bitmap)
//    }

}