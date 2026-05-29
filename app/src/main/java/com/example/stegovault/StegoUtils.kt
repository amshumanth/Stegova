package com.example.stegovault.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.content.ContentValues
import java.io.OutputStream

/* --------------------- BITMAP UTILITIES --------------------- */

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it)
    }
}

/* --------------------- LSB ENCODING --------------------- */

fun encodeLSB(bitmap: Bitmap, message: String): Bitmap {
    val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val msg = (message + "#END#").toByteArray()  // Add terminator

    var byteIndex = 0
    var bitIndex = 0

    loop@ for (y in 0 until mutable.height) {
        for (x in 0 until mutable.width) {
            if (byteIndex >= msg.size) break@loop

            val pixel = mutable.getPixel(x, y)
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            val bit = (msg[byteIndex].toInt() shr bitIndex) and 1
            val newBlue = (b and 0xFE) or bit

            val newPixel = (0xFF shl 24) or (r shl 16) or (g shl 8) or newBlue
            mutable.setPixel(x, y, newPixel)

            bitIndex++
            if (bitIndex == 8) {
                bitIndex = 0
                byteIndex++
            }
        }
    }

    return mutable
}

/* --------------------- LSB DECODING --------------------- */

fun decodeLSB(bitmap: Bitmap): String {
    val byteList = ArrayList<Byte>()
    var byte = 0
    var bitIndex = 0

    loop@ for (y in 0 until bitmap.height) {
        for (x in 0 until bitmap.width) {
            val pixel = bitmap.getPixel(x, y)
            val blue = pixel and 0xFF
            val bit = blue and 1

            byte = byte or (bit shl bitIndex)
            bitIndex++

            if (bitIndex == 8) {
                byteList.add(byte.toByte())
                val currentText = byteList.toByteArray().toString(Charsets.UTF_8)
                if (currentText.endsWith("#END#")) break@loop
                bitIndex = 0
                byte = 0
            }
        }
    }

    val messageWithTerminator = byteList.toByteArray().toString(Charsets.UTF_8)
    return messageWithTerminator.replace("#END#", "") // remove terminator
}


/* --------------------- SAVE TO GALLERY --------------------- */

fun saveToGallery(context: Context, bitmap: Bitmap) {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "encoded_${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/StegoVault")
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    val stream: OutputStream? = uri?.let { context.contentResolver.openOutputStream(it) }

    stream?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
}
