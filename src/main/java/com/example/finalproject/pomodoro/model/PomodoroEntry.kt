package com.example.finalproject.pomodoro.model
data class PomodoroEntry(
    val timestampMs: Long,
    val phase: PomodoroPhase,
    val plannedMinutes: Int,
    val completedMinutes: Int
)
