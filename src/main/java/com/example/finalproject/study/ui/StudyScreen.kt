package com.example.finalproject.study.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.study.ui.components.*
import com.example.finalproject.study.viewmodel.StudyViewModel
import com.example.finalproject.study.viewmodel.StudyViewModelFactory

@Composable
fun StudyScreen(
    navController: NavController,
    vm: StudyViewModel = viewModel(factory = StudyViewModelFactory()),
    onOpenGrid: () -> Unit = {}
) {
    val ui by vm.ui.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF6B5BFF) // purple background like mock
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
        ) {
            GreetingHeader(
                greeting = "GOOD MORNING",
                name = ui.displayName,
                avatar = painterResource(id = R.drawable.study_mate_header)
            )

            // Quote card with gradient + illustration
            QuoteCard(
                text = ui.quote?.text ?: "A Quote Here",
                modifier = Modifier.padding(top = 16.dp)
            )

            TopicChips(
                topics = ui.topics,
                selectedIds = ui.selectedTopicIds,
                onToggle = vm::toggleTopic,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(Modifier.height(60.dp))

            BigActionButton(
                label = "Review",
                onClick = { navController.navigate("review") },
                modifier = Modifier.padding(top = 24.dp)
            )
            BigActionButton(
                label = "Pomodoro",
                onClick = { navController.navigate("pomodoro") },
                modifier = Modifier.padding(top = 24.dp)
            )
            BigActionButton(
                label = "Your Journey",
                onClick = { navController.navigate("quiz") },
                modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
            )
        }
    }
}
