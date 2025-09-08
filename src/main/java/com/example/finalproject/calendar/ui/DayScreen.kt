package com.example.finalproject.calendar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.finalproject.Tasks.model.CalendarTask
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    date: LocalDate,
    viewModel: CalendarViewModel1,
    onDateChange: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val tasks = viewModel.tasks.filter { it.date == date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Day View - ${date.dayOfMonth}/${date.monthValue}/${date.year}") },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (tasks.isEmpty()) {
                Text("No tasks for this day")
            } else {
                tasks.forEach { task ->
                    Text("• ${task.title}")
                }
            }
        }
    }

    // DatePickerDialog đúng cách
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val pickedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        onDateChange(pickedDate)
                    }
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


