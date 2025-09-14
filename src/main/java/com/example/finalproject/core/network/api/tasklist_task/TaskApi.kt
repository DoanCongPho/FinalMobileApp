package com.example.finalproject.core.network.api.tasklist_task

import retrofit2.Response
import retrofit2.http.*

interface TaskApi {
    /**
     * Get all tasks from a specific task list
     */
    @GET("task-lists/{task_list_id}/tasks")
    suspend fun getTasksFromList(
        @Path("task_list_id") taskListId: Int
    ): Response<List<TaskResponse>>
    
    /**
     * Create a new task in a specific task list
     */
    @POST("task-lists/{task_list_id}/tasks")
    suspend fun createTask(
        @Path("task_list_id") taskListId: Int,
        @Body request: CreateTaskRequest
    ): Response<TaskResponse>
    
    /**
     * Update an existing task
     */
    @POST("task-lists/{task_list_id}/tasks/{task_id}")
    suspend fun updateTask(
        @Path("task_list_id") taskListId: Int,
        @Path("task_id") taskId: Int,
        @Body request: UpdateTaskRequest
    ): Response<TaskResponse>
    
    /**
     * Delete a task
     */
    @DELETE("task-lists/{task_list_id}/tasks/{task_id}")
    suspend fun deleteTask(
        @Path("task_list_id") taskListId: Int,
        @Path("task_id") taskId: Int
    ): Response<Unit>
}