/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import android.text.TextUtils
import androidx.work.*
import com.example.background.Constants.IMAGE_MANIPULATION_WORK_NAME
import com.example.background.Constants.KEY_IMAGE_URI
import com.example.background.Constants.TAG_OUTPUT
import com.example.background.workers.BlurWorker
import com.example.background.workers.CleanupWorker
import com.example.background.workers.SaveImageToFileWorker


class BlurViewModel// BlurViewModel constructor
    : ViewModel() {

    private var workManager: WorkManager = WorkManager.getInstance()
    // This transformation makes sure that whenever the current work Id changes the WorkStatus
    // the UI is listening to changes
    private var savedWorkStatus: LiveData<List<WorkStatus>> = workManager.getStatusesByTag(TAG_OUTPUT)

    /**
     * Getters
     */
    internal var imageUri: Uri? = null
        private set
    internal var outputUri: Uri? = null
        private set

    fun getOutputStatus(): LiveData<List<WorkStatus>> {
        return savedWorkStatus
    }

    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     * @param blurLevel The amount to blur the image
     */
    internal fun applyBlur(blurLevel: Int) {
        // Add WorkRequest to Cleanup temporary images
        var continuation =
                workManager.beginUniqueWork(IMAGE_MANIPULATION_WORK_NAME,
                        // if the user decides to blur another image before the current one is finished,
                        // we want to stop the current one and start blurring the new image.
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(CleanupWorker::class.java)
                )

        // Add WorkRequests to blur the image the number of times requested
        for (i in 1..blurLevel) {
            val blurRequestBuilder =
                    OneTimeWorkRequest.Builder(BlurWorker::class.java)

            // Input the Uri if this is the first blur operation
            // After the first blur operation the input will be the output of previous
            // blur operations.
            if (i == 1) {
                blurRequestBuilder.setInputData(createInputDataForUri())
            }

            continuation = continuation.then(blurRequestBuilder.build())
        }
        // Add WorkRequest to save the image to the filesystem
        val saveRequest =
                OneTimeWorkRequest.Builder(SaveImageToFileWorker::class.java)
                        .addTag(TAG_OUTPUT)
                        .build()
        continuation = continuation.then(saveRequest)

        // Actually start the work
        continuation.enqueue()
    }

    /**
     * Creates the input data bundle which includes the Uri to operate on
     * @return Data which contains the Image Uri as a String
     */
    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        builder.putString(KEY_IMAGE_URI, imageUri?.toString())
        return builder.build()
    }

    private fun uriOrNull(uriString: String): Uri? {
        return if (!TextUtils.isEmpty(uriString)) {
            Uri.parse(uriString)
        } else null
    }

    /**
     * Cancel work using the work's unique name
     */
    fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    /**
     * Setters
     */
    internal fun setImageUri(uri: String) {
        imageUri = uriOrNull(uri)
    }

    internal fun setOutputUri(uri: String) {
        outputUri = uriOrNull(uri)
    }

}