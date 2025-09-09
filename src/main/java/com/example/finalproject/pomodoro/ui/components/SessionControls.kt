package com.example.finalproject.pomodoro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SessionControls(
    isRunning: Boolean,
    onPlayPause: () -> Unit,
    onReset: () -> Unit,
    onSwitchPhase: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilledTonalButton(onClick = onPlayPause, modifier = Modifier.weight(1f)) {
            Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, null)
            Text(if (isRunning) "Pause" else "Start")
        }
        OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.RestartAlt, null); Text("Reset")
        }
        Button(onClick = onSwitchPhase, modifier = Modifier.weight(1f)) { Text("Switch") }
    }
}
