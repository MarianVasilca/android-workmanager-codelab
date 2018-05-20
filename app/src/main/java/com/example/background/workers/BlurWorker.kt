package com.example.background.workers

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import com.example.background.Constants.KEY_IMAGE_URI


class BlurWorker : Worker() {

    private val TAG = BlurWorker::class.java.simpleName

    override fun doWork(): WorkerResult {
        val resourceUri = inputData.getString(KEY_IMAGE_URI, null)

        try {

            if (resourceUri.isNullOrBlank()) {
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = applicationContext.contentResolver
            // Create a bitmap
            val picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)))

            // Blur the bitmap
            val blurBitmap = WorkerUtils.blurBitmap(picture, applicationContext)

            // Write bitmap to a temp file
            val writeBitmapToFile = WorkerUtils.writeBitmapToFile(applicationContext, blurBitmap)

            WorkerUtils.makeStatusNotification(writeBitmapToFile.toString(), applicationContext)

            outputData = Data.Builder().putString(KEY_IMAGE_URI, writeBitmapToFile.toString()).build()

            // If there were no errors, return SUCCESS
            return WorkerResult.SUCCESS
        } catch (e: Throwable) {
            // Technically WorkManager will return WorkerResult.FAILURE
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "Error applying blur!", e)
            return WorkerResult.FAILURE
        }
    }

}