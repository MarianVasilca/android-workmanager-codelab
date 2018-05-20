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

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import java.util.*

class SelectImageActivity : AppCompatActivity() {

    private var mPermissionRequestCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        if (savedInstanceState != null) {
            mPermissionRequestCount = savedInstanceState.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0)
        }

        // Make sure the app has correct permissions to run
        requestPermissionsIfNecessary()

        // Create request to get image from filesystem when button clicked
        findViewById<View>(R.id.selectImage).setOnClickListener({
            val chooseIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(chooseIntent, REQUEST_CODE_IMAGE)
        })
    }

    /**
     * Save the permission request count on a rotate
     */

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, mPermissionRequestCount)
    }

    /**
     * Request permissions twice - if the user denies twice then show a toast about how to update
     * the permission for storage. Also disable the button if we don't have access to pictures on
     * the device.
     */
    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions()) {
            if (mPermissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                mPermissionRequestCount += 1
                ActivityCompat.requestPermissions(
                        this,
                        sPermissions.toTypedArray(),
                        REQUEST_CODE_PERMISSIONS)
            } else {
                Toast.makeText(this, R.string.set_permissions_in_settings,
                        Toast.LENGTH_LONG).show()
                findViewById<View>(R.id.selectImage).isEnabled = false
            }
        }
    }

    private fun checkAllPermissions(): Boolean {
        var hasPermissions = true
        for (permission in sPermissions) {
            hasPermissions = hasPermissions and (ContextCompat.checkSelfPermission(
                    this, permission) == PackageManager.PERMISSION_GRANTED)
        }
        return hasPermissions
    }

    /**
     * Permission Checking
     */

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            requestPermissionsIfNecessary() // no-op if permissions are granted already.
        }
    }

    /**
     * Image Selection
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> handleImageRequestResult(data)
                else -> Log.d(TAG, "Unknown request code.")
            }
        } else {
            Log.e(TAG, String.format("Unexpected Result code %s", resultCode))
        }
    }

    private fun handleImageRequestResult(data: Intent) {
        var imageUri: Uri? = null
        if (data.clipData != null) {
            imageUri = data.clipData!!.getItemAt(0).uri
        } else if (data.data != null) {
            imageUri = data.data
        }

        if (imageUri == null) {
            Log.e(TAG, "Invalid input image Uri.")
            return
        }

        val filterIntent = Intent(this, BlurActivity::class.java)
        filterIntent.putExtra(Constants.KEY_IMAGE_URI, imageUri.toString())
        startActivity(filterIntent)
    }

    companion object {

        private const val TAG = "SelectImageActivity"

        private const val REQUEST_CODE_IMAGE = 100
        private const val REQUEST_CODE_PERMISSIONS = 101

        private const val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT"
        private const val MAX_NUMBER_REQUEST_PERMISSIONS = 2

        private val sPermissions = Arrays.asList(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
