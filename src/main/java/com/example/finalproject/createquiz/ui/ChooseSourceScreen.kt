//package com.example.finalproject.createquiz.ui
//
//import android.content.Intent
//import android.provider.OpenableColumns
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.finalproject.createquiz.model.QuizSource
//import com.example.finalproject.createquiz.ui.components.SourceRow
//import com.example.finalproject.createquiz.ui.components.StepDots
//import com.example.finalproject.createquiz.viewmodel.CreateQuizViewModel
//
//@Composable
//fun ChooseSourceScreen(
//    nav: NavController,
//    vm: CreateQuizViewModel = viewModel()
//) {
//    val ui by vm.ui.collectAsState()
//    val ctx = LocalContext.current
//
//    val picker = rememberLauncherForActivityResult(
//        ActivityResultContracts.OpenMultipleDocuments()
//    ) { uris ->
//        val items = uris.mapNotNull { uri ->
//            val mime = ctx.contentResolver.getType(uri) ?: "application/octet-stream"
//            val name = ctx.contentResolver.query(uri, null, null, null, null)?.use { c ->
//                val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (idx >= 0 && c.moveToFirst()) c.getString(idx) else "file"
//            } ?: "file"
//            try { ctx.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
//            QuizSource(uri.toString(), name, mime)
//        }
//        vm.addSources(items)
//    }
//
//    Surface(Modifier.fillMaxSize(), color = Color(0xFF6B5BFF)) {
//        Column(Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                IconButton(onClick = { nav.popBackStack() }) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
//                }
//                Spacer(Modifier.width(8.dp))
//                Text("CreateQuiz", color = Color.White, style = MaterialTheme.typography.titleLarge)
//            }
//
//            Spacer(Modifier.height(16.dp))
//
//            Surface(shape = RoundedCornerShape(28.dp), color = Color.White,
//                modifier = Modifier.fillMaxWidth().weight(1f)) {
//                Column(Modifier.fillMaxSize().padding(20.dp)) {
//                    Text("Choose Your Source",
//                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
//                        color = Color(0xFF3C2A7D))
//
//                    Spacer(Modifier.height(16.dp))
//
//                    if (ui.sources.isEmpty()) {
//                        Text("No files selected.", color = Color.Gray)
//                    } else {
//                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                            items(ui.sources) { src ->
//                                SourceRow(
//                                    name = src.displayName,
//                                    sub = src.mime.uppercase(),
//                                    onRemove = { vm.removeSource(src.uriString) }
//                                )
//                            }
//                        }
//                    }
//
//                    Spacer(Modifier.height(12.dp))
//                    OutlinedButton(
//                        onClick = {
//                            picker.launch(arrayOf(
//                                "application/pdf",
//                                "application/msword",
//                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
//                            ))
//                        },
//                        shape = RoundedCornerShape(16.dp)
//                    ) { Text("Add files") }
//                }
//            }
//
//            Spacer(Modifier.height(16.dp))
//            StepDots(current = 0, total = 2)
//            Spacer(Modifier.height(16.dp))
//
//            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
//                Box(
//                    modifier = Modifier.size(96.dp).background(
//                        if (ui.sources.isNotEmpty()) Color(0xFFFF974A) else Color(0xFFFFC79F),
//                        CircleShape
//                    ).clickable(enabled = ui.sources.isNotEmpty()) { nav.navigate("create_quiz/prompt") },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
//                }
//            }
//        }
//    }
//}
