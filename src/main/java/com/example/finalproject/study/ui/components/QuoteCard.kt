package com.example.finalproject.study.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.finalproject.R
import androidx.compose.foundation.background
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset

@Composable
fun QuoteCard(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().height(160.dp), // Slightly reduced height for better proportions
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF8AB4FF), Color(0xFF4574E0))
                    )
                )
                .padding(20.dp)
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 1.4.em,
                    letterSpacing = 0.3.sp,
                    color = Color.White,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.25f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f
                    )
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.65f) // Give more space for text, less for image
            )
            Image(
                painter = painterResource(id = R.drawable.quotelogo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(120.dp) // Slightly smaller image to give more space for text
            )
        }
    }
}

