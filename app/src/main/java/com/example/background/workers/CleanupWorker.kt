package com.example.background.workers

import android.util.Log
import androidx.work.Worker
import com.example.background.Constants.OUTPUT_PATH
import java.io.File

/**
 * CleanupWorker doesn't need to take any input or pass any output. It always deletes
 * the temporary files if they exist.
 */
class CleanupWorker : Worker() {

    private val TAG: String = CleanupWorker::class.java.simpleName

    override fun doWork(): WorkerResult {
        try {

            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)

            if (outputDirectory.exists()) {
                outputDirectory.listFiles({ _, name ->
                    name.isNotEmpty() && name.endsWith(".png")
                }).forEach { file ->
                    val deleted = file.delete()
                    Log.i(TAG, String.format("Deleted %s - %s",
                            file.name, deleted))
                }
            }// else, no files in temp directory. is not an error.

            return WorkerResult.SUCCESS
        } catch (t: Throwable) {
            Log.e(TAG, "Error cleaning up", t)
            return WorkerResult.FAILURE
        }
    }

}