package com.example.finalproject.calendar.data

import androidx.compose.runtime.mutableStateListOf
import com.example.finalproject.Tasks.model.CalendarTask
import com.example.finalproject.Tasks.model.CalendarTasklist
import com.example.finalproject.Tasks.model.RepeatFrequency
import com.example.finalproject.Tasks.model.RepeatEnd
import com.example.finalproject.Tasks.model.MonthlyPattern
import com.example.finalproject.core.network.api.tasklist_task.TaskListApi
import com.example.finalproject.core.network.api.tasklist_task.TaskListRequest
import com.example.finalproject.core.network.api.tasklist_task.TaskListResponse
import com.example.finalproject.core.network.api.tasklist_task.TaskApi
import com.example.finalproject.core.network.api.tasklist_task.TaskResponse
import com.example.finalproject.core.network.api.tasklist_task.CreateTaskRequest
import com.example.finalproject.core.network.api.tasklist_task.UpdateTaskRequest
import com.example.finalproject.core.network.api.tasklist_task.TaskRecurrence
import com.example.finalproject.core.network.api.tasklist_task.RecurrencePattern
import com.example.finalproject.core.network.api.tasklist_task.RecurrenceRange
import android.util.Log
import java.time.LocalDate
import java.time.LocalTime
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


// Repository (singleton)
object CalendarRepository1 {
    private val tasks = mutableStateListOf<CalendarTask>()
    private val taskLists = mutableStateListOf<CalendarTasklist>()
    
    private var taskListApi: TaskListApi? = null
    private var taskApi: TaskApi? = null

    fun getTasks() = tasks
    fun getTaskLists() = taskLists
    
    // Initialize the API instances
    fun setTaskListApi(api: TaskListApi) {
        taskListApi = api
    }
    
    fun setTaskApi(api: TaskApi) {
        taskApi = api
    }

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
    suspend fun createTaskSeries(rootTask: CalendarTask) {
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
    suspend fun updateTaskSeries(rootTask: CalendarTask) {
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
            
            // Try to update via API first, fallback to local if API fails
            val apiUpdatedTask = updateTaskInApi(updatedTask)
            if (apiUpdatedTask != null) {
                updateTask(apiUpdatedTask)
            } else {
                // API failed, update locally
                updateTask(updatedTask)
            }
        }
    }

    private suspend fun repeatDaily(rootTask: CalendarTask) {
        val endDate = calculateEndDate(rootTask)
        var currentDate = rootTask.date.plusDays(1) // Start from next day
        var occurrenceCount = 1 // Root task is the first occurrence
        
        while (currentDate <= endDate && shouldContinue(rootTask.repeatEnd, occurrenceCount)) {
            val newTask = rootTask.copy(
                id = UUID.randomUUID().toString(),
                date = currentDate,
                state = 0 // Reset completion state for new instances
            )
            
            // Try to create via API first, fallback to local if API fails
            val createdTask = createTaskInApi(newTask)
            if (createdTask != null) {
                addTask(createdTask)
            } else {
                // API failed, add locally
                addTask(newTask)
            }
            
            currentDate = currentDate.plusDays(1)
            occurrenceCount++
        }
    }

    private suspend fun repeatWeekly(rootTask: CalendarTask) {
        val endDate = calculateEndDate(rootTask)
        var currentDate = rootTask.date.plusWeeks(1) // Start from next week
        var occurrenceCount = 1 // Root task is the first occurrence
        
        while (currentDate <= endDate && shouldContinue(rootTask.repeatEnd, occurrenceCount)) {
            val newTask = rootTask.copy(
                id = UUID.randomUUID().toString(),
                date = currentDate,
                state = 0
            )
            
            // Try to create via API first, fallback to local if API fails
            val createdTask = createTaskInApi(newTask)
            if (createdTask != null) {
                addTask(createdTask)
            } else {
                // API failed, add locally
                addTask(newTask)
            }
            
            currentDate = currentDate.plusWeeks(1)
            occurrenceCount++
        }
    }

    private suspend fun repeatMonthly(rootTask: CalendarTask) {
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
            
            // Try to create via API first, fallback to local if API fails
            val createdTask = createTaskInApi(newTask)
            if (createdTask != null) {
                addTask(createdTask)
            } else {
                // API failed, add locally
                addTask(newTask)
            }
            
            occurrenceCount++
        }
    }

    private suspend fun repeatYearly(rootTask: CalendarTask) {
        val endDate = calculateEndDate(rootTask)
        var currentDate = rootTask.date.plusYears(1) // Start from next year
        var occurrenceCount = 1 // Root task is the first occurrence
        
        while (currentDate <= endDate && shouldContinue(rootTask.repeatEnd, occurrenceCount)) {
            val newTask = rootTask.copy(
                id = UUID.randomUUID().toString(),
                date = currentDate,
                state = 0
            )
            
            // Try to create via API first, fallback to local if API fails
            val createdTask = createTaskInApi(newTask)
            if (createdTask != null) {
                addTask(createdTask)
            } else {
                // API failed, add locally
                addTask(newTask)
            }
            
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
        Log.d("CalendarRepository1", "Starting to load tasks from API...")
        
        // Clear all existing tasks
        tasks.clear()
        
        // First, load all task lists to ensure we have them locally
        loadTaskListsFromApi()
        Log.d("CalendarRepository1", "Task lists loaded: ${taskLists.size} lists")
        
        // Then load tasks from each task list
        val allTasks = mutableListOf<CalendarTask>()
        taskLists.forEach { taskList ->
            try {
                Log.d("CalendarRepository1", "Loading tasks from task list: ${taskList.task_list_name} (ID: ${taskList.task_list_id})")
                val tasksFromList = getTasksFromApi(taskList.task_list_id)
                allTasks.addAll(tasksFromList)
                Log.d("CalendarRepository1", "Added ${tasksFromList.size} tasks from task list ${taskList.task_list_name}")
            } catch (e: Exception) {
                Log.e("CalendarRepository1", "Error loading tasks from task list ${taskList.task_list_id}: ${e.message}")
                e.printStackTrace()
            }
        }
        
        // Add all loaded tasks to local memory
        tasks.addAll(allTasks)
        Log.d("CalendarRepository1", "Completed loading tasks: ${allTasks.size} tasks from ${taskLists.size} task lists")
        
        // Log the first few tasks for debugging
        allTasks.take(3).forEach { task ->
            Log.d("CalendarRepository1", "Sample task: ${task.title} (Date: ${task.date}, Task List: ${task.task_list_id})")
        }
    }
    
    // ==============================================
    // TASK LISTS MANAGEMENT
    // ==============================================
    
    // ===== LOCAL DATA MANAGEMENT FUNCTIONS =====
    
    private fun addTaskListLocal(taskList: CalendarTasklist) {
        taskLists.add(taskList)
    }
    
    private fun removeTaskListLocal(taskListId: Int) {
        taskLists.removeAll { it.task_list_id == taskListId }
    }
    
    private fun updateTaskListLocal(updatedTaskList: CalendarTasklist) {
        val index = taskLists.indexOfFirst { it.task_list_id == updatedTaskList.task_list_id }
        if (index != -1) {
            taskLists[index] = updatedTaskList
        }
    }
    
    private fun setTaskListsLocal(taskListsFromApi: List<CalendarTasklist>) {
        taskLists.clear()
        taskLists.addAll(taskListsFromApi)
    }
    
    // ===== API INTERACTION FUNCTIONS =====
    
    private suspend fun getUserTaskListsFromApi(): List<CalendarTasklist> {
        return try {
            val response = taskListApi?.getUserTaskLists()
            if (response?.isSuccessful == true) {
                response.body()?.map { apiTaskList ->
                    CalendarTasklist(
                        task_list_id = apiTaskList.id,
                        task_list_name = apiTaskList.name
                    )
                } ?: emptyList()
            } else {
                Log.e("CalendarRepository1", "Failed to get task lists: ${response?.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error getting task lists: ${e.message}")
            emptyList()
        }
    }
    
    private suspend fun getTaskListFromApi(taskListId: Int): CalendarTasklist? {
        return try {
            val response = taskListApi?.getTaskList(taskListId)
            if (response?.isSuccessful == true) {
                response.body()?.let { apiTaskList ->
                    CalendarTasklist(
                        task_list_id = apiTaskList.id,
                        task_list_name = apiTaskList.name
                    )
                }
            } else {
                Log.e("CalendarRepository1", "Failed to get task list: ${response?.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error getting task list: ${e.message}")
            null
        }
    }
    
    private suspend fun createTaskListInApi(name: String): CalendarTasklist? {
        return try {
            val request = TaskListRequest(name = name)
            val response = taskListApi?.createTaskList(request)
            if (response?.isSuccessful == true) {
                response.body()?.let { apiTaskList ->
                    CalendarTasklist(
                        task_list_id = apiTaskList.id,
                        task_list_name = apiTaskList.name
                    )
                }
            } else {
                Log.e("CalendarRepository1", "Failed to create task list: ${response?.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error creating task list: ${e.message}")
            null
        }
    }
    
    private suspend fun updateTaskListInApi(taskListId: Int, name: String): CalendarTasklist? {
        return try {
            val request = TaskListRequest(name = name)
            val response = taskListApi?.updateTaskList(taskListId, request)
            if (response?.isSuccessful == true) {
                response.body()?.let { apiTaskList ->
                    CalendarTasklist(
                        task_list_id = apiTaskList.id,
                        task_list_name = apiTaskList.name
                    )
                }
            } else {
                Log.e("CalendarRepository1", "Failed to update task list: ${response?.code()}")
                response?.errorBody()?.string()?.let { errorBody ->
                    Log.e("CalendarRepository1", "Error body: $errorBody")
                }
                null
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error updating task list: ${e.message}")
            e.printStackTrace()
            null
        }
    }
    
    private suspend fun deleteTaskListFromApi(taskListId: Int): Boolean {
        return try {
            val response = taskListApi?.deleteTaskList(taskListId)
            if (response?.isSuccessful == true) {
                true
            } else {
                Log.e("CalendarRepository1", "Failed to delete task list: ${response?.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error deleting task list: ${e.message}")
            false
        }
    }
    
    // ===== COMBINED FUNCTIONS (API + LOCAL) =====
    
    suspend fun loadTaskListsFromApi() {
        val taskListsFromApi = getUserTaskListsFromApi()
        setTaskListsLocal(taskListsFromApi)
    }
    
    suspend fun createTaskList(name: String): Boolean {
        val createdTaskList = createTaskListInApi(name)
        return if (createdTaskList != null) {
            addTaskListLocal(createdTaskList)
            true
        } else {
            false
        }
    }
    
    suspend fun updateTaskList(taskListId: Int, name: String): Boolean {
        val updatedTaskList = updateTaskListInApi(taskListId, name)
        return if (updatedTaskList != null) {
            updateTaskListLocal(updatedTaskList)
            true
        } else {
            false
        }
    }
    
    suspend fun deleteTaskList(taskListId: Int): Boolean {
        val success = deleteTaskListFromApi(taskListId)
        if (success) {
            removeTaskListLocal(taskListId)
        }
        return success
    }
    
    suspend fun getTaskList(taskListId: Int): CalendarTasklist? {
        // First try to get from local
        val localTaskList = taskLists.find { it.task_list_id == taskListId }
        return if (localTaskList != null) {
            localTaskList
        } else {
            // If not found locally, try to get from API and add to local
            val apiTaskList = getTaskListFromApi(taskListId)
            if (apiTaskList != null) {
                addTaskListLocal(apiTaskList)
            }
            apiTaskList
        }
    }
    
    // ==============================================
    // TASK API FUNCTIONS
    // ==============================================
    
    // ===== FIELD MAPPING HELPER FUNCTIONS =====
    
    private fun mapCalendarTaskToCreateRequest(task: CalendarTask): CreateTaskRequest {
        return CreateTaskRequest(
            title = task.title,
            description = task.details,
            due_at = formatDueAt(task),
            completed = task.state == 1, // Convert int to boolean
            completed_at = task.seriesId, // Use seriesId as completed_at for recurring tasks
            recurrence = mapRecurrenceToApi(task),
            repeat_from = if (task.isAllDay) null else "due_date" // null when all-day, "due_date" when not all-day
        )
    }
    
    private fun mapCalendarTaskToUpdateRequest(task: CalendarTask): UpdateTaskRequest {
        return UpdateTaskRequest(
            title = task.title,
            description = task.details,
            due_at = formatDueAt(task),
            completed = task.state == 1, // Convert int to boolean
            completed_at = task.seriesId, // Use seriesId as completed_at for recurring tasks
            recurrence = mapRecurrenceToApi(task),
            repeat_from = if (task.isAllDay) null else "due_date" // null when all-day, "due_date" when not all-day
        )
    }
    
    private fun mapApiTaskToCalendarTask(apiTask: TaskResponse): CalendarTask {
        val (date, time) = parseDueAt(apiTask.due_at)
        
        return CalendarTask(
            id = apiTask.id.toString(),
            title = apiTask.title,
            details = apiTask.description ?: "",
            date = date,
            time = time,
            isAllDay = isAllDayFromDueAt(apiTask.due_at),
            tag = getTaskListName(apiTask.task_list_id) ?: "Unknown",
            task_list_id = apiTask.task_list_id, // Use correct field name
            state = if (apiTask.completed) 1 else 0, // Convert boolean to int
            seriesId = apiTask.completed_at, // Use completed_at as seriesId for recurring tasks
            repeatFrequency = mapApiRecurrenceToRepeatFrequency(apiTask.recurrence),
            monthlyPattern = mapApiRecurrenceToMonthlyPattern(apiTask.recurrence),
            repeatEnd = mapApiRecurrenceToRepeatEnd(apiTask.recurrence)
        )
    }
    
    private fun formatDueAt(task: CalendarTask): String? {
        return if (task.isAllDay) {
            // All day task: use date only in ISO format with timezone
            "${task.date}T00:00:00Z" // RFC3339 format for all-day tasks at midnight UTC
        } else {
            // Timed task: combine date and time in RFC3339 format with timezone
            val localDateTime = java.time.LocalDateTime.of(task.date, task.time)
            "${localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}Z"
        }
    }
    
    private fun parseDueAt(dueAt: String?): Pair<LocalDate, LocalTime> {
        if (dueAt == null) {
            return Pair(LocalDate.now(), LocalTime.of(0, 0))
        }
        
        return try {
            if (dueAt.contains('T')) {
                // Has time component
                val parts = dueAt.split('T')
                val date = LocalDate.parse(parts[0])
                val time = LocalTime.parse(parts[1].substringBefore('+').substringBefore('Z'))
                Pair(date, time)
            } else {
                // Date only (all day task)
                val date = LocalDate.parse(dueAt)
                Pair(date, LocalTime.of(0, 0))
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error parsing due_at: $dueAt", e)
            Pair(LocalDate.now(), LocalTime.of(0, 0))
        }
    }
    
    private fun isAllDayFromDueAt(dueAt: String?): Boolean {
        if (dueAt == null) return true
        // Check if it's an all-day task by looking for midnight time (00:00:00)
        return dueAt.contains("T00:00:00")
    }
    
    private fun getTaskListName(taskListId: Int): String? {
        return taskLists.find { it.task_list_id == taskListId }?.task_list_name
    }
    
    // ===== RECURRENCE MAPPING FUNCTIONS =====
    
    private fun mapRecurrenceToApi(task: CalendarTask): TaskRecurrence? {
        if (task.repeatFrequency == RepeatFrequency.NONE) return null
        
        val pattern = when (task.repeatFrequency) {
            RepeatFrequency.DAILY -> RecurrencePattern(
                type = "daily",
                interval = 1
            )
            RepeatFrequency.WEEKLY -> RecurrencePattern(
                type = "weekly",
                interval = 1,
                days_of_week = listOf(task.date.dayOfWeek.name.lowercase()) // Convert to lowercase string format
            )
            RepeatFrequency.MONTHLY -> {
                when (task.monthlyPattern) {
                    is MonthlyPattern.SameDay -> RecurrencePattern(
                        type = "monthly_absolute",
                        interval = 1,
                        day_of_month = task.monthlyPattern.dayOfMonth
                    )
                    is MonthlyPattern.NthWeekday -> RecurrencePattern(
                        type = "monthly_relative",
                        interval = 1,
                        week_of_month = when (task.monthlyPattern.weekOfMonth) {
                            1 -> "first"
                            2 -> "second"
                            3 -> "third"
                            4 -> "fourth"
                            else -> "last"
                        },
                        day_of_week = task.monthlyPattern.dayOfWeek.name.lowercase()
                    )
                    null -> RecurrencePattern(
                        type = "monthly_absolute",
                        interval = 1,
                        day_of_month = task.date.dayOfMonth
                    )
                }
            }
            RepeatFrequency.YEARLY -> RecurrencePattern(
                type = "yearly",
                interval = 1
            )
            RepeatFrequency.NONE -> null
        } ?: return null
        
        val range = when (task.repeatEnd) {
            is RepeatEnd.Never -> null // For "never" ending, range should be null according to API
            is RepeatEnd.AfterOccurrences -> RecurrenceRange(
                type = "numbered",
                count = task.repeatEnd.count
            )
            is RepeatEnd.UntilDate -> RecurrenceRange(
                type = "end_date",
                end_at = "${task.repeatEnd.endDate}T23:59:59Z" // Convert to ISO 8601 datetime format
            )
        }
        
        return TaskRecurrence(pattern = pattern, range = range)
    }
    
    private fun mapApiRecurrenceToRepeatFrequency(recurrence: TaskRecurrence?): RepeatFrequency {
        return when (recurrence?.pattern?.type) {
            "daily" -> RepeatFrequency.DAILY
            "weekly" -> RepeatFrequency.WEEKLY
            "monthly_absolute", "monthly_relative" -> RepeatFrequency.MONTHLY
            "yearly" -> RepeatFrequency.YEARLY
            else -> RepeatFrequency.NONE
        }
    }
    
    private fun mapApiRecurrenceToMonthlyPattern(recurrence: TaskRecurrence?): MonthlyPattern? {
        return when (recurrence?.pattern?.type) {
            "monthly_absolute" -> MonthlyPattern.SameDay(
                dayOfMonth = recurrence.pattern.day_of_month ?: 1
            )
            "monthly_relative" -> MonthlyPattern.NthWeekday(
                weekOfMonth = when (recurrence.pattern.week_of_month) {
                    "first" -> 1
                    "second" -> 2
                    "third" -> 3
                    "fourth" -> 4
                    "last" -> 5
                    else -> 1
                },
                dayOfWeek = when (recurrence.pattern.day_of_week?.lowercase()) {
                    "monday" -> DayOfWeek.MONDAY
                    "tuesday" -> DayOfWeek.TUESDAY
                    "wednesday" -> DayOfWeek.WEDNESDAY
                    "thursday" -> DayOfWeek.THURSDAY
                    "friday" -> DayOfWeek.FRIDAY
                    "saturday" -> DayOfWeek.SATURDAY
                    "sunday" -> DayOfWeek.SUNDAY
                    else -> DayOfWeek.MONDAY
                }
            )
            else -> null
        }
    }
    
    private fun mapApiRecurrenceToRepeatEnd(recurrence: TaskRecurrence?): RepeatEnd {
        return when (recurrence?.range?.type) {
            "numbered" -> {
                val count = recurrence.range.count ?: 1
                RepeatEnd.AfterOccurrences(count)
            }
            "end_date" -> {
                val endDateStr = recurrence.range.end_at // Changed from end_date to end_at
                val endDate = try {
                    // Parse the datetime and extract just the date part
                    if (endDateStr?.contains('T') == true) {
                        LocalDate.parse(endDateStr.split('T')[0])
                    } else {
                        LocalDate.parse(endDateStr)
                    }
                } catch (e: Exception) {
                    LocalDate.now().plusYears(1)
                }
                RepeatEnd.UntilDate(endDate)
            }
            null -> RepeatEnd.Never // Handle null range as "never" ending
            else -> RepeatEnd.Never
        }
    }
    
    // ===== PRIVATE API FUNCTIONS =====
    
    private suspend fun getTasksFromApi(taskListId: Int): List<CalendarTask> {
        return try {
            Log.d("CalendarRepository1", "Getting tasks from task list ID: $taskListId")
            val response = taskApi?.getTasksFromList(taskListId)
            if (response?.isSuccessful == true) {
                val tasks = response.body()?.map { apiTask ->
                    mapApiTaskToCalendarTask(apiTask)
                } ?: emptyList()
                Log.d("CalendarRepository1", "Successfully loaded ${tasks.size} tasks from task list $taskListId")
                tasks
            } else {
                Log.e("CalendarRepository1", "Failed to get tasks from task list $taskListId: ${response?.code()}")
                response?.errorBody()?.string()?.let { errorBody ->
                    Log.e("CalendarRepository1", "Error body: $errorBody")
                }
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error getting tasks from task list $taskListId: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
    
    private suspend fun createTaskInApi(task: CalendarTask): CalendarTask? {
        return try {
            val request = mapCalendarTaskToCreateRequest(task)
            val response = taskApi?.createTask(task.task_list_id, request)
            if (response?.isSuccessful == true) {
                response.body()?.let { apiTask ->
                    mapApiTaskToCalendarTask(apiTask)
                }
            } else {
                Log.e("CalendarRepository1", "Failed to create task: ${response?.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error creating task: ${e.message}")
            null
        }
    }
    
    private suspend fun updateTaskInApi(task: CalendarTask): CalendarTask? {
        return try {
            val request = mapCalendarTaskToUpdateRequest(task)
            val taskId = task.id.toIntOrNull()
            if (taskId != null) {
                val response = taskApi?.updateTask(task.task_list_id, taskId, request)
                if (response?.isSuccessful == true) {
                    response.body()?.let { apiTask ->
                        mapApiTaskToCalendarTask(apiTask)
                    }
                } else {
                    Log.e("CalendarRepository1", "Failed to update task: ${response?.code()}")
                    response?.errorBody()?.string()?.let { errorBody ->
                        Log.e("CalendarRepository1", "Update task error body: $errorBody")
                    }
                    null
                }
            } else {
                Log.e("CalendarRepository1", "Invalid task ID for update: ${task.id}")
                null
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error updating task: ${e.message}")
            null
        }
    }
    
    private suspend fun deleteTaskFromApi(taskListId: Int, taskId: Int): Boolean {
        return try {
            val response = taskApi?.deleteTask(taskListId, taskId)
            if (response?.isSuccessful == true) {
                true
            } else {
                Log.e("CalendarRepository1", "Failed to delete task: ${response?.code()}")
                response?.errorBody()?.string()?.let { errorBody ->
                    Log.e("CalendarRepository1", "Delete task error body: $errorBody")
                }
                false
            }
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error deleting task: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
    // ===== PUBLIC COMBINED FUNCTIONS (API + LOCAL) =====
    
    suspend fun loadTasksFromApi(taskListId: Int) {
        val tasksFromApi = getTasksFromApi(taskListId)
        // Clear existing tasks from this task list
        tasks.removeAll { it.task_list_id == taskListId }
        // Add new tasks from API
        tasks.addAll(tasksFromApi)
    }
    
    suspend fun createTaskApi(task: CalendarTask, hasRfc3339SeriesId: Boolean = false): Boolean {
        // Set seriesId to RFC 3339 format if task has recurrence
        val taskWithSeriesId = if (task.repeatFrequency != RepeatFrequency.NONE) {
            if (hasRfc3339SeriesId) {
                // Use existing seriesId from the passed task
                task.copy()
            } else {
                // Generate new RFC 3339 seriesId
                val rfc3339SeriesId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                task.copy(seriesId = rfc3339SeriesId)
            }
        } else {
            task.copy(seriesId = null) // Ensure no seriesId for non-recurring tasks
        }
        
        val createdTask = createTaskInApi(taskWithSeriesId)
        return if (createdTask != null) {
            // Replace the local task with the API version (which has the real ID)
            tasks.removeAll { it.id == task.id }
            addTask(createdTask)
            true
        } else {
            // API failed, but keep the local task
            addTask(taskWithSeriesId)
            false
        }
    }
    
    suspend fun updateTaskApi(task: CalendarTask): Boolean {
        val updatedTask = updateTaskInApi(task)
        return if (updatedTask != null) {
            updateTask(updatedTask)
            // Update the recurring series if needed
            if (updatedTask.seriesId != null) {
                updateTaskSeries(updatedTask)
            }
            true
        } else {
            // API failed, but update locally
            updateTask(task)
            false
        }
    }
    
    suspend fun deleteTaskApi(task: CalendarTask): Boolean {
        val taskId = task.id.toIntOrNull()
        return if (taskId != null) {
            val success = deleteTaskFromApi(task.task_list_id, taskId)
            if (success) {
                // Delete from local storage
//                if (task.seriesId != null) {
//                    deleteTaskSeries(task.seriesId!!)
//                } else {
//                    deleteTask(task)
//                }
                deleteTask(task)
            }
            success
        } else {
            // If no valid API ID, just delete locally
            if (task.seriesId != null) {
                deleteTaskSeries(task.seriesId!!)
            } else {
                deleteTask(task)
            }
            false
        }
    }
    
    suspend fun toggleTaskStateApi(task: CalendarTask): Boolean {
        val updatedTask = task.copy(state = if (task.state == 0) 1 else 0)
        return updateTaskApi(updatedTask)
    }
    
    suspend fun deleteAllTasks(): Boolean {
        Log.d("CalendarRepository1", "Starting to delete all tasks from all task lists...")
        
        try {
            // First, load all task lists and tasks from API to ensure we have everything
            Log.d("CalendarRepository1", "Loading task lists and tasks from API...")
            loadTaskListsFromApi()
            loadTasksFromApi()
            
            var allSuccess = true
            val tasksToDelete = tasks.toList() // Create a copy to avoid concurrent modification
            
            Log.d("CalendarRepository1", "Found ${tasksToDelete.size} tasks to delete")
            
            if (tasksToDelete.isEmpty()) {
                Log.d("CalendarRepository1", "No tasks found to delete")
                return true
            }
            
            // Delete each task via API
            tasksToDelete.forEachIndexed { index, task ->
                try {
                    Log.d("CalendarRepository1", "Deleting task ${index + 1}/${tasksToDelete.size}: ${task.title} (ID: ${task.id}, TaskList: ${task.task_list_id})")
                    val taskId = task.id.toIntOrNull()
                    if (taskId != null) {
                        val success = deleteTaskFromApi(task.task_list_id, taskId)
                        if (!success) {
                            Log.w("CalendarRepository1", "Failed to delete task ${task.id} (${task.title}) from API")
                            allSuccess = false
                        } else {
                            Log.d("CalendarRepository1", "Successfully deleted task ${task.id} (${task.title}) from API")
                        }
                    } else {
                        Log.w("CalendarRepository1", "Task ${task.id} has invalid ID format (not a number), skipping API deletion")
                        allSuccess = false
                    }
                } catch (e: Exception) {
                    Log.e("CalendarRepository1", "Error deleting task ${task.id}: ${e.message}")
                    e.printStackTrace()
                    allSuccess = false
                }
            }
            
            // Clear all tasks from local storage regardless of API success
            tasks.clear()
            Log.d("CalendarRepository1", "Cleared all ${tasksToDelete.size} tasks from local storage")
            
            if (allSuccess) {
                Log.d("CalendarRepository1", "All tasks deleted successfully from both API and local storage")
            } else {
                Log.w("CalendarRepository1", "Some tasks failed to delete from API, but local storage was cleared")
            }
            
            return allSuccess
        } catch (e: Exception) {
            Log.e("CalendarRepository1", "Error in deleteAllTasks: ${e.message}")
            e.printStackTrace()
            // Still clear local tasks even if there was an error
            tasks.clear()
            return false
        }
    }
}


