package com.example.dailyview.Tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.example.dailyview.Tasks.model.CalendarTask
import java.time.LocalDate
import java.time.LocalTime

class TaskViewModel : ViewModel() {
    fun addTask(task: CalendarTask) {
        _tasks.add(task)
    }
    fun toggleTaskState(task: CalendarTask) {
        val idx = _tasks.indexOfFirst { it.id == task.id }
        if (idx != -1) {
            val current = _tasks[idx]
            _tasks[idx] = current.copy(state = if (current.state == 0) 1 else 0)
        }
    }
    // For now, use hardcoded tasks. Later, replace with API call.
    private val _tasks = mutableStateListOf(
        CalendarTask(
            id = "1",
            title = "Learn German",
            date = LocalDate.now(),
            time = LocalTime.of(1, 0)
        ),
        CalendarTask(
            id = "2",
            title = "Do Homework",
            date = LocalDate.now(),
            time = LocalTime.of(1, 10)
        ),
        CalendarTask(
            id = "3",
            title = "Meeting",
            date = LocalDate.now(),
            time = LocalTime.of(9, 30),
            tag = "Mobile Project",
            details = "Discuss the roles"
        ),
        CalendarTask(
            id = "4",
            title = "Learn IELTS",
            date = LocalDate.now(),
            time = LocalTime.of(11, 10)
        ),
        CalendarTask(
            id = "5",
            title = "Learn writing",
            date = LocalDate.now(),
            time = LocalTime.of(11, 30)
        ),
        CalendarTask(
            id = "6",
            title = "Learn speaking",
            date = LocalDate.now(),
            time = LocalTime.of(11, 40)
        ),
        CalendarTask(
            id = "7",
            title = "Learn listening cambridge, Reading simulation, Reading full 3 passages",
            date = LocalDate.now(),
            time = LocalTime.of(23, 30)
        )
    )

    val tasks: List<CalendarTask> get() = _tasks

    fun deleteTask(task: CalendarTask) {
        _tasks.remove(task)
    }
}