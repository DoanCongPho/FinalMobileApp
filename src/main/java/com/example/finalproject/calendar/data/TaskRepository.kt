package com.example.finalproject.calendar.data

import androidx.compose.runtime.mutableStateListOf
import com.example.finalproject.Tasks.model.CalendarTask
import java.time.LocalDate


// Repository (singleton)
object CalendarRepository1 {
    private val tasks = mutableStateListOf<CalendarTask>()

    fun getTasks() = tasks

    suspend fun loadTasksFromApi() {
        tasks.clear()
        tasks.addAll(
            listOf(
                CalendarTask(
                    id = "1",
                    title = "Task 1",
                    date = LocalDate.now()
                ),
                CalendarTask(
                    id = "4",
                    title = "Task 4",
                    date = LocalDate.now()
                ),
                CalendarTask(
                    id = "5",
                    title = "Task 5",
                    date = LocalDate.now()
                ),
                CalendarTask(
                    id = "2",
                    title = "Task 2",
                    date = LocalDate.now().plusDays(1)
                ),
                CalendarTask(
                    id = "3",
                    title = "Task 3",
                    date = LocalDate.now().plusDays(2)
                )
            )
        )
    }
}

