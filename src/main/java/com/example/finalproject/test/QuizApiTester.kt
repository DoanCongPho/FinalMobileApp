package com.example.finalproject.test

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.core.network.api.ApiClient
import com.example.finalproject.createquiz.data.CreateQuizRepository
import com.example.finalproject.createquiz.model.ManualQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

data class TestResult(
    val testName: String,
    val success: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class QuizApiTester(
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModel() {
    
    private val _testResults = MutableStateFlow<List<TestResult>>(emptyList())
    val testResults: StateFlow<List<TestResult>> = _testResults
    
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning
    
    private val apiClient = ApiClient.create(tokenManager)
    private val repository = CreateQuizRepository(apiClient.quizApi)
    
    fun runAllTests() {
        viewModelScope.launch {
            _isRunning.value = true
            _testResults.value = emptyList()
            
            try {
                // Test 1: Authentication check
                testAuthentication()
                
                // Test 2: Manual quiz creation
                testManualQuizCreation()
                
                // Test 3: Get user quizzes
                testGetUserQuizzes()
                
                // Test 4: File upload test (simplified)
                testFileUpload()
                
            } catch (e: Exception) {
                addTestResult(TestResult(
                    testName = "Test Suite",
                    success = false,
                    message = "Test suite failed: ${e.message}"
                ))
            } finally {
                _isRunning.value = false
            }
        }
    }
    
    private suspend fun testAuthentication() {
        try {
            val token = tokenManager.accessToken.first()
            if (token.isNullOrBlank()) {
                addTestResult(TestResult(
                    testName = "Authentication",
                    success = false,
                    message = "No access token found. Please login first."
                ))
                return
            }
            
            addTestResult(TestResult(
                testName = "Authentication",
                success = true,
                message = "Access token found: ${token.take(20)}..."
            ))
            
        } catch (e: Exception) {
            addTestResult(TestResult(
                testName = "Authentication",
                success = false,
                message = "Auth check failed: ${e.message}"
            ))
        }
    }
    
    private suspend fun testGetUserQuizzes() {
        try {
            val token = tokenManager.accessToken.first()
            val userId = tokenManager.userId.first()
            
            if (token.isNullOrBlank()) {
                addTestResult(TestResult(
                    testName = "Get User Quizzes",
                    success = false,
                    message = "No access token found. Please login first."
                ))
                return
            }
            
            val response = apiClient.quizApi.getUserQuizzes()
            
            if (response.isSuccessful) {
                val quizzes = response.body() ?: emptyList()
                addTestResult(TestResult(
                    testName = "Get User Quizzes",
                    success = true,
                    message = "Retrieved ${quizzes.size} quizzes for user $userId"
                ))
            } else {
                addTestResult(TestResult(
                    testName = "Get User Quizzes",
                    success = false,
                    message = "API call failed: ${response.message()}"
                ))
            }
            
        } catch (e: Exception) {
            addTestResult(TestResult(
                testName = "Get User Quizzes",
                success = false,
                message = "Test failed: ${e.message}"
            ))
        }
    }
    
    private suspend fun testManualQuizCreation() {
        try {
            val testQuestions = listOf(
                ManualQuestion(
                    question = "What is 2 + 2?",
                    answer = "4"
                ),
                ManualQuestion(
                    question = "What color is the sky?",
                    answer = "Blue"
                )
            )
            
            val result = repository.createManualQuiz(
                title = "Test Quiz ${System.currentTimeMillis()}",
                questions = testQuestions
            )
            
            addTestResult(TestResult(
                testName = "Manual Quiz Creation",
                success = true,
                message = "Quiz created successfully: ${result.title}"
            ))
            
        } catch (e: Exception) {
            addTestResult(TestResult(
                testName = "Manual Quiz Creation",
                success = false,
                message = "Test failed: ${e.message}"
            ))
        }
    }
    
    private suspend fun testFileUpload() {
        try {
            addTestResult(TestResult(
                testName = "File Upload",
                success = false,
                message = "File upload test not implemented yet - requires ContentResolver"
            ))
            
        } catch (e: Exception) {
            addTestResult(TestResult(
                testName = "File Upload",
                success = false,
                message = "Test failed: ${e.message}"
            ))
        }
    }
    
    private suspend fun addTestResult(result: TestResult) {
        val currentResults = _testResults.value.toMutableList()
        currentResults.add(result)
        _testResults.value = currentResults
    }
    
    fun clearResults() {
        _testResults.value = emptyList()
    }
}
