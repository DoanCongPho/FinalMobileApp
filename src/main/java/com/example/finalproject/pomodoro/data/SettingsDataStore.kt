package com.example.finalproject.pomodoro.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.finalproject.pomodoro.model.PomodoroSettings
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("pomodoro_settings")

class SettingsDataStore(private val context: Context) {
    private val FOCUS = intPreferencesKey("focus")
    private val SHORT = intPreferencesKey("short")
    private val LONG  = intPreferencesKey("long")
    private val CYCLES= intPreferencesKey("cycles")
    private val TARGET= intPreferencesKey("target")
    private val MUSIC = booleanPreferencesKey("music")
    private val AUTO  = booleanPreferencesKey("auto")

    val flow = context.dataStore.data.map { p ->
        PomodoroSettings(
            p[FOCUS] ?: 25, p[SHORT] ?: 5, p[LONG] ?: 15,
            p[CYCLES] ?: 4, p[TARGET] ?: 8, p[MUSIC] ?: true, p[AUTO] ?: true
        )
    }

    suspend fun save(s: PomodoroSettings) = context.dataStore.edit { p ->
        p[FOCUS]=s.focusMinutes; p[SHORT]=s.shortBreakMinutes; p[LONG]=s.longBreakMinutes
        p[CYCLES]=s.cyclesBeforeLongBreak; p[TARGET]=s.dailyTargetCycles
        p[MUSIC]=s.playCalmMusic; p[AUTO]=s.autoStartNextPhase
    }
}