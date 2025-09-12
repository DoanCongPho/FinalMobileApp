package com.example.finalproject.core.network.api.tasklist_task

import retrofit2.Response
import retrofit2.http.*

interface TaskListApi {
    
    /**
     * Get all user's task lists
     * GET /users/me/task-lists
     */
    @GET("users/me/task-lists")
    suspend fun getUserTaskLists(): Response<List<TaskListResponse>>
    
    /**
     * Get specific task list by ID
     * GET /task-lists/{task_list_id}
     */
    @GET("task-lists/{task_list_id}")
    suspend fun getTaskList(@Path("task_list_id") taskListId: Int): Response<TaskListResponse>
    
    /**
     * Create new task list
     * POST /users/me/task-lists
     */
    @POST("users/me/task-lists")
    suspend fun createTaskList(@Body request: TaskListRequest): Response<TaskListResponse>
    
    /**
     * Update existing task list
     * PUT /task-lists/{task_list_id}
     */
    @PUT("task-lists/{task_list_id}")
    suspend fun updateTaskList(
        @Path("task_list_id") taskListId: Int,
        @Body request: TaskListRequest
    ): Response<TaskListResponse>
    
    /**
     * Delete task list
     * DELETE /task-lists/{task_list_id}
     */
    @DELETE("task-lists/{task_list_id}")
    suspend fun deleteTaskList(@Path("task_list_id") taskListId: Int): Response<TaskListResponse>
}