package com.example.finalproject.pomodoro.model
data class PomodoroSettings(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val cyclesBeforeLongBreak: Int = 4,
    val dailyTargetCycles: Int = 8,
    val playCalmMusic: Boolean = true,
    val autoStartNextPhase: Boolean = true
)
