package com.example.finalproject.createquiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChooseModeScreen(
    onCreateByAI: () -> Unit,
    onCreateManual: () -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = Color(0xFF6B5BFF)) {
        Column(
            Modifier.fillMaxSize().padding(16.dp).statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("CreateQuiz", color = Color.White, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "How do you want to create your quiz?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF3C2A7D)
                    )
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onCreateByAI,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Create by AI")
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onCreateManual,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Create manually")
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
