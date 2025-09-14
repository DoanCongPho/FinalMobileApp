package com.example.finalproject.review.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.journey.viewmodel.QuizViewModel
import com.example.finalproject.journey.model.Quiz

// Function to get a cute emoji based on quiz ID
private fun getCuteEmojiForQuiz(quizId: Int): String {
    val cuteEmojis = listOf(
        "🐱", "🐶", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", 
        "🦄", "🌈", "⭐", "🌟", "✨", "💫", "🎨", "📚",
        "🍭", "🧸", "🎪", "🎭", "🎨", "🎯", "🎮", "🎲",
        "🌸", "🌺", "🌻", "🌷", "🌹", "💐", "🦋", "🐝"
    )
    return cuteEmojis[quizId % cuteEmojis.size]
}

@Composable
private fun QuizRow(quiz: Quiz, onClick: () -> Unit, onDelete: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // delete badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF2748))
                    .clickable(
                        onClick = {
                            onDelete()
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("×", color = Color.White, fontWeight = FontWeight.Black)
            }

            Spacer(Modifier.width(10.dp))

            // Quiz icon with cute emoji
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCuteEmojiForQuiz(quiz.id),
                    fontSize = 24.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    quiz.title ?: "Untitled Quiz",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${quiz.questions.size} questions • Created ${quiz.createdAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF736DAA)
                )
            }

            Icon(Icons.Filled.ArrowForwardIos, null, tint = Color(0xFFB0ACD6))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewRoute(
    onBack: () -> Unit,
    onCreateQuiz: () -> Unit,
    onFindFriends: () -> Unit,
    onShowQuiz: (Quiz) -> Unit,
    quizViewModel: QuizViewModel
) {
    val quizzes by quizViewModel.quizzes
    val isQuizLoading by quizViewModel.isLoading
    val errorMessage by quizViewModel.errorMessage
    
    // Load quizzes when screen opens
    LaunchedEffect(Unit) {
        quizViewModel.loadQuizzes()
    }
    
    ReviewScreen(
        quizzes = quizzes,
        isQuizLoading = isQuizLoading,
        errorMessage = errorMessage,
        onBack = onBack,
        onRetry = { quizViewModel.loadQuizzes() },
        onCreateQuiz = onCreateQuiz,
        onFindFriends = onFindFriends,
        onShowQuiz = onShowQuiz,
        onDeleteQuiz = { quizId -> quizViewModel.deleteQuiz(quizId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    quizzes: List<Quiz>,
    isQuizLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onCreateQuiz: () -> Unit,
    onFindFriends: () -> Unit,
    onShowQuiz: (Quiz) -> Unit,
    onDeleteQuiz: (Int) -> Unit
) {
    val featuredBg = Brush.linearGradient(
        listOf(Color(0xFF7E6BFF), Color(0xFF9D86FF), Color(0xFFBBA5FF))
    )
    val cardBg = Brush.linearGradient(listOf(Color(0xFFEEE7FF), Color(0xFFF6F3FF)))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Review") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        when {
            isQuizLoading && quizzes.isEmpty() -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            errorMessage != null && quizzes.isEmpty() -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onRetry) { Text("Retry") }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .background(Color(0xFFF7F3FF)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    item {
                        Button(
                            onClick = onCreateQuiz,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFA21F),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Create Quiz", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Recent Quiz section - simplified without fake data
                    item {
                        Surface(shape = RoundedCornerShape(16.dp), color = Color.Transparent) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(cardBg)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "YOUR PROGRESS",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF9A8CC6)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "📚 ${quizzes.size} Quizzes Created",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Surface(shape = RoundedCornerShape(16.dp), color = Color.Transparent) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(featuredBg)
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "FEATURED",
                                    color = Color(0xFFE7DBFF),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Take part in challenges\nwith friends or other\nplayers",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = onFindFriends,
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF5B53D6)
                                    )
                                ) { Text("Find Friends") }
                            }
                        }
                    }

                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Your Quizzes", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    
                    // Show quizzes
                    if (isQuizLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF5B53D6),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    } else if (errorMessage != null) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Error loading quizzes",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFFD32F2F)
                                    )
                                    Text(
                                        errorMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Button(
                                        onClick = onRetry,
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                    } else {
                        items(quizzes, key = { it.id }) { quiz ->
                            QuizRow(
                                quiz = quiz,
                                onClick = { onShowQuiz(quiz) },
                                onDelete = { onDeleteQuiz(quiz.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PercentageBadge(percent: Int) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFB8C6)),
        contentAlignment = Alignment.Center
    ) {
        Text("$percent%", color = Color(0xFFB0325A), fontWeight = FontWeight.Bold)
    }
}

