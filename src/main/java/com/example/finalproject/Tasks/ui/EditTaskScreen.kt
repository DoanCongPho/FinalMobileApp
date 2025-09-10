package com.example.finalproject.Tasks.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.Tasks.model.CalendarTask
import com.example.finalproject.Tasks.model.RepeatFrequency
import com.example.finalproject.Tasks.model.RepeatEnd
import com.example.finalproject.Tasks.ui.getDrawableId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun EditTaskScreen(
    task: CalendarTask,
    onCancel: () -> Unit,
    onSave: (CalendarTask) -> Unit,
    onCustomRecurrence: (RepeatFrequency) -> Unit,
    calendarViewModel: com.example.finalproject.calendar.viewmodel.CalendarViewModel1
) {
    // Initialize draft task with existing task data when entering edit mode
    LaunchedEffect(task.id) {
        calendarViewModel.saveDraftTask(task)
    }
    
    val draftTask = calendarViewModel.draftTask
    
    // Initialize all state variables from draft task (which was initialized with the existing task)
    var taskDate by remember { mutableStateOf(draftTask?.date ?: task.date) }
    var time by remember { mutableStateOf(draftTask?.time ?: task.time ?: java.time.LocalTime.now().withSecond(0).withNano(0)) }
    var repeat by remember { mutableStateOf(draftTask?.repeatFrequency ?: task.repeatFrequency) }
    var showRepeatDialog by remember { mutableStateOf(false) }
    val repeatOptions = listOf(
        RepeatFrequency.NONE to "Does not repeat",
        RepeatFrequency.DAILY to "Every day",
        RepeatFrequency.WEEKLY to "Every week",
        RepeatFrequency.MONTHLY to "Every month",
        RepeatFrequency.YEARLY to "Every year",
        null to "Custom"
    )
    var details by remember { mutableStateOf(draftTask?.details ?: task.details ?: "") }
    var showCancelConfirm by remember { mutableStateOf(false) }
    var showWarning by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(draftTask?.title ?: task.title) }
    var tag by remember { mutableStateOf(draftTask?.tag ?: task.tag ?: "") }
    var isAllDay by remember { mutableStateOf(draftTask?.isAllDay ?: task.isAllDay) }
    
    // Function to create current draft task from form state, preserving the original task ID
    fun createCurrentDraft(): CalendarTask {
        return CalendarTask(
            id = task.id, // Keep the original ID for editing
            title = title,
            details = details.takeIf { it.isNotBlank() },
            isAllDay = isAllDay,
            date = taskDate,
            time = if (isAllDay) null else time,
            repeatFrequency = repeat,
            repeatEnd = draftTask?.repeatEnd ?: task.repeatEnd, // Preserve repeat end settings
            monthlyPattern = draftTask?.monthlyPattern ?: task.monthlyPattern, // Preserve monthly pattern
            tag = tag,
            state = task.state // Preserve the completion state
        )
    }
    
    // Save draft whenever form data changes
    LaunchedEffect(title, details, isAllDay, taskDate, time, repeat, tag) {
        calendarViewModel.saveDraftTask(createCurrentDraft())
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF232326))) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cancel",
                color = Color(0xFF1976D2),
                fontSize = 18.sp,
                modifier = Modifier.clickable { showCancelConfirm = true }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Save",
                color = Color(0xFF1976D2),
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    if (title.isBlank() || tag.isBlank()) {
                        showWarning = true
                    } else {
                        val editedTask = CalendarTask(
                            id = task.id, // Keep original ID
                            title = title,
                            details = details.takeIf { it.isNotBlank() },
                            isAllDay = isAllDay,
                            date = taskDate,
                            time = if (isAllDay) null else time,
                            repeatFrequency = repeat,
                            repeatEnd = draftTask?.repeatEnd ?: task.repeatEnd,
                            monthlyPattern = draftTask?.monthlyPattern ?: task.monthlyPattern,
                            tag = tag,
                            state = task.state, // Preserve completion state
                            seriesId = task.seriesId // Preserve original series ID
                        )
                        
                        // Check if repeat pattern changed
                        val originalRepeatFrequency = task.repeatFrequency
                        val newRepeatFrequency = repeat
                        
                        if (originalRepeatFrequency == newRepeatFrequency && newRepeatFrequency != RepeatFrequency.NONE) {
                            // No repeat pattern changes: Use updateTaskSeries() to update all tasks in series
                            calendarViewModel.updateTaskSeries(editedTask)
                        } else if (originalRepeatFrequency != newRepeatFrequency) {
                            // Repeat pattern changes:
                            
                            // Save copy of original task before any modifications
                            val originalTaskCopy = task.copy()
                            
                            // 1. Delete old series if it existed
                            task.seriesId?.let { oldSeriesId ->
                                calendarViewModel.deleteTaskSeries(oldSeriesId)
                            }
                            
                            // 2. Remove the original task from repository
                            calendarViewModel.deleteTask(originalTaskCopy)
                            
                            // 3. Add new root task manually
                            val newRootTask = if (newRepeatFrequency != RepeatFrequency.NONE) {
                                // Generate new series ID if it will have repeats
                                editedTask.copy(seriesId = java.util.UUID.randomUUID().toString())
                            } else {
                                // Remove series ID if no longer repeating
                                editedTask.copy(seriesId = null)
                            }
                            
                            calendarViewModel.addTask(newRootTask)
                            
                            // 4. Call createTaskSeries(newRootTask) if new pattern exists
                            if (newRepeatFrequency != RepeatFrequency.NONE) {
                                calendarViewModel.createTaskSeries(newRootTask)
                            }
                        } else {
                            // Simple update for non-repeating tasks
                            calendarViewModel.updateTask(editedTask)
                        }
                        
                        calendarViewModel.clearDraftTask() // Clear draft after saving
                        onSave(editedTask)
                    }
                }
            )
        }
        
        // Main content (identical to AddTaskScreen)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Add title", fontSize = 28.sp, color = Color(0xFF757575)) },
                textStyle = TextStyle(fontSize = 28.sp, color = Color.Black, fontWeight = FontWeight.Bold),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().background(Color.White)
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = getDrawableId("detail_icon")),
                    contentDescription = "Details icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = details,
                    onValueChange = { details = it },
                    placeholder = { Text("Add details", color = Color(0xFF757575)) },
                    textStyle = TextStyle(color = Color.Black),
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth().background(Color.White)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // All-day toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = getDrawableId("clock_icon")),
                    contentDescription = "Clock icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("All-day", color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                if (isAllDay) {
                    Image(
                        painter = painterResource(id = getDrawableId("yes_button")),
                        contentDescription = "Yes",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { isAllDay = false }
                    )
                } else {
                    Image(
                        painter = painterResource(id = getDrawableId("no_button")),
                        contentDescription = "No",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { isAllDay = true }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Date and time
            Row(verticalAlignment = Alignment.CenterVertically) {
                val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, d MMM", java.util.Locale.getDefault())
                Text(
                    text = taskDate.format(formatter),
                    color = Color.White,
                    fontSize = 16.sp
                )
                if (!isAllDay) {
                    Spacer(modifier = Modifier.width(16.dp))
                    var timeInput by remember { mutableStateOf("%02d:%02d".format(time.hour, time.minute)) }
                    TextField(
                        value = timeInput,
                        onValueChange = {
                            timeInput = it
                            val regex = Regex("^(\\d{1,2}):(\\d{1,2})$")
                            val match = regex.matchEntire(it)
                            if (match != null) {
                                val h = match.groupValues[1].toIntOrNull()
                                val m = match.groupValues[2].toIntOrNull()
                                if (h != null && m != null && h in 0..23 && m in 0..59) {
                                    // Round minute to nearest 5, 10, or 15
                                    val nearest = listOf(5, 10, 15).minByOrNull { kotlin.math.abs(m - it) } ?: 5
                                    val rounded = when {
                                        m % 15 == 0 -> m
                                        m % 10 == 0 -> m
                                        m % 5 == 0 -> m
                                        else -> nearest
                                    }
                                    time = java.time.LocalTime.of(h, rounded)
                                }
                            }
                        },
                        placeholder = { Text("HH:mm", color = Color(0xFF757575)) },
                        textStyle = TextStyle(color = Color.Black),
                        singleLine = true,
                        modifier = Modifier.width(100.dp).background(Color.White)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Repeat
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = getDrawableId("repeat_icon")),
                    contentDescription = "Repeat icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    // Get repeat end information from draft task
                    val currentRepeatEnd = draftTask?.repeatEnd ?: task.repeatEnd
                    val repeatEndText = when (currentRepeatEnd) {
                        is RepeatEnd.Never -> "does not end"
                        is RepeatEnd.UntilDate -> {
                            val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d yyyy", Locale.getDefault())
                            "ends on ${currentRepeatEnd.endDate.format(formatter)}"
                        }
                        is RepeatEnd.AfterOccurrences -> "ends after ${currentRepeatEnd.count} occurrences"
                    }
                    
                    val baseRepeatText = repeatOptions.firstOrNull { it.first == repeat }?.second ?: "Custom"
                    val fullRepeatText = if (repeat != RepeatFrequency.NONE) {
                        "$baseRepeatText, $repeatEndText"
                    } else {
                        baseRepeatText
                    }
                    
                    Text(
                        text = fullRepeatText,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .background(Color(0xFF333333), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { showRepeatDialog = true }
                    )
                    if (showRepeatDialog) {
                        Surface(
                            modifier = Modifier
                                .width(180.dp)
                                .background(Color(0xFF232326)),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF232326)
                        ) {
                            Column {
                                repeatOptions.forEach { (freq, label) ->
                                    Text(
                                        text = label,
                                        color = if (freq == repeat) Color(0xFF90CAF9) else Color.White,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (freq != null) {
                                                    repeat = freq
                                                    showRepeatDialog = false
                                                } else {
                                                    // Custom clicked
                                                    showRepeatDialog = false
                                                    if (repeat == RepeatFrequency.NONE) {
                                                        // Ignore custom if no base frequency chosen yet
                                                    } else {
                                                        // Save current draft before navigating
                                                        calendarViewModel.saveDraftTask(createCurrentDraft())
                                                        onCustomRecurrence(repeat)
                                                    }
                                                }
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Tag
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = getDrawableId("tag_icon")),
                    contentDescription = "Tag icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = tag,
                    onValueChange = { tag = it },
                    placeholder = { Text("Tag", color = Color(0xFF757575)) },
                    textStyle = TextStyle(color = Color.Black),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().background(Color.White)
                )
            }
        }
        // Warning dialog
        if (showWarning) {
            AlertDialog(
                onDismissRequest = { showWarning = false },
                title = { Text("Missing Info") },
                text = { Text("Please enter both a title and a tag.") },
                confirmButton = {
                    Button(onClick = { showWarning = false }) { Text("OK") }
                }
            )
        }
        // Cancel confirmation dialog
        if (showCancelConfirm) {
            AlertDialog(
                onDismissRequest = { showCancelConfirm = false },
                title = { Text("Cancel Task Editing") },
                text = { Text("Are you sure you want to cancel? Your changes will be lost.") },
                confirmButton = {
                    Button(onClick = {
                        showCancelConfirm = false
                        calendarViewModel.clearDraftTask() // Clear draft when canceling
                        onCancel()
                    }) { Text("Yes") }
                },
                dismissButton = {
                    Button(onClick = { showCancelConfirm = false }) { Text("No") }
                }
            )
        }
    }
}
