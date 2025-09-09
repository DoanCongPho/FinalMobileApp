package com.example.finalproject.study.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finalproject.R

@Composable
fun GreetingHeader(
    greeting: String,
    name: String,
    avatar: Painter,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(greeting, style = MaterialTheme.typography.labelLarge)
            Text(name, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
        }
        Image(painter = painterResource(id = R.drawable.avatar), contentDescription = null, modifier = Modifier.size(64.dp))
    }
}
