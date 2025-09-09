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
    
    // Draft task state for preserving data during navigation
    private var _draftTask: com.example.finalproject.Tasks.model.CalendarTask? = null
    val draftTask: com.example.finalproject.Tasks.model.CalendarTask?
        get() = _draftTask

    init {
        viewModelScope.launch {
            CalendarRepository1.loadTasksFromApi()
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            CalendarRepository1.loadTasksFromApi()
        }
    }

    fun addTask(task: com.example.finalproject.Tasks.model.CalendarTask) {
        CalendarRepository1.addTask(task)
    }

    fun deleteTask(task: com.example.finalproject.Tasks.model.CalendarTask) {
        CalendarRepository1.removeTask(task)
    }

    fun updateTask(task: com.example.finalproject.Tasks.model.CalendarTask) {
        CalendarRepository1.updateTask(task)
    }

    fun toggleTaskState(task: com.example.finalproject.Tasks.model.CalendarTask) {
        val updatedTask = task.copy(state = if (task.state == 0) 1 else 0)
        CalendarRepository1.updateTask(updatedTask)
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


