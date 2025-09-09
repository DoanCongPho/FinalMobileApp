package com.example.finalproject.calendar.data

import androidx.compose.runtime.mutableStateListOf
import com.example.finalproject.Tasks.model.CalendarTask
import java.time.LocalDate
import java.time.LocalTime


// Repository (singleton)
object CalendarRepository1 {
    private val tasks = mutableStateListOf<CalendarTask>()

    fun getTasks() = tasks

    fun addTask(task: CalendarTask) {
        tasks.add(task)
    }

    fun removeTask(task: CalendarTask) {
        tasks.remove(task)
    }

    fun updateTask(updatedTask: CalendarTask) {
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            tasks[index] = updatedTask
        }
    }

    suspend fun loadTasksFromApi() {
        tasks.clear()
        tasks.addAll(
            listOf(
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
                ),
                CalendarTask(
                    id = "8",
                    title = "Task 1",
                    date = LocalDate.now(),
                    time = LocalTime.of(1, 0)

                ),
                CalendarTask(
                    id = "9",
                    title = "Task 4",
                    date = LocalDate.now(),
                    time = LocalTime.of(2, 0)
                ),
                CalendarTask(
                    id = "10",
                    title = "Task 5",
                    date = LocalDate.now(),
                    time = LocalTime.of(3, 0)
                ),
                CalendarTask(
                    id = "11",
                    title = "Task 2",
                    date = LocalDate.now().plusDays(1),
                    time = LocalTime.of(4, 0)
                ),
                CalendarTask(
                    id = "12",
                    title = "Task 3",
                    date = LocalDate.now().plusDays(2),
                    time = LocalTime.of(5, 0)
                )
            )
        )
    }
}

