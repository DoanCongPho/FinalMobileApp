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