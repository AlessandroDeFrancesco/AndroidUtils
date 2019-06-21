package com.alessandrodefrancesco.androidutils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Resize a [Bitmap] retaining the same aspect ratio
 * The resulting [Bitmap] will have the height matching [maxHeight] and the width <= [maxWidth] or
 * will have the width matching [maxWidth] and the height <= [maxHeight]
 * @param maxWidth the max width of the resulting bitmap
 * @param maxHeight the max height of the resulting bitmap
 */
fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
    if (maxHeight > 0 && maxWidth > 0) {
        val width = width
        val height = height
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }

        return Bitmap.createScaledBitmap(this, finalWidth, finalHeight, true)
    } else {
        return this
    }
}

/**
 * Return the first frame of the video from the URL as [Bitmap]
 * @param videoPath an URI pointing to a video
 */
fun getFirstFrameFromVideoURL(videoPath: String): Bitmap? {
    var bitmap: Bitmap? = null
    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(videoPath, HashMap())

        bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        mediaMetadataRetriever?.release()
    }
    return bitmap
}

/**
 * Convert a Bitmap to an image file and save it in a temporary File
 * @param context  The android context
 * @param format   The format of the compressed image
 * @param quality  Hint to the compressor, 0-100. 0 meaning compress for
 *                 small size, 100 meaning compress for max quality. Some
 *                 formats, like PNG which is lossless, will ignore the
 *                 quality setting
 */
fun Bitmap.toJPGFile(
    context: Context,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 70
): File? {
    var file: File? = null
    var bos: ByteArrayOutputStream? = null
    var fos: FileOutputStream? = null
    try {
        bos = ByteArrayOutputStream()
        file = File.createTempFile("temp", format.name.toLowerCase(), context.cacheDir)
        this.compress(format, quality, bos)
        fos = FileOutputStream(file)
        fos.write(bos.toByteArray())
        fos.flush()
    } catch (e: Exception) {
        Log.e(TAG, "Cannnot convert Bitmap to file \n ${e.localizedMessage}")
    } finally {
        fos?.close()
        bos?.close()
    }

    return file
}