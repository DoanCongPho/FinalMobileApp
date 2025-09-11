package com.example.finalproject.createquiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalproject.createquiz.viewmodel.ManualQuizViewModel
import com.example.finalproject.createquiz.model.ManualQuestion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualQuizScreen(
    viewModel: ManualQuizViewModel,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Quiz • Manually") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Input area
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.draftQuestion,
                    onValueChange = viewModel::onQuestionChange,
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth(),
                    // No KeyboardOptions — keep multi-line via minLines
                    singleLine = false,
                    minLines = 2
                )

                OutlinedTextField(
                    value = state.draftAnswer,
                    onValueChange = viewModel::onAnswerChange,
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    // No KeyboardOptions — keep multi-line via minLines
                    singleLine = false,
                    minLines = 2
                )

                if (state.showEmptyError) {
                    Text(
                        "Please enter both a question and an answer.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { viewModel.addFlashcard() }) {
                        Text("Add card")
                    }
                    OutlinedButton(onClick = {
                        viewModel.onQuestionChange("")
                        viewModel.onAnswerChange("")
                    }) {
                        Text("Clear")
                    }
                }
            }

            // List preview + Finish
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Flashcards (${state.items.size})", style = MaterialTheme.typography.titleMedium)
                if (state.items.isEmpty()) {
                    Text("No cards yet. Add at least one to finish.")
                } else {
                    FlashcardList(
                        items = state.items,
                        onDelete = { viewModel.removeFlashcard(it) }
                    )
                }

                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = state.canSubmit
                ) {
                    Text("Finish")
                }
            }
        }
    }
}

@Composable
private fun FlashcardList(
    items: List<ManualQuestion>,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { card ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Q:", style = MaterialTheme.typography.labelMedium)
                    Text(card.question, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("A:", style = MaterialTheme.typography.labelMedium)
                    Text(card.answer, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onDelete(card.id) }) { Text("Remove") }
                    }
                }
            }
        }
    }
}
