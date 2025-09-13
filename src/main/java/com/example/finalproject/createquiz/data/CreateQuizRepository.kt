package com.example.finalproject.createquiz.data

import android.content.ContentResolver
import android.net.Uri
import com.example.finalproject.createquiz.model.Difficulty
import com.example.finalproject.createquiz.model.QuizCreateResponse
import com.example.finalproject.createquiz.model.QuizSource
import com.example.finalproject.createquiz.model.ManualQuestion
import com.example.finalproject.createquiz.model.QuizCreateRequest
import com.example.finalproject.createquiz.model.QuizQuestionCreateRequest
import com.example.finalproject.journey.model.Quiz
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.delay
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.core.network.api.quiz.QuizApi
import com.example.finalproject.createquiz.viewmodel.CreateQuizViewModel


class CreateQuizViewModelFactory(
    private val repository: CreateQuizRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateQuizViewModel::class.java)) {
            return CreateQuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class CreateQuizRepository(
    private val api: QuizApi
) {
    
    // Manual Quiz Creation - NEW API method
    suspend fun createManualQuiz(
        title: String?,
        questions: List<ManualQuestion>
    ): Quiz {
        val request = QuizCreateRequest(
            title = title,
            questions = questions.map { 
                QuizQuestionCreateRequest(
                    question = it.question,
                    answer = it.answer,
                    explanation = null
                )
            }
        )
        
        val response = api.createQuiz(request)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to create quiz: ${response.message()}")
        }
    }
    
    // File-based Quiz Creation with Retry Logic and Validation
    suspend fun createQuizFromFile(
        resolver: ContentResolver,
        source: QuizSource,
        prompt: String?,
        numQuestions: Int
    ): Quiz {
        val uri = Uri.parse(source.uriString)
        
        // File size validation (limit to 10MB)
        val maxFileSize = 10 * 1024 * 1024 // 10MB in bytes
        val fileSize = resolver.openInputStream(uri)?.available() ?: 0
        if (fileSize > maxFileSize) {
            throw Exception("File is too large (${fileSize / (1024 * 1024)}MB). Maximum size is 10MB.")
        }
        
        val tempFile = resolver.openInputStream(uri)!!.use { inputStream ->
            streamToTemp(inputStream, source.displayName)
        }
        
        val filePart = MultipartBody.Part.createFormData(
            "file", 
            source.displayName, 
            tempFile.asRequestBody(source.mime.toMediaTypeOrNull())
        )
        
        val promptBody = prompt?.toRequestBody("text/plain".toMediaTypeOrNull())
        val questionCountBody = numQuestions.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        
        // Retry logic with exponential backoff
        return retryWithBackoff(
            maxRetries = 3,
            baseDelayMs = 1000L,
            multiplier = 2.0
        ) {
            val response = api.createQuizFromFile(filePart, promptBody, questionCountBody)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API Error ${response.code()}: ${response.message()}")
            }
        }
    }
    
    // Retry helper function with exponential backoff
    private suspend fun <T> retryWithBackoff(
        maxRetries: Int,
        baseDelayMs: Long,
        multiplier: Double,
        block: suspend () -> T
    ): T {
        var currentDelay = baseDelayMs
        repeat(maxRetries) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                // Only retry on timeout and IO exceptions
                when (e) {
                    is SocketTimeoutException,
                    is IOException -> {
                        if (attempt == maxRetries - 1) {
                            // Last attempt failed, throw with helpful message
                            throw Exception(
                                "Request failed after $maxRetries attempts. " +
                                "This may be due to large file size or slow network. " +
                                "Original error: ${e.message}",
                                e
                            )
                        }
                        // Wait before retrying
                        delay(currentDelay)
                        currentDelay = (currentDelay * multiplier).toLong()
                    }
                    else -> throw e // Don't retry other exceptions
                }
            }
        }
        throw IllegalStateException("Should not reach here")
    }
    
    // Legacy method - keep for backward compatibility
    suspend fun generateQuiz(
        resolver: ContentResolver,
        sources: List<QuizSource>,
        prompt: String,
        difficulty: Difficulty,
        numQuestions: Int
    ): QuizCreateResponse {
        val parts = sources.mapIndexed { idx, src ->
            val uri = Uri.parse(src.uriString)
            val tmp = resolver.openInputStream(uri)!!.use { inS ->
                streamToTemp(inS, src.displayName)
            }
            val body = tmp.asRequestBody(src.mime.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("files", src.displayName, body)
        }

        val promptRB = prompt.toRequestBody("text/plain".toMediaTypeOrNull())
        val diffRB   = difficulty.name.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())
        val numRB    = numQuestions.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return api.generateQuiz(parts, promptRB, diffRB, numRB)
    }

    private fun streamToTemp(input: InputStream, name: String): File {
        val f = File.createTempFile(name.substringBeforeLast('.'), name.substringAfterLast('.', "tmp"))
        f.outputStream().use { out -> input.copyTo(out) }
        return f
    }
}
