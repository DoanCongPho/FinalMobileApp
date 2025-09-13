# Quiz Creation Backend Integration Guide

## 📋 Overview
Based on the backend.json OpenAPI specification, here are the correct API endpoints for creating quizzes:

## 🔧 Available APIs

### 1. Manual Quiz Creation
```
POST /api/v1/users/me/quizzes
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body:**
```kotlin
data class QuizCreateRequest(
    val title: String?,
    val questions: List<QuizQuestionCreateRequest>
)

data class QuizQuestionCreateRequest(
    val question: String,
    val answer: String, 
    val explanation: String? = null
)
```

**Response:** Returns a complete `Quiz` object with generated ID.

### 2. File-based Quiz Creation
```
POST /api/v1/users/me/quizzes/from-file
Content-Type: multipart/form-data
Authorization: Bearer {token}
```

**Form Data:**
- `file`: Binary file (PDF, DOCX, TXT, etc.)
- `prompt`: Optional text prompt for AI generation
- `question_count`: Number of questions to generate (max 50)

**Response:** Returns a complete `Quiz` object with AI-generated questions.

## 🛠️ Implementation Steps

### Step 1: Update QuizApi Interface
```kotlin
interface QuizApi {
    @GET("users/me/quizzes")
    suspend fun getUserQuizzes(): Response<List<Quiz>>

    // Manual Quiz Creation
    @POST("users/me/quizzes")
    suspend fun createQuiz(@Body request: QuizCreateRequest): Response<Quiz>

    // File-based Quiz Creation  
    @Multipart
    @POST("users/me/quizzes/from-file")
    suspend fun createQuizFromFile(
        @Part("file") file: MultipartBody.Part,
        @Part("prompt") prompt: RequestBody?,
        @Part("question_count") questionCount: RequestBody
    ): Response<Quiz>
}
```

### Step 2: Update Repository Methods
```kotlin
class CreateQuizRepository(private val api: QuizApi) {
    
    // Manual Quiz Creation
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
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception("Failed to create quiz: ${response.message()}")
        }
    }
    
    // File-based Quiz Creation
    suspend fun createQuizFromFile(
        resolver: ContentResolver,
        source: QuizSource,
        prompt: String?,
        numQuestions: Int
    ): Quiz {
        val uri = Uri.parse(source.uriString)
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
        
        val response = api.createQuizFromFile(filePart, promptBody, questionCountBody)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception("Failed to create quiz: ${response.message()}")
        }
    }
}
```

### Step 3: Update ViewModels

#### Manual Quiz ViewModel
```kotlin
class ManualQuizViewModel(
    private val repository: CreateQuizRepository
) : ViewModel() {
    
    // Add submission logic
    fun submitQuiz(title: String? = null, onSuccess: (Int) -> Unit) {
        val questions = _state.value.items
        if (questions.isEmpty()) return
        
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            
            try {
                val createdQuiz = repository.createManualQuiz(
                    title = title?.takeIf { it.isNotBlank() } ?: "Manual Quiz",
                    questions = questions
                )
                
                _state.update { 
                    it.copy(
                        isSubmitting = false, 
                        createdQuizId = createdQuiz.id,
                        items = emptyList() // Clear form
                    ) 
                }
                onSuccess(createdQuiz.id)
                
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isSubmitting = false, 
                        error = e.message ?: "Failed to create quiz"
                    ) 
                }
            }
        }
    }
}
```

#### File-based Quiz ViewModel
```kotlin
class CreateQuizViewModel(
    private val repo: CreateQuizRepository
) : ViewModel() {
    
    fun createQuizFromFile(resolver: ContentResolver, onSuccess: (Int) -> Unit) {
        val s = _ui.value
        if (s.sources.isEmpty()) { setError("Please select a file."); return }
        
        setState { copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            try {
                val source = s.sources.first()
                val quiz = repo.createQuizFromFile(
                    resolver = resolver,
                    source = source,
                    prompt = s.prompt.takeIf { it.isNotBlank() },
                    numQuestions = s.numQuestions
                )
                
                setState { 
                    copy(
                        isSubmitting = false, 
                        createdQuiz = quiz
                    ) 
                }
                onSuccess(quiz.id)
                
            } catch (e: Exception) {
                setState { 
                    copy(
                        isSubmitting = false, 
                        error = e.message ?: "Failed to create quiz"
                    ) 
                }
            }
        }
    }
}
```

### Step 4: Update Navigation
```kotlin
// Manual Quiz Screen
composable(Screen.CreateQuizManual.route) {
    val repository = CreateQuizRepository(apiHolder.quizApi)
    val vm: ManualQuizViewModel = viewModel(
        factory = ManualQuizViewModelFactory(repository)
    )
    
    ManualQuizScreen(
        viewModel = vm,
        onFinish = { quizId ->
            navController.navigate("create_quiz/success?quizId=$quizId") {
                popUpTo(Screen.CreateQuizRoot.route) { inclusive = false }
            }
        },
        onBack = { navController.popBackStack() }
    )
}

// File-based Quiz Screen  
composable(Screen.CreateQuizPrompt.route) { backStackEntry ->
    val parentEntry = remember(backStackEntry) {
        navController.getBackStackEntry(Screen.CreateQuizRoot.route)
    }
    val createQuizVm: CreateQuizViewModel = viewModel(
        parentEntry,
        factory = CreateQuizViewModelFactory(
            CreateQuizRepository(apiHolder.quizApi)
        )
    )
    
    PromptScreen(
        nav = navController,
        vm = createQuizVm
    )
}
```

## ✅ Key Features
1. **Authentication**: Automatically handled via AuthInterceptor
2. **File Upload**: Supports multiple file formats for AI generation
3. **Manual Creation**: Create quizzes with custom questions/answers
4. **Error Handling**: Proper error responses and user feedback
5. **Navigation**: Seamless integration with existing navigation flow

## 🔄 Migration Notes
- Update your existing `generateQuiz` calls to use `createQuizFromFile`
- Add manual quiz creation functionality to `ManualQuizViewModel`
- Both APIs return complete `Quiz` objects instead of just IDs
- File upload now uses single file instead of multiple files
- Question count is limited to 50 maximum per backend specification

## 🧪 Testing
1. Test manual quiz creation with various question/answer combinations
2. Test file upload with different file formats (PDF, DOCX, TXT)
3. Test error handling for invalid files or network issues
4. Verify created quizzes appear in the quiz list immediately

This implementation matches the backend.json OpenAPI specification exactly and provides both manual and AI-powered quiz creation capabilities.
