package com.example.finalproject.createquiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finalproject.createquiz.viewmodel.ManualQuizViewModel
import com.example.finalproject.navigation.Screen

@Composable
fun ManualQuizScreen(
    navController: NavController,
    //onFinish: () -> Unit,
    onBack: () -> Unit,
    vm: ManualQuizViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    Surface(Modifier.fillMaxSize(), color = Color(0xFF6B5BFF)) {
        Column(Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
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
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        "Create manually",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color(0xFF3C2A7D)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Add questions and mark the correct answer.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
                        items(ui.items, key = { it.id }) { q ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF7F8FC))
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    OutlinedTextField(
                                        value = q.question,
                                        onValueChange = { vm.setQuestionText(q.id, it) },
                                        label = { Text("Question") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(8.dp))

                                    q.answers.forEachIndexed { idx, ans ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            RadioButton(
                                                selected = q.correctIndex == idx,
                                                onClick = { vm.setCorrect(q.id, idx) }
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedTextField(
                                                value = ans,
                                                onValueChange = { vm.setAnswerText(q.id, idx, it) },
                                                label = { Text("Answer ${idx + 1}") },
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            IconButton(
                                                onClick = { vm.removeAnswer(q.id, idx) },
                                                enabled = q.answers.size > 2
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Remove answer")
                                            }
                                        }
                                        Spacer(Modifier.height(6.dp))
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = { vm.addAnswer(q.id) },
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null)
                                            Spacer(Modifier.width(6.dp))
                                            Text("Add answer")
                                        }
                                        OutlinedButton(
                                            onClick = { vm.removeQuestion(q.id) },
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null)
                                            Spacer(Modifier.width(6.dp))
                                            Text("Remove question")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { vm.addQuestion() },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Add question")
                        }
                        Button(
                            onClick = {
                                navController.navigate("create_quiz/success?quizId=manual_dev") {
                                    popUpTo(Screen.CreateQuizRoot.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            enabled = true, //vm.canFinish()
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Finish")
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
