package com.example.finalproject.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finalproject.calendar.data.CalendarRepository
import com.example.finalproject.calendar.data.CalendarRepository1
import com.example.finalproject.calendar.model.CalendarEvent
import com.example.finalproject.calendar.model.EventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.collections.List
import kotlin.Boolean
import kotlin.String

data class CalendarUiState(
    val events: List<CalendarEvent> = emptyList(),
    val selectedDate: LocalDateTime = LocalDateTime.now(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CalendarViewModel(private val repo: CalendarRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        loadMockEvents()
    }

    private fun loadMockEvents() {
        val now = LocalDateTime.now()
        val mockEvents = listOf(
            CalendarEvent(
                id = "1",
                title = "Daily Standup",
                time = now.withHour(8).withMinute(0),
                type = EventType.DAILY_STANDUP
            ),
            CalendarEvent(
                id = "2",
                title = "Budget Review",
                time = now.withHour(9).withMinute(0),
                type = EventType.MEETING
            ),
            CalendarEvent(
                id = "3",
                title = "Nha Trang",
                time = now.plusDays(1).withHour(0).withMinute(0),
                type = EventType.VACATION
            )
        )
        _uiState.value = _uiState.value.copy(events = mockEvents)
    }
}

class CalendarViewModelFactory(private val repo: CalendarRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




class CalendarViewModel1(): ViewModel() {
    val tasks = CalendarRepository1.getTasks() // tasks là mutableStateListOf
    val taskLists = CalendarRepository1.getTaskLists() // taskLists là mutableStateListOf
    
    // Draft task state for preserving data during navigation
    private var _draftTask: com.example.finalproject.Tasks.model.CalendarTask? = null
    val draftTask: com.example.finalproject.Tasks.model.CalendarTask?
        get() = _draftTask

    init {
        viewModelScope.launch {
            //CalendarRepository1.loadTasksFromApi()
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            CalendarRepository1.loadTaskListsFromApi()
            CalendarRepository1.loadTasksFromApi()
        }
    }
    
    // Load tasks for a specific task list
    fun loadTasksForTaskList(taskListId: Int) {
        viewModelScope.launch {
            CalendarRepository1.loadTasksFromApi(taskListId)
        }
    }

    fun addTask(task: com.example.finalproject.Tasks.model.CalendarTask) {
        CalendarRepository1.addTask(task)
    }
    
    // API-based task creation
    suspend fun createTask(task: com.example.finalproject.Tasks.model.CalendarTask, hasRfc3339SeriesId: Boolean = false): Boolean {
        return CalendarRepository1.createTaskApi(task, hasRfc3339SeriesId)
    }

    fun deleteTask(task: com.example.finalproject.Tasks.model.CalendarTask) {
        CalendarRepository1.deleteTask(task)
    }
    
    // API-based task deletion  
    suspend fun deleteTaskApi(task: com.example.finalproject.Tasks.model.CalendarTask): Boolean {
        return CalendarRepository1.deleteTaskApi(task)
    }
    
    // Delete all tasks from all task lists
    suspend fun deleteAllTasks(): Boolean {
        return CalendarRepository1.deleteAllTasks()
    }

    fun updateTask(task: com.example.finalproject.Tasks.model.CalendarTask) {
        CalendarRepository1.updateTask(task)
    }
    
    // API-based task update
    suspend fun updateTaskApi(task: com.example.finalproject.Tasks.model.CalendarTask): Boolean {
        return CalendarRepository1.updateTaskApi(task)
    }

    fun toggleTaskState(task: com.example.finalproject.Tasks.model.CalendarTask) {
        viewModelScope.launch {
            CalendarRepository1.toggleTaskStateApi(task)
        }
    }
    
    // Task series management methods
    suspend fun createTaskSeries(rootTask: com.example.finalproject.Tasks.model.CalendarTask) {
        CalendarRepository1.createTaskSeries(rootTask)
    }
    
    fun deleteTaskSeries(seriesId: String) {
        CalendarRepository1.deleteTaskSeries(seriesId)
    }
    
    suspend fun updateTaskSeries(rootTask: com.example.finalproject.Tasks.model.CalendarTask) {
        CalendarRepository1.updateTaskSeries(rootTask)
    }
    
    // Draft task management methods
    fun saveDraftTask(task: com.example.finalproject.Tasks.model.CalendarTask) {
        _draftTask = task
    }
    
    fun clearDraftTask() {
        _draftTask = null
    }
    
    fun updateDraftTask(updates: (com.example.finalproject.Tasks.model.CalendarTask) -> com.example.finalproject.Tasks.model.CalendarTask) {
        _draftTask?.let { draft ->
            _draftTask = updates(draft)
        }
    }
    
    // Task Lists management methods
    suspend fun createTaskList(name: String): Boolean {
        return CalendarRepository1.createTaskList(name)
    }
    
    suspend fun updateTaskList(taskListId: Int, name: String): Boolean {
        return CalendarRepository1.updateTaskList(taskListId, name)
    }
    
    suspend fun deleteTaskList(taskListId: Int): Boolean {
        return CalendarRepository1.deleteTaskList(taskListId)
    }
    
    suspend fun getTaskList(taskListId: Int): com.example.finalproject.Tasks.model.CalendarTasklist? {
        return CalendarRepository1.getTaskList(taskListId)
    }
    
    fun loadTaskLists() {
        viewModelScope.launch {
            CalendarRepository1.loadTaskListsFromApi()
        }
    }
}

class CalendarViewModel1Factory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel1::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel1() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


