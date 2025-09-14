package com.example.finalproject.createquiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalproject.journey.viewmodel.QuizViewModel
import com.example.finalproject.review.ui.QuizNavigationHolder

@Composable
fun SuccessScreen(
    nav: NavController,
    onGetThere: () -> Unit,
    onBackToMenu: () -> Unit,
    quizId: String? = null,
    quizViewModel: QuizViewModel? = null
) {
    var isLoading by remember { mutableStateOf(false) }
    
    val quizzes = if (quizViewModel != null) {
        quizViewModel.quizzes.value
    } else {
        emptyList()
    }
    
    // Load quizzes when screen loads
    LaunchedEffect(Unit) {
        if (quizViewModel != null && quizzes.isEmpty()) {
            quizViewModel.loadQuizzes()
        }
    }
    
    // Function to navigate to flashcard view
    val navigateToFlashcards = {
        if (!quizId.isNullOrBlank() && quizViewModel != null) {
            isLoading = true
            try {
                val quizIdInt = quizId.toIntOrNull()
                if (quizIdInt != null) {
                    // Find the quiz by ID
                    val quiz = quizViewModel.getQuizById(quizIdInt)
                    if (quiz != null) {
                        // Set the quiz in navigation holder
                        QuizNavigationHolder.selectedQuiz = quiz
                        // Navigate to flashcard screen
                        nav.navigate("flashcard/$quizId")
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    Surface(Modifier.fillMaxSize(), color = Color(0xFF6B5BFF)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Successfully", color = Color.White, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(24.dp))

            Box(
                Modifier
                    .size(140.dp)
                    .background(Color(0xFFFF974A), RoundedCornerShape(70.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("Your quiz has been created", color = Color.White, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(48.dp))
            
            // View Quiz Button (only show if quizId and quizViewModel are available)
            if (!quizId.isNullOrBlank() && quizViewModel != null) {
                Button(
                    onClick = { navigateToFlashcards() },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF974A)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("View Quiz", color = Color.White)
                }
                Spacer(Modifier.height(12.dp))
            }
            
            Spacer(Modifier.height(15.dp))
            Button(
                onClick = onBackToMenu,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to menu")
            }
        }
    }
}

