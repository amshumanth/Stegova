package com.example.stegovault.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stegovault.utils.encodeTextStego
import com.example.stegovault.utils.encryptText

@Composable
fun TextEncodeScreen(navController: NavController) {

    val neonBlue = Color(0xFF00E5FF)
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var coverText by remember { mutableStateOf("") }
    var secretText by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var encodedText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Text Steganography – Encode", color = neonBlue, fontSize = 26.sp)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = coverText,
            onValueChange = { coverText = it },
            label = { Text("Cover Text (Visible)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = neonBlue,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = neonBlue
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 5
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = secretText,
            onValueChange = { secretText = it },
            label = { Text("Secret Message (Hidden)") },
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

        Spacer(Modifier.height(16.dp))

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

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                status = ""
                encodedText = ""

                if (coverText.isBlank() || secretText.isBlank()) {
                    status = "Enter cover text and secret message."
                    return@Button
                }
                if (key.isBlank()) {
                    status = "Enter encryption key."
                    return@Button
                }

                val encrypted = encryptText(secretText, key)
                encodedText = encodeTextStego(coverText, encrypted)
                status = "Encoded successfully!"
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Encode Text", color = Color.Black)
        }

        if (status.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(status, color = Color.LightGray)
        }

        if (encodedText.isNotEmpty()) {
            Spacer(Modifier.height(18.dp))

            Text("Encoded Text:", color = neonBlue)

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(encodedText, color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    clipboard.setText(AnnotatedString(encodedText))
                    Toast.makeText(context, "Encoded text copied!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy Encoded Text", color = Color.Black)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "⚠ Hidden message is invisible.\nAlways copy using the button.",
                color = Color.LightGray,
                fontSize = 13.sp
            )
        }
    }
}
