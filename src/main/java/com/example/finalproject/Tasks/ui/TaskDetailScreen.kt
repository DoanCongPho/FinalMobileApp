package com.example.dailyview.Tasks.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyview.Tasks.model.CalendarTask
import com.example.dailyview.Tasks.viewmodel.TaskViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.dailyview.Tasks.ui.getDrawableId

@Composable
fun TaskDetailScreen(
    task: CalendarTask,
    onClose: () -> Unit,
    onEdit: (CalendarTask) -> Unit,
    onDelete: (CalendarTask) -> Unit,
    onToggleState: (CalendarTask) -> Unit = {}
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button
            IconButtonWithHover(
                normalIcon = "close_button",
                hoverIcon = "close_button_hover",
                contentDescription = "Close",
                modifier = Modifier.padding(start = 8.dp)
            ) { onClose() }
            Spacer(modifier = Modifier.weight(1f))
            // Edit button
            IconButtonWithHover(
                normalIcon = "edit_button",
                hoverIcon = "edit_button_hover",
                contentDescription = "Edit",
                modifier = Modifier.padding(end = 8.dp)
            ) { onEdit(task) }
            // Delete button
            IconButtonWithHover(
                normalIcon = "delete_button",
                hoverIcon = "delete_button_hover",
                contentDescription = "Delete",
                modifier = Modifier.padding(end = 8.dp)
            ) { showDeleteConfirm = true }
        }
        // Main content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 72.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Tag
            Text(
                text = task.tag ?: "",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Normal
            )
            // Title with strikethrough if done
            Text(
                text = task.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp),
                textDecoration = if (task.state == 1) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
            // Time
            val formatter = DateTimeFormatter.ofPattern("EEEE, d MMM 'at' HH:mm", Locale.getDefault())
            val timeString = if (task.time != null) {
                task.date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() } + ", " +
                        task.date.dayOfMonth + " " +
                        task.date.month.name.lowercase().replaceFirstChar { it.uppercase() } +
                        " at " + String.format("%02d:%02d", task.time.hour, task.time.minute)
            } else "All day"
            Text(
                text = timeString,
                fontSize = 16.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = getDrawableId("detail_icon")),
                    contentDescription = "Details icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = task.details ?: "None",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            // Mark completed/uncompleted button at bottom right
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                val markText = if (task.state == 0) "Mark completed" else "Mark uncompleted"
                Text(
                    text = markText,
                    color = Color(0xFF388E3C),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 64.dp, end = 16.dp)
                        .clickable { onToggleState(task) }
                )
            }
        }
        // Delete confirmation dialog
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete this task?") },
                confirmButton = {
                    Button(onClick = {
                        showDeleteConfirm = false
                        onDelete(task)
                    }) { Text("Delete") }
                },
                dismissButton = {
                    Button(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun IconButtonWithHover(
    normalIcon: String,
    hoverIcon: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    Image(
        painter = painterResource(id = getDrawableId(if (isHovered) hoverIcon else normalIcon)),
        contentDescription = contentDescription,
        modifier = modifier
            .size(32.dp)
            .clickable { onClick() }
    )
}

