package com.example.stegovault.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stegovault.ui.screens.*

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") { HomeScreen { route -> navController.navigate(route) } }

        composable("image_menu") { ImageStegoMenuScreen(navController) }

        composable("image_encode") { ImageEncodeScreen(navController) }

        composable("image_decode") { ImageDecodeScreen(navController) }

        composable("text_menu") { TextStegoMenuScreen(navController) }
        composable("text_encode") { TextEncodeScreen(navController) }
        composable("text_decode") { TextDecodeScreen(navController) }
        composable("audio_menu") { AudioStegoMenuScreen(navController) }
        composable("audio_encode") { AudioEncodeScreen(navController) }
        composable("audio_decode") { AudioDecodeScreen(navController) }
        composable("video_menu") { VideoStegoMenuScreen(navController) }
        composable("video_encode") { VideoEncodeScreen(navController) }
        composable("video_decode") { VideoDecodeScreen(navController) }



    }
}
