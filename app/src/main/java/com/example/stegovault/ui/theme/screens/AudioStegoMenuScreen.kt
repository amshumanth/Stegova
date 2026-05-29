package com.example.stegovault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AudioStegoMenuScreen(navController: NavController) {
    val neonBlue = Color(0xFF00E5FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Audio Steganography (WAV)", color = neonBlue, fontSize = 26.sp)

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = { navController.navigate("audio_encode") },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Encode WAV", color = Color.Black, fontSize = 18.sp)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("audio_decode") },
            colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Decode WAV", color = Color.Black, fontSize = 18.sp)
        }

        Spacer(Modifier.height(12.dp))
        Text("Supports only 16-bit PCM .wav", color = Color.LightGray, fontSize = 13.sp)
    }
}
