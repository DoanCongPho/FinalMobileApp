//package com.example.finalproject.calendar.data
//
//import android.util.Log
//import com.example.finalproject.Tasks.model.CalendarTasklist
//import com.example.finalproject.core.network.api.tasklist_task.CreateTaskListRequest
//import com.example.finalproject.core.network.api.tasklist_task.TaskListApi
//import com.example.finalproject.core.network.api.tasklist_task.TaskListResponse
//import com.example.finalproject.core.network.api.tasklist_task.UpdateTaskListRequest
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class TaskListService(private val taskListApi: TaskListApi) {
//
//    // API interaction functions
//    private suspend fun fetchUserTaskListsFromApi(): Result<List<TaskListResponse>> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = taskListApi.getUserTaskLists()
//                if (response.isSuccessful) {
//                    Result.success(response.body() ?: emptyList())
//                } else {
//                    val errorMessage = "Failed to fetch task lists: ${response.code()} ${response.message()}"
//                    Log.e("TaskListService", errorMessage)
//                    Result.failure(Exception(errorMessage))
//                }
//            } catch (e: Exception) {
//                Log.e("TaskListService", "Error fetching task lists", e)
//                Result.failure(e)
//            }
//        }
//    }
//
//    private suspend fun fetchTaskListFromApi(taskListId: Int): Result<TaskListResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = taskListApi.getTaskList(taskListId)
//                if (response.isSuccessful) {
//                    Result.success(response.body()!!)
//                } else {
//                    val errorMessage = "Failed to fetch task list: ${response.code()} ${response.message()}"
//                    Log.e("TaskListService", errorMessage)
//                    Result.failure(Exception(errorMessage))
//                }
//            } catch (e: Exception) {
//                Log.e("TaskListService", "Error fetching task list", e)
//                Result.failure(e)
//            }
//        }
//    }
//
//    private suspend fun createTaskListInApi(name: String): Result<TaskListResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = taskListApi.createTaskList(CreateTaskListRequest(name))
//                if (response.isSuccessful) {
//                    Result.success(response.body()!!)
//                } else {
//                    val errorMessage = "Failed to create task list: ${response.code()} ${response.message()}"
//                    Log.e("TaskListService", errorMessage)
//                    Result.failure(Exception(errorMessage))
//                }
//            } catch (e: Exception) {
//                Log.e("TaskListService", "Error creating task list", e)
//                Result.failure(e)
//            }
//        }
//    }
//
//    private suspend fun updateTaskListInApi(taskListId: Int, name: String): Result<TaskListResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = taskListApi.updateTaskList(taskListId, UpdateTaskListRequest(name))
//                if (response.isSuccessful) {
//                    Result.success(response.body()!!)
//                } else {
//                    val errorMessage = "Failed to update task list: ${response.code()} ${response.message()}"
//                    Log.e("TaskListService", errorMessage)
//                    Result.failure(Exception(errorMessage))
//                }
//            } catch (e: Exception) {
//                Log.e("TaskListService", "Error updating task list", e)
//                Result.failure(e)
//            }
//        }
//    }
//
//    private suspend fun deleteTaskListInApi(taskListId: Int): Result<TaskListResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = taskListApi.deleteTaskList(taskListId)
//                if (response.isSuccessful) {
//                    Result.success(response.body()!!)
//                } else {
//                    val errorMessage = "Failed to delete task list: ${response.code()} ${response.message()}"
//                    Log.e("TaskListService", errorMessage)
//                    Result.failure(Exception(errorMessage))
//                }
//            } catch (e: Exception) {
//                Log.e("TaskListService", "Error deleting task list", e)
//                Result.failure(e)
//            }
//        }
//    }
//
//    // Repository interaction functions
//    private fun updateRepositoryTaskLists(taskLists: List<TaskListResponse>) {
//        val calendarTaskLists = taskLists.map { response ->
//            CalendarTasklist(
//                id = response.id,
//                name = response.name
//            )
//        }
//        CalendarRepository1.setTaskLists(calendarTaskLists)
//    }
//
//    private fun addToRepository(taskListResponse: TaskListResponse) {
//        val calendarTaskList = CalendarTasklist(
//            id = taskListResponse.id,
//            name = taskListResponse.name
//        )
//        CalendarRepository1.addTaskList(calendarTaskList)
//    }
//
//    private fun updateInRepository(taskListResponse: TaskListResponse) {
//        val updatedTaskList = CalendarTasklist(
//            id = taskListResponse.id,
//            name = taskListResponse.name
//        )
//        CalendarRepository1.updateTaskList(updatedTaskList)
//    }
//
//    private fun removeFromRepository(taskListId: Int) {
//        CalendarRepository1.removeTaskList(taskListId)
//    }
//
//    // Combined functions that handle both API and repository
//    suspend fun loadUserTaskLists(): Result<List<CalendarTasklist>> {
//        val apiResult = fetchUserTaskListsFromApi()
//        return if (apiResult.isSuccess) {
//            updateRepositoryTaskLists(apiResult.getOrNull()!!)
//            Result.success(CalendarRepository1.getTaskLists())
//        } else {
//            // Return empty list from repository on failure
//            CalendarRepository1.clearTaskLists()
//            Result.failure(apiResult.exceptionOrNull()!!)
//        }
//    }
//
//    suspend fun getTaskList(taskListId: Int): Result<CalendarTasklist> {
//        val apiResult = fetchTaskListFromApi(taskListId)
//        return if (apiResult.isSuccess) {
//            val taskListResponse = apiResult.getOrNull()!!
//            val calendarTaskList = CalendarTasklist(
//                id = taskListResponse.id,
//                name = taskListResponse.name
//            )
//            Result.success(calendarTaskList)
//        } else {
//            Result.failure(apiResult.exceptionOrNull()!!)
//        }
//    }
//
//    suspend fun createTaskList(name: String): Result<CalendarTasklist> {
//        val apiResult = createTaskListInApi(name)
//        return if (apiResult.isSuccess) {
//            val taskListResponse = apiResult.getOrNull()!!
//            addToRepository(taskListResponse)
//            val calendarTaskList = CalendarTasklist(
//                id = taskListResponse.id,
//                name = taskListResponse.name
//            )
//            Result.success(calendarTaskList)
//        } else {
//            Result.failure(apiResult.exceptionOrNull()!!)
//        }
//    }
//
//    suspend fun updateTaskList(taskListId: Int, name: String): Result<CalendarTasklist> {
//        val apiResult = updateTaskListInApi(taskListId, name)
//        return if (apiResult.isSuccess) {
//            val taskListResponse = apiResult.getOrNull()!!
//            updateInRepository(taskListResponse)
//            val calendarTaskList = CalendarTasklist(
//                id = taskListResponse.id,
//                name = taskListResponse.name
//            )
//            Result.success(calendarTaskList)
//        } else {
//            Result.failure(apiResult.exceptionOrNull()!!)
//        }
//    }
//
//    suspend fun deleteTaskList(taskListId: Int): Result<Unit> {
//        val apiResult = deleteTaskListInApi(taskListId)
//        return if (apiResult.isSuccess) {
//            removeFromRepository(taskListId)
//            Result.success(Unit)
//        } else {
//            Result.failure(apiResult.exceptionOrNull()!!)
//        }
//    }
//}