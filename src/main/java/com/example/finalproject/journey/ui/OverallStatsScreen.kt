package com.example.finalproject.journey.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.journey.model.Quiz

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallStatsScreen(
    quizzes: List<Quiz>,
    onClose: () -> Unit,
    onSwitchToDetail: () -> Unit
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Close",
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
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(end = 32.dp)
                )
                Text(
                    text = "Detail",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    modifier = Modifier.clickable { onSwitchToDetail() }
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
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Top 3 quizzes that has the most questions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Category indicators - showing actual quiz titles
                    Column(
                        modifier = Modifier.padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val topQuizzes = quizzes.sortedByDescending { it.questions.size }.take(3)
                        topQuizzes.forEachIndexed { index, quiz ->
                            CategoryIndicator(
                                name = quiz.title ?: "Untitled Quiz",
                                color = when (index) {
                                    0 -> Color(0xFFFFB74D)
                                    1 -> Color(0xFF64B5F6)
                                    2 -> Color(0xFFBA68C8)
                                    else -> Color.Gray
                                }
                            )
                        }
                    }
                    
                    // Bar Chart
                    BarChart(quizzes = quizzes.sortedByDescending { it.questions.size }.take(3))
                }
            }
        }
    }
}

@Composable
fun CategoryIndicator(name: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 1, // Single line with ellipsis for long titles
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BarChart(quizzes: List<Quiz>) {
    val maxQuestions = quizzes.maxOfOrNull { it.questions.size } ?: 1
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        quizzes.forEachIndexed { index, quiz ->
            BarItem(
                questionCount = quiz.questions.size,
                maxQuestions = maxQuestions,
                color = when (index) {
                    0 -> Color(0xFFFFB74D)
                    1 -> Color(0xFF64B5F6)
                    2 -> Color(0xFFBA68C8)
                    else -> Color.Gray
                },
                label = quiz.questions.size.toString()
            )
        }
    }
}

@Composable
fun BarItem(
    questionCount: Int,
    maxQuestions: Int,
    color: Color,
    label: String
) {
    val barHeight = (questionCount.toFloat() / maxQuestions.toFloat() * 150).dp
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(barHeight)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
