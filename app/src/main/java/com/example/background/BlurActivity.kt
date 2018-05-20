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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup
import com.bumptech.glide.Glide
import com.example.background.Constants.KEY_IMAGE_URI


class BlurActivity : AppCompatActivity() {

    private lateinit var viewModel: BlurViewModel
    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var goButton: Button
    private lateinit var outputButton: Button
    private lateinit var cancelButton: Button

    /**
     * Get the blur level from the radio button as an integer
     * @return Integer representing the amount of times to blur the image
     */
    private val blurLevel: Int
        get() {
            val radioGroup = findViewById<RadioGroup>(R.id.radio_blur_group)

            when (radioGroup.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> return 1
                R.id.radio_blur_lv_2 -> return 2
                R.id.radio_blur_lv_3 -> return 3
            }

            return 1
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)

        // Get the ViewModel
        viewModel = ViewModelProviders.of(this).get(BlurViewModel::class.java)

        // Get all of the Views
        imageView = findViewById(R.id.image_view)
        progressBar = findViewById(R.id.progress_bar)
        goButton = findViewById(R.id.go_button)
        outputButton = findViewById(R.id.see_file_button)
        cancelButton = findViewById(R.id.cancel_button)

        // Image uri should be stored in the ViewModel; put it there then display
        val intent = intent
        val imageUriExtra = intent.getStringExtra(Constants.KEY_IMAGE_URI)
        viewModel.setImageUri(imageUriExtra)
        if (viewModel.imageUri != null) {
            Glide.with(this).load(viewModel.imageUri).into(imageView)
        }

        viewModel.getOutputStatus().observe(this, Observer { workStatuses ->
            // If there are no matching work statuses, do nothing
            if (workStatuses == null || workStatuses.isEmpty()) {
                return@Observer
            }
            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workStatus = workStatuses.first()
            val finished = workStatus.state.isFinished
            if (finished) {
                showWorkFinished()

                // Normally this processing, which is not directly related to drawing views on
                // screen would be in the ViewModel. For simplicity we are keeping it here.
                val outputData = workStatus.outputData
                val outputUri = outputData.getString(KEY_IMAGE_URI, null)
                if (!outputUri.isNullOrEmpty()) {
                    viewModel.setOutputUri(outputUri)
                    outputButton.visibility = View.VISIBLE
                }
            } else {
                showWorkInProgress()
            }
        })

        // Setup blur image file button
        goButton.setOnClickListener { viewModel.applyBlur(blurLevel) }

        // Setup see file button
        outputButton.setOnClickListener {
            val outputUri = viewModel.outputUri
            if (outputUri != null) {
                val actionView = Intent(Intent.ACTION_VIEW, outputUri)
                if (actionView.resolveActivity(packageManager) != null) {
                    startActivity(actionView)
                }
            }
        }

        // Hookup the Cancel button
        cancelButton.setOnClickListener({ viewModel.cancelWork() })
    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        progressBar.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
        goButton.visibility = View.GONE
        outputButton.visibility = View.GONE
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        progressBar.visibility = View.GONE
        cancelButton.visibility = View.GONE
        goButton.visibility = View.VISIBLE
    }
}