package com.example.finalproject.calendar.data

import androidx.compose.runtime.mutableStateListOf
import com.example.finalproject.Tasks.model.CalendarTask
import com.example.finalproject.Tasks.model.RepeatFrequency
import com.example.finalproject.Tasks.model.RepeatEnd
import com.example.finalproject.Tasks.model.MonthlyPattern
import java.time.LocalDate
import java.time.LocalTime
import java.time.DayOfWeek
import java.util.UUID


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

    // Delete all tasks in a series (including the root task)
    fun deleteTaskSeries(seriesId: String) {
        tasks.removeAll { it.seriesId == seriesId }
    }
    
    // Delete a specific task (for single task removal)
    fun deleteTask(taskToDelete: CalendarTask) {
        tasks.removeAll { it.id == taskToDelete.id }
    }

    // Create a series of recurring tasks
    fun createTaskSeries(rootTask: CalendarTask) {
        if (rootTask.repeatFrequency == RepeatFrequency.NONE) return
        
        when (rootTask.repeatFrequency) {
            RepeatFrequency.DAILY -> repeatDaily(rootTask)
            RepeatFrequency.WEEKLY -> repeatWeekly(rootTask)
            RepeatFrequency.MONTHLY -> repeatMonthly(rootTask)
            RepeatFrequency.YEARLY -> repeatYearly(rootTask)
            RepeatFrequency.NONE -> {} // Already handled above
        }
    }

    // Update all tasks in a series with new information (except date/time which stays the same)
    fun updateTaskSeries(rootTask: CalendarTask) {
        if (rootTask.seriesId == null) return
        
        val tasksToUpdate = tasks.filter { it.seriesId == rootTask.seriesId }
        tasksToUpdate.forEach { existingTask ->
            val updatedTask = existingTask.copy(
                title = rootTask.title,
                details = rootTask.details,
                isAllDay = rootTask.isAllDay,
                // Keep original date and time for each instance
                tag = rootTask.tag,
                // Keep original state for each instance
                repeatFrequency = rootTask.repeatFrequency,
                monthlyPattern = rootTask.monthlyPattern,
                repeatEnd = rootTask.repeatEnd
            )
            updateTask(updatedTask)
        }
    }

    private fun repeatDaily(rootTask: CalendarTask) {
        val endDate = calculateEndDate(rootTask)
        var currentDate = rootTask.date.plusDays(1) // Start from next day
        var occurrenceCount = 1 // Root task is the first occurrence
        
        while (currentDate <= endDate && shouldContinue(rootTask.repeatEnd, occurrenceCount)) {
            val newTask = rootTask.copy(
                id = UUID.randomUUID().toString(),
                date = currentDate,
                state = 0 // Reset completion state for new instances
            )
            addTask(newTask)
            
            currentDate = currentDate.plusDays(1)
            occurrenceCount++
        }
    }

    private fun repeatWeekly(rootTask: CalendarTask) {
        val endDate = calculateEndDate(rootTask)
        var currentDate = rootTask.date.plusWeeks(1) // Start from next week
        var occurrenceCount = 1 // Root task is the first occurrence
        
        while (currentDate <= endDate && shouldContinue(rootTask.repeatEnd, occurrenceCount)) {
            val newTask = rootTask.copy(
                id = UUID.randomUUID().toString(),
                date = currentDate,
                state = 0 // Reset completion state for new instances
            )
            addTask(newTask)
            
            currentDate = currentDate.plusWeeks(1)
            occurrenceCount++
        }
    }

    private fun repeatMonthly(rootTask: CalendarTask) {
        val endDate = calculateEndDate(rootTask)
        var currentDate = rootTask.date
        var occurrenceCount = 1 // Root task is the first occurrence
        
        while (occurrenceCount < 100 && shouldContinue(rootTask.repeatEnd, occurrenceCount)) { // Safety limit
            currentDate = calculateNextMonthlyDate(currentDate, rootTask.monthlyPattern)
            
            if (currentDate > endDate) break
            
            val newTask = rootTask.copy(
                id = UUID.randomUUID().toString(),
                date = currentDate,
                state = 0 // Reset completion state for new instances
            )
            addTask(newTask)
            
            occurrenceCount++
        }
    }

    private fun repeatYearly(rootTask: CalendarTask) {
        val endDate = calculateEndDate(rootTask)
        var currentDate = rootTask.date.plusYears(1) // Start from next year
        var occurrenceCount = 1 // Root task is the first occurrence
        
        while (currentDate <= endDate && shouldContinue(rootTask.repeatEnd, occurrenceCount)) {
            val newTask = rootTask.copy(
                id = UUID.randomUUID().toString(),
                date = currentDate,
                state = 0 // Reset completion state for new instances
            )
            addTask(newTask)
            
            currentDate = currentDate.plusYears(1)
            occurrenceCount++
        }
    }

    private fun calculateEndDate(rootTask: CalendarTask): LocalDate {
        return when (rootTask.repeatEnd) {
            is RepeatEnd.Never -> LocalDate.now().plusYears(1) // Default to next year
            is RepeatEnd.UntilDate -> rootTask.repeatEnd.endDate
            is RepeatEnd.AfterOccurrences -> LocalDate.now().plusYears(2) // Generous limit for occurrence counting
        }
    }

    private fun shouldContinue(repeatEnd: RepeatEnd, occurrenceCount: Int): Boolean {
        return when (repeatEnd) {
            is RepeatEnd.Never -> true
            is RepeatEnd.UntilDate -> true // Date check is done in the calling function
            is RepeatEnd.AfterOccurrences -> occurrenceCount < repeatEnd.count
        }
    }

    private fun calculateNextMonthlyDate(currentDate: LocalDate, monthlyPattern: MonthlyPattern?): LocalDate {
        return when (monthlyPattern) {
            is MonthlyPattern.SameDay -> {
                val nextMonth = currentDate.plusMonths(1)
                try {
                    nextMonth.withDayOfMonth(monthlyPattern.dayOfMonth)
                } catch (e: Exception) {
                    // Handle case where day doesn't exist in target month (e.g., Jan 31 -> Feb 31)
                    nextMonth.withDayOfMonth(nextMonth.lengthOfMonth().coerceAtMost(monthlyPattern.dayOfMonth))
                }
            }
            is MonthlyPattern.NthWeekday -> {
                calculateNthWeekdayOfMonth(currentDate.plusMonths(1), monthlyPattern.weekOfMonth, monthlyPattern.dayOfWeek)
            }
            null -> currentDate.plusMonths(1) // Fallback to same day next month
        }
    }

    private fun calculateNthWeekdayOfMonth(date: LocalDate, weekOfMonth: Int, dayOfWeek: DayOfWeek): LocalDate {
        val firstDayOfMonth = date.withDayOfMonth(1)
        val firstOccurrence = firstDayOfMonth.with(java.time.temporal.TemporalAdjusters.nextOrSame(dayOfWeek))
        val targetDate = firstOccurrence.plusWeeks((weekOfMonth - 1).toLong())
        
        // If the calculated date is beyond the month, use the last occurrence
        return if (targetDate.month == date.month) {
            targetDate
        } else {
            firstOccurrence.plusWeeks((weekOfMonth - 2).toLong()) // Use previous week
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
                )
//                CalendarTask(
//                    id = "2",
//                    title = "Do Homework",
//                    date = LocalDate.now(),
//                    time = LocalTime.of(1, 10)
//                ),
//                CalendarTask(
//                    id = "3",
//                    title = "Meeting",
//                    date = LocalDate.now(),
//                    time = LocalTime.of(9, 30),
//                    tag = "Mobile Project",
//                    details = "Discuss the roles"
//                ),
//                CalendarTask(
//                    id = "4",
//                    title = "Learn IELTS",
//                    date = LocalDate.now(),
//                    time = LocalTime.of(11, 10)
//                ),
//                CalendarTask(
//                    id = "5",
//                    title = "Learn writing",
//                    date = LocalDate.now(),
//                    time = LocalTime.of(11, 30)
//                ),
//                CalendarTask(
//                    id = "6",
//                    title = "Learn speaking",
//                    date = LocalDate.now(),
//                    time = LocalTime.of(11, 40)
//                ),
//                CalendarTask(
//                    id = "7",
//                    title = "Learn listening cambridge, Reading simulation, Reading full 3 passages",
//                    date = LocalDate.now(),
//                    time = LocalTime.of(23, 30)
//                ),
//                CalendarTask(
//                    id = "8",
//                    title = "Task 1",
//                    date = LocalDate.now(),
//                    time = LocalTime.of(1, 0)
//
//                ),
//                CalendarTask(
//                    id = "9",
//                    title = "Task 4",
//                    date = LocalDate.now(),
//                    time = LocalTime.of(2, 0)
//                ),
//                CalendarTask(
//                    id = "10",
//                    title = "Task 5",
//                    date = LocalDate.now(),
//                    time = LocalTime.of(3, 0)
//                ),
//                CalendarTask(
//                    id = "11",
//                    title = "Task 2",
//                    date = LocalDate.now().plusDays(1),
//                    time = LocalTime.of(4, 0)
//                ),
//                CalendarTask(
//                    id = "12",
//                    title = "Task 3",
//                    date = LocalDate.now().plusDays(2),
//                    time = LocalTime.of(5, 0)
//                )
            )
        )
    }
}

