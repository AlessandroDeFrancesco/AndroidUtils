package com.alessandrodefrancesco.androidutils

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.util.*

/**
 * Try to download the file asynchronously from [fileURL] if it points to a File and save it in the specified folder
 * It will try to retain the original file name if content-disposition header is present
 * @param fileURL the URL pointing to a file
 * @param outputFolder the folder where to save the file
 * @param fileDownloaded called when the file has been downloaded or an error occurred
 */
fun downloadFile(
    fileURL: String,
    outputFolder: File,
    fileDownloaded: (Result<File>) -> Unit
) {
    object : AsyncTask<Unit, Unit, File?>() {
        var failException: Exception? = null

        override fun doInBackground(vararg params: Unit?): File? {
            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null
            var file: File? = null

            try {
                val url = URL(fileURL)
                val conn = url.openConnection()

                // RETRIEVE FILENAME OR GENERATE A RANDOM ONE
                val fileNameRegex = "\"(.*?)\"".toRegex()
                val dispositionHeader: String? = conn.getHeaderField("content-disposition")
                val fileNameMatch: String? = fileNameRegex.find(dispositionHeader ?: "")?.value?.replace("\"", "")
                var fileName: String? = UUID.randomUUID().toString()
                if (dispositionHeader != null && fileNameMatch != null)
                    fileName = fileNameMatch

                // SAVE THE FILE
                file = File(outputFolder, fileName)
                Log.d(TAG, "Saving file to ${file.toURI()}")

                inputStream = DataInputStream(url.openStream())
                outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var len = inputStream.read(buffer)
                while (len > 0) {
                    outputStream.write(buffer, 0, len)
                    len = inputStream.read(buffer)
                }

                outputStream.flush()
            } catch (e: Exception) {
                failException = e
            } finally {
                outputStream?.close()
                inputStream?.close()
            }

            return file
        }

        override fun onPostExecute(result: File?) {
            val failException = failException
            if (failException == null && result != null)
                fileDownloaded(Result.success(result))
            else
                fileDownloaded(Result.failure(failException ?: Exception()))
        }
    }.execute()
}

/**
 * Try to download the file synchronously from this [Uri] if it points to a File and save it in the android temporary directory
 */
fun Uri.toFile(
    context: Context
): File? {
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null
    var file: File? = null
    try {
        inputStream = context.contentResolver.openInputStream(this)
        file = File.createTempFile("temp", this.lastPathSegment?.substringAfterLast("."), context.cacheDir)
        outputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var len = inputStream.read(buffer)
        while (len > 0) {
            outputStream.write(buffer, 0, len)
            len = inputStream.read(buffer)
        }

        outputStream.flush()
    } catch (e: Exception) {
        Log.e(TAG, "Cannnot convert to file $this \n ${e.localizedMessage}")
    } finally {
        outputStream?.close()
        inputStream?.close()
    }

    return file
}