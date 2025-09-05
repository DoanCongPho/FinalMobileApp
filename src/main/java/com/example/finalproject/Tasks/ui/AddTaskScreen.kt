package com.example.dailyview.Tasks.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyview.Tasks.model.CalendarTask
import com.example.dailyview.Tasks.model.RepeatFrequency
import com.example.dailyview.Tasks.ui.getDrawableId
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random

@Composable
fun AddTaskScreen(
    onCancel: () -> Unit,
    onSave: (CalendarTask) -> Unit
) {
    // State for custom recurrence screen (must be inside composable)
    val showCustomRecurrence = remember { mutableStateOf(false) }
    val customFrequency = remember { mutableStateOf(RepeatFrequency.MONTHLY) }
    val customMonthlyPattern = remember { mutableStateOf<com.example.dailyview.Tasks.model.MonthlyPattern?>(null) }
    val customRepeatEnd = remember { mutableStateOf<com.example.dailyview.Tasks.model.RepeatEnd>(com.example.dailyview.Tasks.model.RepeatEnd.Never) }
    var title by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var isAllDay by remember { mutableStateOf(false) }
    var showCancelConfirm by remember { mutableStateOf(false) }
    var showWarning by remember { mutableStateOf(false) }
    var details by remember { mutableStateOf("") }
    val now = remember { LocalDate.now() }
    val nowTime = remember { LocalTime.now().withSecond(0).withNano(0) }
    var date by remember { mutableStateOf(now) }
    var time by remember { mutableStateOf(nowTime) }
    var repeat by remember { mutableStateOf(RepeatFrequency.NONE) }
    var showRepeatDialog by remember { mutableStateOf(false) }
    val repeatOptions = listOf(
        RepeatFrequency.NONE to "Does not repeat",
        RepeatFrequency.DAILY to "Every day",
        RepeatFrequency.WEEKLY to "Every week",
        RepeatFrequency.MONTHLY to "Every month",
        RepeatFrequency.YEARLY to "Every year",
        null to "Custom"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF232326))) {
        if (showCustomRecurrence.value) {
            CustomRecurrenceScreen(
                initialFrequency = customFrequency.value,
                initialMonthlyPattern = customMonthlyPattern.value,
                initialRepeatEnd = customRepeatEnd.value,
                onDone = { freq, monthlyPattern, repeatEnd ->
                    customFrequency.value = freq
                    customMonthlyPattern.value = monthlyPattern
                    customRepeatEnd.value = repeatEnd
                    repeat = freq
                    showCustomRecurrence.value = false
                },
                onBack = {
                    showCustomRecurrence.value = false
                }
            )
            return@Box
        }
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
                        onSave(
                            CalendarTask(
                                id = Random.nextInt().toString(),
                                title = title,
                                details = details.takeIf { it.isNotBlank() },
                                isAllDay = isAllDay,
                                date = date,
                                time = if (isAllDay) null else time,
                                repeatFrequency = repeat,
                                tag = tag
                            )
                        )
                    }
                }
            )
        }
        // Title input
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
                val formatter = DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.getDefault())
                Text(
                    text = date.format(formatter),
                    color = Color.White,
                    fontSize = 16.sp
                )
                if (!isAllDay) {
                    Spacer(modifier = Modifier.width(16.dp))
                    var timeInput by remember { mutableStateOf(String.format("%02d:%02d", time.hour, time.minute)) }
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
                                    val nearest = listOf(5, 10, 15).minByOrNull { Math.abs(m - it) } ?: 5
                                    val rounded = when {
                                        m % 15 == 0 -> m
                                        m % 10 == 0 -> m
                                        m % 5 == 0 -> m
                                        else -> nearest
                                    }
                                    time = LocalTime.of(h, rounded)
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
                    Text(
                        text = repeatOptions.firstOrNull { it.first == repeat }?.second ?: "Custom",
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
                                                    // Set customFrequency to current repeat value
                                                    customFrequency.value = repeat
                                                    showCustomRecurrence.value = true
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
                title = { Text("Cancel Task Creation") },
                text = { Text("Are you sure you want to cancel? Your changes will be lost.") },
                confirmButton = {
                    Button(onClick = {
                        showCancelConfirm = false
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
