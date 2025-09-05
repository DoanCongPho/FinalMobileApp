package com.example.dailyview.Tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyview.Tasks.model.CalendarTask

@Composable
fun TaskCard(task: CalendarTask, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxHeight(1f)
            .padding(
                top = 80.dp * ((task.time?.minute ?: 0) / 60f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFA7F3D0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = task.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = if (task.state == 1) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
        }
    }
}