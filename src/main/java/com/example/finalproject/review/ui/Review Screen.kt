package com.example.finalproject.review.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.review.model.ReviewItem
import com.example.finalproject.review.model.ReviewItemType
import com.example.finalproject.review.viewmodel.ReviewUiState
import com.example.finalproject.review.viewmodel.ReviewViewModel
import com.example.finalproject.review.viewmodel.ReviewViewModelFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewRoute(
    onBack: () -> Unit,
    onCreateQuiz: () -> Unit,
    onFindFriends: () -> Unit,
    factory: ReviewViewModelFactory,
    vm: ReviewViewModel = viewModel(factory = factory)
) {
    val state by vm.ui.collectAsStateWithLifecycle()
    ReviewScreen(
        state = state,
        onBack = onBack,
        onRetry = vm::refresh,
        onCreateQuiz = onCreateQuiz,
        onFindFriends = onFindFriends,
        onOpenItem = { /* open */ },
        onDelete = { id -> vm.removeItem(id) } // NEW
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    state: ReviewUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onCreateQuiz: () -> Unit,
    onFindFriends: () -> Unit,
    onOpenItem: (ReviewItem) -> Unit,
    onDelete: (String) -> Unit
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
            state.loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            state.error != null -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.error, color = MaterialTheme.colorScheme.error)
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

                    state.recent?.let { recent ->
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
                                            "RECENT QUIZ",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color(0xFF9A8CC6)
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "🎧  ${recent.title}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    PercentageBadge(recent.progressPercent)
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
                            Text("Available", style = MaterialTheme.typography.titleMedium)
                            TextButton(onClick = { /* show all */ }) { Text("See all") }
                        }
                    }

                    items(state.items, key = { it.id }) { item ->
                        ReviewRow(item = item, onClick = { onOpenItem(item) }, onDelete = {onDelete(item.id)})
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

@Composable
private fun ReviewRow(item: ReviewItem, onClick: () -> Unit, onDelete: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
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
                        onClick = onDelete,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("×", color = Color.White, fontWeight = FontWeight.Black)
            }

            Spacer(Modifier.width(10.dp))

            val icon = when (item.type) {
                ReviewItemType.Quiz -> Icons.Filled.BarChart
                ReviewItemType.Document -> Icons.Filled.Calculate
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0ECFF)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = Color(0xFF5B53D6)) }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF736DAA))
            }

            Icon(Icons.Filled.ArrowForwardIos, null, tint = Color(0xFFB0ACD6))
        }
    }
}
