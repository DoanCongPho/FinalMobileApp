
package com.example.dailyview.Tasks.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyview.Tasks.model.MonthlyPattern
import com.example.dailyview.Tasks.model.RepeatEnd
import com.example.dailyview.Tasks.model.RepeatFrequency
import com.example.dailyview.Tasks.ui.getDrawableId

// Helper function for ordinal numbers
fun ordinal(n: Int): String {
    return when (n) {
        1 -> "first"
        2 -> "second"
        3 -> "third"
        4 -> "fourth"
        5 -> "fifth"
        else -> "$n-th"
    }
}

@Composable
fun CustomRecurrenceScreen(
    initialFrequency: RepeatFrequency,
    initialMonthlyPattern: MonthlyPattern?,
    initialRepeatEnd: RepeatEnd,
    onDone: (RepeatFrequency, MonthlyPattern?, RepeatEnd) -> Unit,
    onBack: () -> Unit
) {
    var frequency by remember { mutableStateOf(initialFrequency) }
    var monthlyPattern by remember {
        mutableStateOf(initialMonthlyPattern ?: MonthlyPattern.SameDay(1))
    }
    var repeatEnd by remember { mutableStateOf(initialRepeatEnd) }
    var showWarning by remember { mutableStateOf(false) }
    var hasChanges by remember {
        mutableStateOf(false)
    }

    Surface(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = getDrawableId("back_button")),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            if (hasChanges) showWarning = true else onBack()
                        }
                )
                Text(
                    text = "Back",
                    color = Color(0xFF1976D2),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .clickable {
                            if (hasChanges) showWarning = true else onBack()
                        }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Custom Recurrence",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Done",
                    color = Color(0xFF1976D2),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable {
                            onDone(frequency, monthlyPattern, repeatEnd)
                        }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Monthly pattern options
            if (frequency == RepeatFrequency.MONTHLY) {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    // Option 1: Same day each month
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "On the same day each month",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        if (monthlyPattern is MonthlyPattern.SameDay) {
                            Image(
                                painter = painterResource(id = getDrawableId("yes_button")),
                                contentDescription = "Selected",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    monthlyPattern = MonthlyPattern.SameDay(1)
                                    hasChanges = true
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Option 2: Nth weekday each month
                    val nth = 1 // Example: first
                    val weekday = java.time.DayOfWeek.FRIDAY // Example: Friday
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "On every ${ordinal(nth)} ${weekday.name.lowercase().replaceFirstChar { it.uppercase() }}",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        if (monthlyPattern is MonthlyPattern.NthWeekday) {
                            Image(
                                painter = painterResource(id = getDrawableId("yes_button")),
                                contentDescription = "Selected",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    monthlyPattern = MonthlyPattern.NthWeekday(nth, weekday)
                                    hasChanges = true
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Repeat end option
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = getDrawableId("repeat_end")),
                    contentDescription = "Repeat End",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val repeatEndText = when (repeatEnd) {
                    is RepeatEnd.Never -> "Does not end"
                    is RepeatEnd.UntilDate -> "On a date"
                    is RepeatEnd.AfterOccurrences -> "After a number of occurrences"
                }
                Text(
                    text = repeatEndText,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // TODO: Handle changing repeat end type
                        }
                )
                Image(
                    painter = painterResource(id = getDrawableId("expand_right_button")),
                    contentDescription = "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            // TODO: Handle changing repeat end type
                        }
                )
            }

            // Warning dialog for unsaved changes
            if (showWarning) {
                AlertDialog(
                    onDismissRequest = { showWarning = false },
                    title = { Text("Unsaved Changes") },
                    text = { Text("You have unsaved changes. Are you sure you want to go back?") },
                    confirmButton = {
                        Button(onClick = {
                            showWarning = false
                            onBack()
                        }) { Text("Discard Changes") }
                    },
                    dismissButton = {
                        Button(onClick = { showWarning = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}
