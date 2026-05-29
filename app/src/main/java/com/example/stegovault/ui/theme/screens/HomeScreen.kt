package com.example.stegovault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Videocam


private data class HomeTile(
    val title: String,
    val subtitle: String,
    val route: String,
    val bgColor: Color,
    val icon: @Composable () -> Unit
)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF061A3A),
            Color(0xFF0B0B2B),
            Color(0xFF12001F)
        )
    )
    val tiles = listOf(
        HomeTile(
            title = "Image",
            subtitle = "Steganography",
            route = "image_menu",
            bgColor = Color(0xFF1E88E5),
            icon = { Icon(Icons.Filled.Image, contentDescription = "Image", tint = Color.White) }
        ),
        HomeTile(
            title = "Text",
            subtitle = "Steganography",
            route = "text_menu",
            bgColor = Color(0xFF00ACC1),
            icon = { Icon(Icons.Filled.TextFields, contentDescription = "Text", tint = Color.White) }
        ),
        HomeTile(
            title = "Audio",
            subtitle = "Steganography",
            route = "audio_menu",
            bgColor = Color(0xFF8E24AA),
            icon = { Icon(Icons.Filled.GraphicEq, contentDescription = "Audio", tint = Color.White) }
        ),
        HomeTile(
            title = "Video",
            subtitle = "Steganography",
            route = "video_menu",
            bgColor = Color(0xFF43A047),
            icon = { Icon(Icons.Filled.Videocam, contentDescription = "Video", tint = Color.White) }
        )
    )




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {

        Text(
            text = "CypherSnap",
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )

        Text(
            text = "Image, Text , Audio & Video Steganography",
            fontSize = 16.sp,
            color = Color(0xFFB7C7FF),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tiles) { tile ->
                HomeTileCard(tile = tile) { onNavigate(tile.route) }
            }
        }
    }
}

@Composable
private fun HomeTileCard(tile: HomeTile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(170.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = tile.bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                tile.icon()
            }

            Column {
                Text(
                    text = tile.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = tile.subtitle,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}
