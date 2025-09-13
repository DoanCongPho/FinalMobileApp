package com.example.finalproject.createquiz.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizApiTestScreen() {
    val context = LocalContext.current
    var isRunning by remember { mutableStateOf(false) }
    var testResults by remember { mutableStateOf<List<String>>(emptyList()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz API Testing") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quiz Backend API Testing",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "This will test both manual quiz creation and retrieving quizzes from the backend.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Button(
                onClick = {
                    isRunning = true
                    testResults = emptyList()
                    
                    runQuizApiTests(context) { results ->
                        testResults = results
                        isRunning = false
                    }
                },
                enabled = !isRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Running Tests...")
                } else {
                    Text("🧪 Run API Tests", fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (testResults.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Test Results:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        LazyColumn {
                            items(testResults.withIndex().toList()) { (index, result) ->
                                val testNames = listOf(
                                    "🔐 Authentication Check",
                                    "📋 Get Existing Quizzes", 
                                    "➕ Create Manual Quiz",
                                    "🔄 Verify New Quiz Created"
                                )
                                
                                TestResultItem(
                                    testName = testNames.getOrElse(index) { "Test ${index + 1}" },
                                    result = result,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestResultItem(
    testName: String,
    result: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                result.startsWith("SUCCESS") -> Color(0xFFE8F5E8)
                result.startsWith("FAILED") -> Color(0xFFFFF0F0) 
                result.startsWith("EXCEPTION") -> Color(0xFFFFF8E1)
                else -> Color(0xFFF5F5F5)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = testName,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            
            Text(
                text = result,
                fontSize = 12.sp,
                color = when {
                    result.startsWith("SUCCESS") -> Color(0xFF2E7D32)
                    result.startsWith("FAILED") -> Color(0xFFD32F2F)
                    result.startsWith("EXCEPTION") -> Color(0xFFED6C02)
                    else -> Color.Gray
                },
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
