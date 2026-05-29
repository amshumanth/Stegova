package com.example.stegovault.ui.screens

import android.graphics.Bitmap
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
import com.example.stegovault.utils.decodeLSB
import com.example.stegovault.utils.decryptText
import com.example.stegovault.utils.uriToBitmap

@Composable
fun ImageDecodeScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var key by remember { mutableStateOf("") }
    var decodedMessage by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("") }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedBitmap = uriToBitmap(context, it) }
            decodedMessage = null
            status = ""
        }

    val neonBlue = Color(0xFF00E5FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Decode Image", color = neonBlue, fontSize = 28.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Pick Encoded Image", color = Color.Black) }

        selectedBitmap?.let { bmp ->
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                modifier = Modifier.size(250.dp)
            ) {
                Image(bmp.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    status = ""
                    decodedMessage = null

                    if (key.isBlank()) {
                        status = "Enter decryption key."
                        return@Button
                    }

                    val extracted = decodeLSB(bmp)        // existing logic
                    val decrypted = decryptText(extracted, key)

                    if (decrypted.isBlank()) {
                        status = "Wrong key or no hidden message."
                    } else {
                        decodedMessage = decrypted
                        status = "Decoded successfully!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Decode Message", color = Color.Black) }
        }

        if (status.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(status, color = Color.LightGray)
        }

        decodedMessage?.let { msg ->
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray, RoundedCornerShape(12.dp))
                    .padding(15.dp)
            ) {
                Text(msg, color = Color.White, fontSize = 18.sp)
            }
        }
    }
}