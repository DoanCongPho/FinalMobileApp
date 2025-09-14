package com.example.finalproject.core.network.api.tasklist_task

import com.google.gson.annotations.SerializedName

/**
 * API Response model for task list
 */
data class TaskListResponse(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("user_id")
    val user_id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("created_at")
    val created_at: String,
    
    @SerializedName("updated_at")
    val updated_at: String
)

/**
 * Request model for creating/updating task list
 */
data class TaskListRequest(
    @SerializedName("name")
    val name: String
)

/**
 * Error response model
 */
data class ErrorResponse(
    @SerializedName("status_code")
    val status_code: Int,
    
    @SerializedName("detail")
    val detail: String,
    
    @SerializedName("extra")
    val extra: Map<String, Any>
)

// ==============================================
// TASK API MODELS
// ==============================================

/**
 * Recurrence pattern sub-object for API
 */
data class RecurrencePattern(
    @SerializedName("type")
    val type: String, // "daily", "weekly", "monthly_absolute", "monthly_relative", "yearly"
    
    @SerializedName("interval")
    val interval: Int? = null, // For intervals (every N days/weeks/months/years)
    
    @SerializedName("day_of_month")
    val day_of_month: Int? = null, // For monthly_absolute
    
    @SerializedName("week_of_month")
    val week_of_month: String? = null, // For monthly_relative ("first", "second", "third", "fourth", "last")
    
    @SerializedName("day_of_week")
    val day_of_week: String? = null, // For weekly and monthly_relative ("monday", "tuesday", etc.)
    
    @SerializedName("days_of_week")
    val days_of_week: List<String>? = null // For weekly with multiple days (["monday", "tuesday", etc.])
)

/**
 * Recurrence range sub-object for API
 */
data class RecurrenceRange(
    @SerializedName("type")
    val type: String, // "numbered", "end_date"
    
    @SerializedName("count")
    val count: Int? = null, // For numbered recurrence
    
    @SerializedName("end_date")
    val end_date: String? = null // For end_date recurrence (ISO 8601 format)
)

/**
 * Complete recurrence object for API
 */
data class TaskRecurrence(
    @SerializedName("pattern")
    val pattern: RecurrencePattern,
    
    @SerializedName("range")
    val range: RecurrenceRange
)

/**
 * API Response model for task
 */
data class TaskResponse(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("task_list_id")
    val task_list_id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("due_at")
    val due_at: String? = null, // ISO 8601 datetime format
    
    @SerializedName("completed")
    val completed: Boolean, // true = completed, false = not completed
    
    @SerializedName("completed_at")
    val completed_at: String? = null, // Will be used as series_id for recurring tasks
    
    @SerializedName("recurrence")
    val recurrence: TaskRecurrence? = null,
    
    @SerializedName("repeat_from")
    val repeat_from: String? = null, // "due_date" or "completion_date"
    
    @SerializedName("created_at")
    val created_at: String,
    
    @SerializedName("updated_at")
    val updated_at: String
)

/**
 * Request model for creating task
 */
data class CreateTaskRequest(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("due_at")
    val due_at: String? = null, // ISO 8601 datetime format
    
    @SerializedName("completed")
    val completed: Boolean = false, // true = completed, false = not completed
    
    @SerializedName("completed_at")
    val completed_at: String? = null, // Will be used as series_id for recurring tasks
    
    @SerializedName("recurrence")
    val recurrence: TaskRecurrence? = null,
    
    @SerializedName("repeat_from")
    val repeat_from: String? = null // "due_date" or "completion_date"
)

/**
 * Request model for updating task
 */
data class UpdateTaskRequest(
    @SerializedName("title")
    val title: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("due_at")
    val due_at: String? = null, // ISO 8601 datetime format
    
    @SerializedName("completed")
    val completed: Boolean? = null, // true = completed, false = not completed
    
    @SerializedName("completed_at")
    val completed_at: String? = null, // Used as series_id for recurring tasks
    
    @SerializedName("recurrence")
    val recurrence: TaskRecurrence? = null,
    
    @SerializedName("repeat_from")
    val repeat_from: String? = null // "due_date" or "completion_date"
)