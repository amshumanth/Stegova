package com.example.stegovault

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ImageDecodeScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var decodedMessage by remember { mutableStateOf<String?>(null) }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedBitmap = uriToBitmap(context, it) }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Decode Image", color = Color(0xFF00E5FF), fontSize = 28.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
        ) {
            Text("Pick Encoded Image", color = Color.Black)
        }

        selectedBitmap?.let { bmp ->
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { decodedMessage = decodeLSB(bmp) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
            ) {
                Text("Decode Message", color = Color.Black)
            }
        }

        decodedMessage?.let { msg ->
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray, RoundedCornerShape(12.dp))
                    .padding(15.dp)
            ) {
                Text(
                    text = msg,
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

/* ---------------- LSB DECODE ---------------- */
fun decodeLSB(bitmap: Bitmap): String {
    val byteList = ArrayList<Byte>()
    var byte = 0
    var bitIndex = 0

    for (y in 0 until bitmap.height) {
        for (x in 0 until bitmap.width) {
            val pixel = bitmap.getPixel(x, y)
            val blue = pixel and 0xFF
            val bit = blue and 1

            byte = byte or (bit shl bitIndex)
            bitIndex++

            if (bitIndex == 8) {
                if (byte == 0) return byteList.toByteArray().toString(Charsets.UTF_8) // End of message
                byteList.add(byte.toByte())
                byte = 0
                bitIndex = 0
            }
        }
    }
    return byteList.toByteArray().toString(Charsets.UTF_8)
}

fun uriToBitmap(context: android.content.Context, uri: Uri): Bitmap {
    return context.contentResolver.openInputStream(uri).use {
        BitmapFactory.decodeStream(it)
    }!!
}
