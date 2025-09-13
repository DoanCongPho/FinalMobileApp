package com.example.finalproject.journey.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalproject.journey.data.QuizRepository
import com.example.finalproject.journey.model.Quiz
import com.example.finalproject.journey.viewmodel.QuizViewModel
import com.example.finalproject.journey.viewmodel.QuizViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class FilterPeriod {
    DAILY, WEEKLY, MONTHLY, YEARLY, ALL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    onQuizClick: (Quiz) -> Unit,
    onSwitchToStats: () -> Unit,
    onBack: () -> Unit,
    viewModel: QuizViewModel
) {
    val quizzes by viewModel.quizzes
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    // Filter state
    var selectedFilter by remember { mutableStateOf(FilterPeriod.ALL) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    
    // Filter quizzes based on selected period
    val filteredQuizzes = remember(quizzes, selectedFilter) {
        filterQuizzesByPeriod(quizzes, selectedFilter)
    }

    LaunchedEffect(Unit) {
        viewModel.loadQuizzes()
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(horizontal = 24.dp),
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        containerColor = Color(0xFF7C4DFF) // Purple background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Avatar Section
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF7C4DFF)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User Name
            Text(
                text = "Endy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tab Row (Stats and Detail)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Stats",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(end = 32.dp)
                        .clickable { onSwitchToStats() }
                )
                Text(
                    text = "Detail",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // White Content Card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Filter Dropdown at top right
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = it },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .menuAnchor()
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = selectedFilter.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Expand",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                FilterPeriod.values().forEach { period ->
                                    DropdownMenuItem(
                                        text = { 
                                            Text(period.name.lowercase().replaceFirstChar { it.uppercase() }) 
                                        },
                                        onClick = {
                                            selectedFilter = period
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Statistics Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Quiz Statistics",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Quizzes: ${filteredQuizzes.size}",
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Total Questions: ${filteredQuizzes.sumOf { it.questions.size }}",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Content Section
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        
                        errorMessage != null -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                        
                        filteredQuizzes.isEmpty() && quizzes.isNotEmpty() -> {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No quizzes found for ${selectedFilter.name.lowercase()} period.",
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 16.sp
                                )
                            }
                        }
                        
                        quizzes.isEmpty() -> {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No quizzes found. Create your first quiz!",
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 16.sp
                                )
                            }
                        }
                        
                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredQuizzes) { quiz ->
                                    QuizItem(
                                        quiz = quiz,
                                        onClick = { onQuizClick(quiz) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizItem(
    quiz: Quiz,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = quiz.title ?: "Untitled Quiz",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${quiz.questions.size} questions",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Created: ${quiz.createdAt}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun filterQuizzesByPeriod(quizzes: List<Quiz>, period: FilterPeriod): List<Quiz> {
    if (period == FilterPeriod.ALL) return quizzes

    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ISO_DATE_TIME

    return quizzes.filter { quiz ->
        val createdAt = try {
            LocalDateTime.parse(quiz.createdAt, formatter)
        } catch (e: Exception) {
            return@filter false
        }

        when (period) {
            FilterPeriod.DAILY -> ChronoUnit.DAYS.between(createdAt, now) < 1
            FilterPeriod.WEEKLY -> ChronoUnit.DAYS.between(createdAt, now) < 7
            FilterPeriod.MONTHLY -> ChronoUnit.DAYS.between(createdAt, now) < 30
            FilterPeriod.YEARLY -> ChronoUnit.DAYS.between(createdAt, now) < 365
            FilterPeriod.ALL -> true
        }
    }
}