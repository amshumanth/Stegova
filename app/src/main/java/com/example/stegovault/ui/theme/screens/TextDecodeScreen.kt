package com.example.stegovault.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stegovault.utils.decodeTextStego
import com.example.stegovault.utils.decryptText

@Composable
fun TextDecodeScreen(navController: NavController) {

    val neonBlue = Color(0xFF00E5FF)

    var inputText by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var decodedText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Text Steganography – Decode", color = neonBlue, fontSize = 26.sp)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Paste Encoded Text Here") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = neonBlue,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = neonBlue
            ),
            shape = RoundedCornerShape(12.dp),
            minLines = 6
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

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                status = ""
                decodedText = ""

                if (key.isBlank()) {
                    status = "Enter decryption key."
                    return@Button
                }

                val extracted = decodeTextStego(inputText)
                if (extracted.isBlank()) {
                    status = "No hidden message found."
                    return@Button
                }

                val decrypted = decryptText(extracted, key)
                if (decrypted.isBlank()) {
                    status = "Wrong key or invalid encoded text."
                } else {
                    decodedText = decrypted
                    status = "Decoded successfully!"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Decode", color = Color.Black)
        }

        if (status.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(status, color = Color.LightGray)
        }

        if (decodedText.isNotEmpty()) {
            Spacer(Modifier.height(18.dp))
            Text("Hidden Message:", color = neonBlue)

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(decodedText, color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
