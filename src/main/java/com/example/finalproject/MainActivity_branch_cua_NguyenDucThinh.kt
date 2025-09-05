package com.example.dailyview
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.example.dailyview.Tasks.model.CalendarTask

import com.example.dailyview.Tasks.ui.TaskDetailScreen
import com.example.dailyview.Tasks.ui.AddTaskScreen


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyview.Tasks.viewmodel.TaskViewModel
import com.example.dailyview.Tasks.ui.DailySchedule
import com.example.dailyview.Tasks.ui.TaskDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val taskViewModel: TaskViewModel = viewModel()
    var selectedTask by remember { mutableStateOf<CalendarTask?>(null) }
    var showDetail by remember { mutableStateOf(false) }

    var showAdd by remember { mutableStateOf(false) }

    when {
        showDetail && selectedTask != null -> {
            // Find the latest version of the selected task (for real-time updates)
            val currentTask = taskViewModel.tasks.find { it.id == selectedTask!!.id } ?: selectedTask!!
            TaskDetailScreen(
                task = currentTask,
                onClose = { showDetail = false },
                onEdit = { /* TODO: Launch edit activity */ },
                onDelete = { task ->
                    taskViewModel.deleteTask(task)
                    showDetail = false
                },
                onToggleState = { task ->
                    taskViewModel.toggleTaskState(task)
                }
            )
        }
        showAdd -> {
            AddTaskScreen(
                onCancel = { showAdd = false },
                onSave = { newTask ->
                    taskViewModel.addTask(newTask)
                    showAdd = false
                }
            )
        }
        else -> {
            val today = java.time.LocalDate.now()
            DailySchedule(
                tasks = taskViewModel.tasks,
                date = today,
                onTaskClick = { task ->
                    selectedTask = task
                    showDetail = true
                },
                onAddClick = { showAdd = true }
            )
        }
    }
}