package com.example.finalproject.createquiz.test

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.core.network.api.ApiClient
import com.example.finalproject.createquiz.data.CreateQuizRepository
import com.example.finalproject.createquiz.model.QuizCreateRequest
import com.example.finalproject.createquiz.model.QuizQuestionCreateRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * Simple test class to verify Quiz Creation APIs work
 * Call these methods from your Activity/Fragment to test
 */
class QuizApiTester(private val context: Context) {
    
    private val tokenManager = TokenManager.getInstance(context)
    private val apiClient = ApiClient.create(tokenManager)
    private val repository = CreateQuizRepository(apiClient.quizApi)
    
    /**
     * Test 1: Manual Quiz Creation
     * Creates a simple 2-question quiz manually
     */
    suspend fun testManualQuizCreation(): String {
        return try {
            println("🧪 Testing Manual Quiz Creation...")
            
            // Create test questions
            val testQuestions = listOf(
                QuizQuestionCreateRequest(
                    question = "What is 2 + 2?",
                    answer = "4",
                    explanation = "Basic arithmetic"
                ),
                QuizQuestionCreateRequest(
                    question = "What is the capital of France?",
                    answer = "Paris",
                    explanation = "Geography question"
                )
            )
            
            // Create request
            val request = QuizCreateRequest(
                title = "Test Quiz - Manual",
                questions = testQuestions
            )
            
            // Call API
            val response = apiClient.quizApi.createQuiz(request)
            
            if (response.isSuccessful) {
                val quiz = response.body()
                println("✅ Manual Quiz Created Successfully!")
                println("📋 Quiz ID: ${quiz?.id}")
                println("📋 Quiz Title: ${quiz?.title}")
                println("📋 Questions Count: ${quiz?.questions?.size}")
                "SUCCESS: Manual quiz created with ID: ${quiz?.id}"
            } else {
                println("❌ Manual Quiz Creation Failed")
                println("📋 Error Code: ${response.code()}")
                println("📋 Error Message: ${response.message()}")
                "FAILED: ${response.code()} - ${response.message()}"
            }
            
        } catch (e: Exception) {
            println("💥 Exception in Manual Quiz Creation: ${e.message}")
            e.printStackTrace()
            "EXCEPTION: ${e.message}"
        }
    }
    
    /**
     * Test 2: Get User Quizzes (to verify quiz was created)
     */
    suspend fun testGetUserQuizzes(): String {
        return try {
            println("🧪 Testing Get User Quizzes...")
            
            val response = apiClient.quizApi.getUserQuizzes()
            
            if (response.isSuccessful) {
                val quizzes = response.body() ?: emptyList()
                println("✅ Retrieved ${quizzes.size} quizzes")
                quizzes.forEach { quiz ->
                    println("📋 Quiz: ${quiz.title} (ID: ${quiz.id}) - ${quiz.questions.size} questions")
                }
                "SUCCESS: Retrieved ${quizzes.size} quizzes"
            } else {
                println("❌ Failed to get quizzes")
                println("📋 Error: ${response.code()} - ${response.message()}")
                "FAILED: ${response.code()} - ${response.message()}"
            }
            
        } catch (e: Exception) {
            println("💥 Exception in Get Quizzes: ${e.message}")
            e.printStackTrace()
            "EXCEPTION: ${e.message}"
        }
    }
    
    /**
     * Test 3: Check Authentication
     * Verifies if we have a valid token
     */
    suspend fun testAuthentication(): String {
        return try {
            val token = tokenManager.accessToken.first()
            val userId = tokenManager.userId.first()
            
            if (token != null && userId != null) {
                println("✅ Authentication OK")
                println("📋 User ID: $userId")
                println("📋 Token Length: ${token.length}")
                "SUCCESS: Authenticated as User $userId"
            } else {
                println("❌ No authentication token found")
                "FAILED: Not authenticated - Please login first"
            }
        } catch (e: Exception) {
            println("💥 Exception checking auth: ${e.message}")
            "EXCEPTION: ${e.message}"
        }
    }
    
    /**
     * Run all tests sequentially
     */
    suspend fun runAllTests(): List<String> {
        return listOf(
            testAuthentication(),
            testGetUserQuizzes(),
            testManualQuizCreation(),
            testGetUserQuizzes() // Check again to see new quiz
        )
    }
}

/**
 * Helper function to run tests from UI
 * Call this from your Activity/Fragment
 */
fun runQuizApiTests(context: Context, onResults: (List<String>) -> Unit) {
    val tester = QuizApiTester(context)
    
    // Run in background coroutine
    CoroutineScope(Dispatchers.IO).launch {
        val results = tester.runAllTests()
        
        // Switch back to main thread for UI updates
        withContext(Dispatchers.Main) {
            onResults(results)
        }
    }
}
