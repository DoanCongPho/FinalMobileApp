package com.example.finalproject.review.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import com.example.finalproject.journey.model.Quiz
import com.example.finalproject.journey.model.QuizQuestion

// Temporary storage for selected quiz (simple workaround for navigation)
object QuizNavigationHolder {
    var selectedQuiz: Quiz? = null
}

// Helper function to format mathematical text
private fun formatMathText(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var workingText = text
        
        // Handle various LaTeX patterns step by step
        
        // 1. Remove \( and \) markers (inline math delimiters)
        workingText = workingText.replace("\\\\\\(", "").replace("\\\\\\)", "")
        
        // 2. Handle \frac{numerator}{denominator} -> (numerator)/(denominator)
        val fractionPattern = "\\\\frac\\{([^{}]+(?:\\{[^{}]*\\}[^{}]*)*)\\}\\{([^{}]+(?:\\{[^{}]*\\}[^{}]*)*)\\}".toRegex()
        workingText = fractionPattern.replace(workingText) { match ->
            val numerator = match.groupValues[1]
            val denominator = match.groupValues[2]
            "($numerator)/($denominator)"
        }
        
        // 3. Handle \sqrt{content} -> √(content)
        val sqrtPattern = "\\\\sqrt\\{([^{}]+(?:\\{[^{}]*\\}[^{}]*)*)\\}".toRegex()
        workingText = sqrtPattern.replace(workingText) { match ->
            val content = match.groupValues[1]
            "√($content)"
        }
        
        // 4. Handle \mathbf{content} or \mathbf content -> content (bold)
        val mathbfPattern = "\\\\mathbf\\s*\\{?([A-Za-z_]+)\\}?".toRegex()
        workingText = mathbfPattern.replace(workingText) { match ->
            match.groupValues[1]
        }
        
        // 5. Handle subscripts: _{content} -> ₊content₋ (we'll style these later)
        val subscriptPattern = "_\\{([^{}]+)\\}".toRegex()
        workingText = subscriptPattern.replace(workingText) { match ->
            "₊${match.groupValues[1]}₋"
        }
        
        // 6. Handle single character subscripts: _x -> ₊x₋
        val singleSubscriptPattern = "_([a-zA-Z0-9+\\-=])".toRegex()
        workingText = singleSubscriptPattern.replace(workingText) { match ->
            "₊${match.groupValues[1]}₋"
        }
        
        // 7. Handle \hat{content} -> content̂
        val hatPattern = "\\\\hat\\s*\\{?([A-Za-z_]+)\\}?".toRegex()
        workingText = hatPattern.replace(workingText) { match ->
            "${match.groupValues[1]}̂"
        }
        
        // 8. Handle \cdot -> ·
        workingText = workingText.replace("\\\\cdot", "·")
        
        // 9. Handle \epsilon -> ε
        workingText = workingText.replace("\\\\epsilon", "ε")
        
        // 10. Handle \eta -> η
        workingText = workingText.replace("\\\\eta", "η")
        
        // 11. Handle \theta -> θ
        workingText = workingText.replace("\\\\theta", "θ")
        
        // 12. Handle \sum -> Σ
        workingText = workingText.replace("\\\\sum", "Σ")
        
        // Now build the annotated string with proper styling
        var currentIndex = 0
        val subscriptMarkerPattern = "₊([^₋]+)₋".toRegex()
        val subscriptMatches = subscriptMarkerPattern.findAll(workingText).toList()
        
        if (subscriptMatches.isNotEmpty()) {
            // Process text with subscripts
            subscriptMatches.forEach { match ->
                // Add text before subscript
                if (currentIndex < match.range.first) {
                    val beforeText = workingText.substring(currentIndex, match.range.first)
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(beforeText)
                    }
                }
                
                // Add subscript with smaller, slightly offset text
                withStyle(
                    style = SpanStyle(
                        fontSize = 10.sp,
                        baselineShift = androidx.compose.ui.text.style.BaselineShift(-0.3f),
                        color = Color(0xFF1976D2)
                    )
                ) {
                    append(match.groupValues[1])
                }
                
                currentIndex = match.range.last + 1
            }
            
            // Add remaining text after last subscript
            if (currentIndex < workingText.length) {
                val remainingText = workingText.substring(currentIndex)
                val cleanText = remainingText.replace("₊", "").replace("₋", "")
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append(cleanText)
                }
            }
        } else {
            // No subscripts found, just style the whole text
            val cleanText = workingText.replace("₊", "").replace("₋", "")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Medium
                )
            ) {
                append(cleanText)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    quiz: Quiz?,
    onBack: () -> Unit
) {
    if (quiz == null) {
        // Try to get quiz from the navigation holder
        val selectedQuiz = QuizNavigationHolder.selectedQuiz
        if (selectedQuiz != null) {
            FlashcardScreenContent(quiz = selectedQuiz, onBack = onBack)
        }
    } else {
        FlashcardScreenContent(quiz = quiz, onBack = onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlashcardScreenContent(
    quiz: Quiz,
    onBack: () -> Unit
) {
    var currentCardIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    
    // Reset flip state when changing cards
    LaunchedEffect(currentCardIndex) {
        isFlipped = false
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        quiz.title ?: "Quiz",
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        if (quiz.questions.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No questions available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .background(Color(0xFFF7F3FF))
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${currentCardIndex + 1} of ${quiz.questions.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5B53D6),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Flashcard
                FlipCard(
                    question = quiz.questions[currentCardIndex],
                    isFlipped = isFlipped,
                    onFlip = { isFlipped = !isFlipped },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Navigation controls
                NavigationControls(
                    currentIndex = currentCardIndex,
                    totalCount = quiz.questions.size,
                    onPrevious = {
                        if (currentCardIndex > 0) {
                            currentCardIndex--
                        }
                    },
                    onNext = {
                        if (currentCardIndex < quiz.questions.size - 1) {
                            currentCardIndex++
                        }
                    },
                    onFinish = onBack
                )
            }
        }
    }
}

@Composable
private fun FlipCard(
    question: QuizQuestion,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "flip_animation"
    )
    
    Box(
        modifier = modifier
            .aspectRatio(1.2f)
            .clickable { onFlip() }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // Front side (Question)
            CardSide(
                title = "Question",
                content = question.question,
                backgroundColor = Color(0xFFEEE7FF),
                titleColor = Color(0xFF5B53D6),
                isFlipped = false
            )
        } else {
            // Back side (Answer)
            CardSide(
                title = "Answer",
                content = question.answer,
                explanation = question.explanation,
                backgroundColor = Color(0xFFE8F5E8),
                titleColor = Color(0xFF4CAF50),
                isFlipped = true,
                modifier = Modifier.graphicsLayer {
                    rotationY = 180f
                }
            )
        }
    }
}

@Composable
private fun CardSide(
    title: String,
    content: String,
    explanation: String? = null,
    backgroundColor: Color,
    titleColor: Color,
    isFlipped: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(backgroundColor, Color.White)
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = titleColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content
                Text(
                    text = formatMathText(content),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.weight(1f),
                    color = if (isFlipped) Color(0xFF2E7D32) else Color(0xFF333333)
                )
                
                // Explanation (only on answer side)
                if (isFlipped && !explanation.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        color = Color(0xFFD1C4E9),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Explanation:",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF9A8CC6),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatMathText(explanation),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        color = Color(0xFF736DAA)
                    )
                }
            }
            
            // Tap hint
            if (!isFlipped) {
                Text(
                    text = "Tap to reveal answer",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun NavigationControls(
    currentIndex: Int,
    totalCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    val isFirstCard = currentIndex == 0
    val isLastCard = currentIndex == totalCount - 1
    
    Column {
        // Progress bar
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalCount.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF5B53D6),
            trackColor = Color(0xFFE0E0E0)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            OutlinedButton(
                onClick = onPrevious,
                enabled = !isFirstCard,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isFirstCard) Color.Gray else Color(0xFF5B53D6)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isFirstCard) Color.Gray else Color(0xFF5B53D6)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Previous",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Previous",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Next/Finish button
            Button(
                onClick = if (isLastCard) onFinish else onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLastCard) Color(0xFF4CAF50) else Color(0xFF5B53D6)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isLastCard) "Finish" else "Next",
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        if (isLastCard) Icons.Default.Check else Icons.Default.ArrowForward,
                        contentDescription = if (isLastCard) "Finish" else "Next",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        // Page dots indicator
        Spacer(modifier = Modifier.height(16.dp))
        PageDots(
            currentPage = currentIndex,
            totalPages = totalCount,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PageDots(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    // Limit number of dots shown for better UI
    val maxDotsToShow = 7
    val showDots = totalPages <= maxDotsToShow
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showDots) {
            // Show all dots if total is reasonable
            repeat(totalPages) { index ->
                val isSelected = index == currentPage
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 12.dp else 8.dp)
                        .background(
                            color = if (isSelected) Color(0xFF5B53D6) else Color(0xFFE0E0E0),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .animateContentSize()
                )
                if (index < totalPages - 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        } else {
            // Show condensed indicator for many pages
            Text(
                text = "${currentPage + 1} / $totalPages",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium
            )
        }
    }
}