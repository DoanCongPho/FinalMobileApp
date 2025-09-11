package com.example.finalproject.createquiz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SourceRow(name: String, sub: String, onRemove: () -> Unit) {
    Surface(
        color = Color(0xFF222222),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.InsertDriveFile, null, tint = Color(0xFFFF5A8B))
            Column(Modifier.weight(1f)) {
                Text(name, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(sub, color = Color(0xFFBDBDBD), style = MaterialTheme.typography.labelLarge)
            }
            IconButton(onClick = onRemove) { Icon(Icons.Default.Close, contentDescription = "Remove") }
        }
    }
}
