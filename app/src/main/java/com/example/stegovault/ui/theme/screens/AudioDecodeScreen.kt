package com.example.stegovault.ui.screens

import android.net.Uri
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
import com.example.stegovault.utils.decodeWavLSB
import com.example.stegovault.utils.decryptText

@Composable
fun AudioDecodeScreen(navController: NavController) {
    val neonBlue = Color(0xFF00E5FF)
    val context = LocalContext.current

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var key by remember { mutableStateOf("") }
    var decoded by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    val pickAudio = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedUri = uri
        decoded = ""
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
        Text("Decode Audio (WAV)", color = neonBlue, fontSize = 26.sp)
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { pickAudio.launch("audio/*") },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Pick Encoded WAV", color = Color.Black) }

        Spacer(Modifier.height(12.dp))
        Text(
            text = if (selectedUri != null) "Selected: $selectedUri" else "No file selected",
            color = Color.LightGray,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            label = { Text("Decryption Key") },
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
                    decoded = ""

                    val uri = selectedUri ?: run { status = "Pick an encoded WAV file first."; return@Button }
                    if (key.isBlank()) { status = "Enter decryption key."; return@Button }

                    val input = context.contentResolver.openInputStream(uri)
                        ?: throw IllegalArgumentException("Can't open file")

                    val bytes = input.use { it.readBytes() }
                    val extracted = decodeWavLSB(bytes)

                    if (extracted.isBlank()) {
                        status = "No hidden message found."
                        return@Button
                    }

                    val decrypted = decryptText(extracted, key)
                    if (decrypted.isBlank()) {
                        status = "Wrong key or invalid encoded audio."
                    } else {
                        decoded = decrypted
                        status = "Decoded successfully!"
                    }
                } catch (e: Exception) {
                    decoded = ""
                    status = "Error: ${e.message}"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Decode", color = Color.Black) }

        Spacer(Modifier.height(12.dp))
        if (status.isNotBlank()) Text(status, color = Color.LightGray)

        if (decoded.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text("Hidden Message:", color = neonBlue)

            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) { Text(decoded, color = Color.White, fontSize = 18.sp) }
        }
    }
}
