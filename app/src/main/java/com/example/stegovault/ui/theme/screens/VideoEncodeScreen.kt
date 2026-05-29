package com.example.stegovault.ui.screens

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stegovault.utils.encodeVideoFooter
import com.example.stegovault.utils.encryptText

@Composable
fun VideoEncodeScreen(navController: NavController) {
    val neonBlue = Color(0xFF00E5FF)
    val context = LocalContext.current

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var secret by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var encodedBytes by remember { mutableStateOf<ByteArray?>(null) }

    val pickVideo = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedUri = uri
        encodedBytes = null
        status = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Encode Video (MP4)", color = neonBlue, fontSize = 26.sp)
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { pickVideo.launch("video/*") },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Pick Video", color = Color.Black) }

        Spacer(Modifier.height(10.dp))
        Text(
            text = if (selectedUri != null) "Selected: $selectedUri" else "No file selected",
            color = Color.LightGray,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = secret,
            onValueChange = { secret = it },
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
            minLines = 3
        )

        Spacer(Modifier.height(14.dp))

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

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                try {
                    status = ""
                    val uri = selectedUri ?: run { status = "Pick a video first."; return@Button }
                    if (secret.isBlank()) { status = "Enter secret message."; return@Button }
                    if (key.isBlank()) { status = "Enter encryption key."; return@Button }

                    val input = context.contentResolver.openInputStream(uri)
                        ?: throw IllegalArgumentException("Can't open file")

                    val videoBytes = input.use { it.readBytes() }

                    val encrypted = encryptText(secret, key)
                    encodedBytes = encodeVideoFooter(videoBytes, encrypted)

                    status = "Encoded successfully. Now Save."
                } catch (e: Exception) {
                    encodedBytes = null
                    status = "Error: ${e.message}"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Encode", color = Color.Black) }

        Spacer(Modifier.height(10.dp))
        if (status.isNotBlank()) Text(status, color = Color.LightGray)

        encodedBytes?.let { bytes ->
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val filename = "stegovault_${System.currentTimeMillis()}.mp4"
                        val values = ContentValues().apply {
                            put(MediaStore.Video.Media.DISPLAY_NAME, filename)
                            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/StegoVault")
                        }

                        val outUri = context.contentResolver.insert(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            values
                        ) ?: throw IllegalStateException("Failed to create MediaStore record")

                        context.contentResolver.openOutputStream(outUri)?.use { os ->
                            os.write(bytes)
                            os.flush()
                        } ?: throw IllegalStateException("Failed to open output stream")

                        Toast.makeText(context, "Saved: Movies/StegoVault/$filename", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save Encoded Video", color = Color.Black) }
        }
    }
}
