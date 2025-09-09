package com.example.finalproject.pomodoro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimerCard(phaseLabel: String, mmss: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.linearGradient(listOf(Color(0xFF8AB4FF), Color(0xFF4574E0))),
                shape = RoundedCornerShape(28.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(phaseLabel, style = MaterialTheme.typography.titleLarge, color = Color.White)
            Text(mmss, fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

