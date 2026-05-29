package com.example.stegovault.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.stegovault.utils.encodeLSB
import com.example.stegovault.utils.encryptText
import com.example.stegovault.utils.saveToGallery
import com.example.stegovault.utils.uriToBitmap

@Composable
fun ImageEncodeScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var encodedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var message by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var savedMessage by remember { mutableStateOf("") }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedBitmap = uriToBitmap(context, it) }
            encodedBitmap = null
            savedMessage = ""
        }

    val neonBlue = Color(0xFF00E5FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Encode Image", color = neonBlue, fontSize = 28.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue)
        ) { Text("Pick Image", color = Color.Black) }

        Spacer(modifier = Modifier.height(20.dp))

        selectedBitmap?.let { bmp ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                modifier = Modifier.size(250.dp)
            ) {
                Image(bmp.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Secret Message") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = neonBlue,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = neonBlue
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            label = { Text("Encryption Key") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = neonBlue,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = neonBlue
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                savedMessage = ""
                val bmp = selectedBitmap
                if (bmp == null) {
                    savedMessage = "Pick an image first."
                    return@Button
                }
                if (message.isBlank()) {
                    savedMessage = "Enter secret message."
                    return@Button
                }
                if (key.isBlank()) {
                    savedMessage = "Enter encryption key."
                    return@Button
                }

                val encrypted = encryptText(message, key)
                encodedBitmap = encodeLSB(bmp, encrypted)
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue)
        ) { Text("Encode", color = Color.Black) }

        Spacer(modifier = Modifier.height(20.dp))

        encodedBitmap?.let { bmp ->
            Text("Encoded Image:", color = neonBlue)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                modifier = Modifier.size(250.dp)
            ) {
                Image(bmp.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    saveToGallery(context, bmp)
                    savedMessage = "Image saved to gallery!"
                },
                colors = ButtonDefaults.buttonColors(containerColor = neonBlue)
            ) { Text("Save Encoded Image", color = Color.Black) }
        }

        if (savedMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(savedMessage, color = if (savedMessage.contains("saved", true)) Color.Green else Color.LightGray)
        }
    }
}
