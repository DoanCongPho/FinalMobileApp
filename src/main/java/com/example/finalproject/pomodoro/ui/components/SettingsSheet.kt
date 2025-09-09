package com.example.finalproject.pomodoro.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalproject.pomodoro.model.PomodoroSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    initial: PomodoroSettings,
    onSave: (PomodoroSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var focus by remember { mutableStateOf(initial.focusMinutes.toString()) }
    var short by remember { mutableStateOf(initial.shortBreakMinutes.toString()) }
    var long by remember { mutableStateOf(initial.longBreakMinutes.toString()) }
    var cycles by remember { mutableStateOf(initial.cyclesBeforeLongBreak.toString()) }
    var target by remember { mutableStateOf(initial.dailyTargetCycles.toString()) }
    var music by remember { mutableStateOf(initial.playCalmMusic) }
    var auto by remember { mutableStateOf(initial.autoStartNextPhase) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Pomodoro Settings", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(focus, { focus = it }, label = { Text("Focus (min)") })
            OutlinedTextField(short, { short = it }, label = { Text("Short break (min)") })
            OutlinedTextField(long,  { long  = it }, label = { Text("Long break (min)") })
            OutlinedTextField(cycles,{ cycles= it }, label = { Text("Cycles before long break") })
            OutlinedTextField(target,{ target= it }, label = { Text("Daily target cycles/day") })
            Row { Checkbox(music, { music = it }); Text("Calm music during focus") }
            Row { Checkbox(auto, { auto = it }); Text("Auto start next phase") }
            Button(
                onClick = {
                    onSave(
                        PomodoroSettings(
                            focus.toIntOrNull() ?: 25,
                            short.toIntOrNull() ?: 5,
                            long.toIntOrNull() ?: 15,
                            cycles.toIntOrNull() ?: 4,
                            target.toIntOrNull() ?: 8,
                            music, auto
                        )
                    ); onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}
