package com.example.finalproject.pomodoro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finalproject.pomodoro.model.PomodoroPhase
import com.example.finalproject.pomodoro.ui.components.*
import com.example.finalproject.pomodoro.viewmodel.PomodoroViewModel
import androidx.compose.ui.res.painterResource
import com.example.finalproject.R

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    navController: NavController,
    vm: PomodoroViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.prepareMusic() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pomodoro") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(56.dp) // larger tap area
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_button),
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp) // larger icon visual
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.settings_button),
                            contentDescription = "Settings",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

            )
        }
    ) { inner ->
        Column(
            modifier = Modifier.padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val mm = ui.remainingSec / 60
            val ss = ui.remainingSec % 60
            TimerCard(
                phaseLabel = when (ui.phase) {
                    PomodoroPhase.Focus -> "Focus"
                    PomodoroPhase.ShortBreak -> "Short Break"
                    PomodoroPhase.LongBreak -> "Long Break"
                    PomodoroPhase.Idle -> "Ready"
                },
                mmss = "%02d:%02d".format(mm, ss)
            )
            SessionControls(
                isRunning = ui.isRunning,
                onPlayPause = vm::toggleRun,
                onReset = vm::reset,
                onSwitchPhase = {
                    val next = when (ui.phase) {
                        PomodoroPhase.Focus -> PomodoroPhase.ShortBreak
                        PomodoroPhase.ShortBreak -> PomodoroPhase.Focus
                        PomodoroPhase.LongBreak -> PomodoroPhase.Focus
                        PomodoroPhase.Idle -> PomodoroPhase.Focus
                    }
                    vm.switchTo(next)
                }
            )
            Text(
                text = "Today’s entries: ${ui.history.count { it.phase == PomodoroPhase.Focus }} focus sessions",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showSettings) {
        SettingsSheet(
            initial = ui.settings,
            onSave = vm::saveSettings,
            onDismiss = { showSettings = false }
        )
    }
}
