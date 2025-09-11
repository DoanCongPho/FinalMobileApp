package com.example.finalproject.createquiz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StepDots(current: Int, total: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        repeat(total) { i ->
            Box(
                Modifier
                    .padding(horizontal = 8.dp)
                    .height(10.dp)
                    .width(if (i == current) 36.dp else 24.dp)
                    .background(
                        if (i == current) Color(0xFFFF974A) else Color(0xFFE3E7F1),
                        RoundedCornerShape(16.dp)
                    )
            )
        }
    }
}
