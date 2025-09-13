package com.example.finalproject.createquiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finalproject.createquiz.model.Difficulty
import com.example.finalproject.createquiz.ui.components.StepDots
import com.example.finalproject.createquiz.viewmodel.CreateQuizViewModel
import com.example.finalproject.navigation.Screen

@Composable
fun PromptScreen(
    nav: NavController,
    vm: CreateQuizViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    Surface(Modifier.fillMaxSize(), color = Color(0xFF6B5BFF)) {
        Column(Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text("CreateQuiz", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Column(Modifier.fillMaxSize().padding(20.dp)) {
                    Text(
                        "How you want your quiz be generated ?",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color(0xFF3C2A7D)
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = ui.prompt,
                        onValueChange = vm::setPrompt,
                        placeholder = { Text("Prompt here") },
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("Difficulty", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        FilterChip(
                            selected = ui.difficulty == Difficulty.EASY,
                            onClick = { vm.setDifficulty(Difficulty.EASY) },
                            label = { Text("Easy") }
                        )
                        FilterChip(
                            selected = ui.difficulty == Difficulty.MEDIUM,
                            onClick = { vm.setDifficulty(Difficulty.MEDIUM) },
                            label = { Text("Medium") }
                        )
                        FilterChip(
                            selected = ui.difficulty == Difficulty.HARD,
                            onClick = { vm.setDifficulty(Difficulty.HARD) },
                            label = { Text("Hard") }
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Number of questions: ${ui.numQuestions}", fontWeight = FontWeight.Bold)
                    Slider(
                        value = ui.numQuestions.toFloat(),
                        onValueChange = { vm.setNumQuestions(it.toInt()) },
                        valueRange = 5f..50f,
                        steps = 44
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            StepDots(current = 1, total = 2)
            Spacer(Modifier.height(16.dp))

            // TEST-ONLY: jump straight to Success screen (bypass API)
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(96.dp)
                        .background(Color(0xFFFF974A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        // Keep the route shape consistent with your NavHost
                        nav.navigate("create_quiz/success?quizId=dev_test") {
                            // pop within the create_quiz graph, but keep the graph itself
                            popUpTo(Screen.CreateQuizRoot.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                    
                    // Progress message during creation
                    if (ui.isSubmitting) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Creating quiz from file...\nThis may take a few minutes",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            
            // Error message
            ui.error?.let { errorMsg ->
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
