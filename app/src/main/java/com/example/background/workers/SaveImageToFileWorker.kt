package com.example.background.workers

import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import com.example.background.Constants
import java.text.SimpleDateFormat
import java.util.*

/**
 * SaveImageToFileWorker has and input and an output. The input is a String stored with
 * the key KEY_IMAGE_URI. And the output will also be a String stored with the key KEY_IMAGE_URI.
 */
class SaveImageToFileWorker : Worker() {

    private val TAG = SaveImageToFileWorker::class.java.simpleName
    private val title = "Blurred Image"
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z",
            Locale.getDefault())

    override fun doWork(): WorkerResult {

        val resolver = applicationContext.contentResolver
        val resourceUri = inputData.getString(Constants.KEY_IMAGE_URI, null)

        try {
            if (resourceUri.isNullOrBlank()) {
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }
            val bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)))
            val imageUrl = MediaStore.Images.Media.insertImage(
                    resolver, bitmap, title, dateFormat.format(Date()))
            if (imageUrl.isNullOrBlank()) {
                Log.e(TAG, "Writing to MediaStore failed")
                return WorkerResult.FAILURE
            }

            outputData = Data.Builder().putString(Constants.KEY_IMAGE_URI, imageUrl).build()
            
            return WorkerResult.SUCCESS
        } catch (t: Throwable) {
            return WorkerResult.FAILURE
        }
    }

}