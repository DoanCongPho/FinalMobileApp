package com.example.finalproject.Tasks.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.Tasks.model.MonthlyPattern
import com.example.finalproject.Tasks.model.RepeatEnd
import com.example.finalproject.Tasks.model.RepeatFrequency
import com.example.finalproject.Tasks.ui.getDrawableId
import java.time.format.DateTimeFormatter
import java.util.*

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRecurrenceScreen(
    initialFrequency: RepeatFrequency,
    initialMonthlyPattern: MonthlyPattern?,
    initialRepeatEnd: RepeatEnd,
    onDone: (RepeatFrequency, MonthlyPattern?, RepeatEnd) -> Unit,
    onBack: () -> Unit,
    calendarViewModel: com.example.finalproject.calendar.viewmodel.CalendarViewModel1,
    onNavigateToEnds: () -> Unit
) {
    // Frequency is fixed for the lifetime of this screen (cannot be changed here)
    val frequency = remember(initialFrequency) { initialFrequency }
    // Get the current repeat end from draft task or use initial
    val draftTask = calendarViewModel.draftTask
    var repeatEnd by remember { 
        mutableStateOf(draftTask?.repeatEnd ?: initialRepeatEnd) 
    }
    // Only hold a monthly pattern if the frequency is MONTHLY
    var monthlyPattern by remember(frequency) {
        mutableStateOf(
            if (frequency == RepeatFrequency.MONTHLY) {
                initialMonthlyPattern ?: MonthlyPattern.SameDay(1)
            } else null
        )
    }
    var showWarning by remember { mutableStateOf(false) }
    var hasChanges by remember {
        mutableStateOf(false)
    }
    
    // Update repeatEnd when draft task changes (e.g., coming back from EndsScreen)
    LaunchedEffect(draftTask?.repeatEnd) {
        draftTask?.repeatEnd?.let { newRepeatEnd ->
            repeatEnd = newRepeatEnd
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Custom Recurrence",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = getDrawableId("back_button")),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                if (hasChanges) showWarning = true else onBack()
                            }
                            .padding(start = 8.dp)
                    )
                },
                actions = {
                    Text(
                        text = "Done",
                        color = Color(0xFF1976D2),
                        fontSize = 18.sp,
                        modifier = Modifier
                            .clickable {
                                // Update the draft task with new repeat settings
                                calendarViewModel.updateDraftTask { draft ->
                                    draft.copy(
                                        repeatFrequency = frequency,
                                        monthlyPattern = monthlyPattern
                                    )
                                }
                                onDone(frequency, monthlyPattern, repeatEnd)
                            }
                            .padding(end = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Only show monthly pattern options if frequency is MONTHLY
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

            // Repeat end option (always shown)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { onNavigateToEnds() }
            ) {
                Image(
                    painter = painterResource(id = getDrawableId("repeat_end")),
                    contentDescription = "Repeat End",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val currentRepeatEnd = repeatEnd // Create local variable for smart casting
                val repeatEndText = when (currentRepeatEnd) {
                    is RepeatEnd.Never -> "Does not end"
                    is RepeatEnd.UntilDate -> {
                        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d yyyy", Locale.getDefault())
                        "Ends on ${currentRepeatEnd.endDate.format(formatter)}"
                    }
                    is RepeatEnd.AfterOccurrences -> "Ends after ${currentRepeatEnd.count} occurrences"
                }
                Text(
                    text = repeatEndText,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onNavigateToEnds()
                        }
                )
                Image(
                    painter = painterResource(id = getDrawableId("expand_right_button")),
                    contentDescription = "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onNavigateToEnds()
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
