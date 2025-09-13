package com.example.finalproject.calendar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.calendar.viewmodel.CalendarViewModel
import com.example.finalproject.Tasks.model.CalendarTask
import com.example.finalproject.Tasks.viewmodel.TaskViewModel
import com.example.finalproject.main.ui.BottomNavigationBar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    taskViewModel: TaskViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Use remember to trigger recomposition when tasks change
    val tasks by remember(taskViewModel) { 
        derivedStateOf { taskViewModel.tasks.toList() }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.selectedDate.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()) + 
                              " " + uiState.selectedDate.year,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                val taskDates = tasks.map { it.date }
                
                CalendarGrid(
                    selectedDate = uiState.selectedDate.toLocalDate(),
                    onDateSelect = { /* Handle date selection */ },
                    events = taskDates
                )
            }

            // Today's tasks section
            item {
                val todayDateString = LocalDateTime.now().toLocalDate().toString()
                val todayTasks = tasks.filter { it.date == LocalDateTime.now().toLocalDate() }
                
                Text(
                    text = "Today (${todayTasks.size} tasks)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("daily_schedule/$todayDateString")
                        }
                )
            }

            items(tasks.filter { it.date == LocalDateTime.now().toLocalDate() }) { task ->
                TaskEventItem(
                    task = task,
                    onClick = {
                        val taskDateString = task.date.toString()
                        navController.navigate("daily_schedule/$taskDateString")
                    },
                    onToggleComplete = { taskViewModel.toggleTaskState(task) }
                )
            }

            // Tomorrow's tasks section
            item {
                val tomorrowDateString = LocalDateTime.now().plusDays(1).toLocalDate().toString()
                val tomorrowTasks = tasks.filter { it.date == LocalDateTime.now().plusDays(1).toLocalDate() }
                
                Text(
                    text = "Tomorrow (${tomorrowTasks.size} tasks)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp)
                        .clickable {
                            navController.navigate("daily_schedule/$tomorrowDateString")
                        }
                )
            }

            items(tasks.filter { it.date == LocalDateTime.now().plusDays(1).toLocalDate() }) { task ->
                TaskEventItem(
                    task = task,
                    onClick = {
                        val taskDateString = task.date.toString()
                        navController.navigate("daily_schedule/$taskDateString")
                    },
                    onToggleComplete = { taskViewModel.toggleTaskState(task) }
                )
            }
        }
    }
}

@Composable
fun TaskEventItem(
    task: CalendarTask, 
    onClick: (() -> Unit)? = null,
    onToggleComplete: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(12.dp),
        color = if (task.state == 1) Color(0xFFE8F5E8) else Color(0xFFEFEBF2) // Green tint for completed tasks
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox for task completion
                Checkbox(
                    checked = task.state == 1,
                    onCheckedChange = { onToggleComplete?.invoke() },
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                Column {
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (task.state == 1) Color.Gray else Color.Black
                    )
                    
                    Row {
                        if (task.time != null) {
                            Text(
                                text = task.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        } else if (task.isAllDay) {
                            Text(
                                text = "All day",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        
                        if (task.tag != null) {
                            Text(
                                text = " • ${task.tag}",
                                fontSize = 14.sp,
                                color = Color(0xFF6B4EFF)
                            )
                        }
                    }
                    
                    if (task.details != null) {
                        Text(
                            text = task.details,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Task status icon
            Icon(
                imageVector = if (task.state == 1) Icons.Default.CheckCircle else Icons.Default.Circle,
                contentDescription = if (task.state == 1) "Completed" else "Pending",
                modifier = Modifier.size(24.dp),
                tint = if (task.state == 1) Color(0xFF4CAF50) else Color.Gray
            )
        }
    }
}
