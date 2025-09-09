package com.example.finalproject.pomodoro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.finalproject.pomodoro.data.HistoryPrefs
import com.example.finalproject.pomodoro.data.SettingsDataStore
import com.example.finalproject.pomodoro.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max


data class PomodoroUiState(
    val phase: PomodoroPhase = PomodoroPhase.Idle,
    val remainingSec: Int = 0,
    val settings: PomodoroSettings = PomodoroSettings(),
    val isRunning: Boolean = false,
    val history: List<PomodoroEntry> = emptyList()
)

class PomodoroViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val settings = SettingsDataStore(app)
    private val historyPrefs = HistoryPrefs(app) // remove if you don't want persistence

    private val _ui = MutableStateFlow(PomodoroUiState())
    val ui: StateFlow<PomodoroUiState> = _ui.asStateFlow()

    private var ticker: Job? = null
    private val player = ExoPlayer.Builder(app).build()
    init {
        viewModelScope.launch {
            settings.flow.collect { s ->
                _ui.update { it.copy(settings = s) }  // << PomodoroSettings goes here
            }
        }
        // (Optional) load persisted history if you kept HistoryPrefs
        viewModelScope.launch {
            val h = historyPrefs.get()
            _ui.update { it.copy(history = h) }
        }
    }


    fun prepareMusic() {
        if (_ui.value.settings.playCalmMusic) {
            // Put calm_loop.mp3 in res/raw/
            val item = MediaItem.fromUri("android.resource://${getApplication<Application>().packageName}/raw/calm_loop")
            player.setMediaItem(item); player.repeatMode = ExoPlayer.REPEAT_MODE_ALL; player.prepare()
        }
    }

    fun toggleRun() { if (_ui.value.isRunning) pause() else start() }

    private fun start() {
        if (_ui.value.phase == PomodoroPhase.Idle) switchTo(PomodoroPhase.Focus)
        _ui.update { it.copy(isRunning = true) }
        if (_ui.value.settings.playCalmMusic && _ui.value.phase == PomodoroPhase.Focus) player.play()
        startTicker()
    }

    fun pause() { _ui.update { it.copy(isRunning = false) }; ticker?.cancel(); player.pause() }

    fun reset() { ticker?.cancel(); player.pause(); _ui.update { it.copy(phase = PomodoroPhase.Idle, remainingSec = 0, isRunning = false) } }

    fun switchTo(next: PomodoroPhase) {
        val s = _ui.value.settings
        val sec = when (next) {
            PomodoroPhase.Focus -> s.focusMinutes * 60
            PomodoroPhase.ShortBreak -> s.shortBreakMinutes * 60
            PomodoroPhase.LongBreak -> s.longBreakMinutes * 60
            PomodoroPhase.Idle -> 0
        }
        _ui.update { it.copy(phase = next, remainingSec = sec) }
        player.pause(); ticker?.cancel()
    }

    private fun startTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (_ui.value.isRunning && _ui.value.remainingSec > 0) {
                delay(1000)
                _ui.update { it.copy(remainingSec = max(0, it.remainingSec - 1)) }
                if (_ui.value.remainingSec == 0) onPhaseFinished()
            }
        }
    }

    private fun onPhaseFinished() {
        val was = _ui.value
        val planned = when (was.phase) {
            PomodoroPhase.Focus -> was.settings.focusMinutes
            PomodoroPhase.ShortBreak -> was.settings.shortBreakMinutes
            PomodoroPhase.LongBreak -> was.settings.longBreakMinutes
            PomodoroPhase.Idle -> 0
        }
        val entry = PomodoroEntry(
            timestampMs = System.currentTimeMillis(),
            phase = was.phase,
            plannedMinutes = planned,
            completedMinutes = planned - (was.remainingSec / 60)
        )
        viewModelScope.launch {
            // keep in memory
            _ui.update { it.copy(history = listOf(entry) + it.history) }
            // also persist in prefs (optional)
            historyPrefs.push(entry)
        }

        val next = when (was.phase) {
            PomodoroPhase.Focus -> PomodoroPhase.ShortBreak
            PomodoroPhase.ShortBreak, PomodoroPhase.LongBreak, PomodoroPhase.Idle -> PomodoroPhase.Focus
        }
        if (was.settings.autoStartNextPhase) { switchTo(next); start() } else { switchTo(next); pause() }
    }

    fun saveSettings(new: PomodoroSettings) = viewModelScope.launch { settings.save(new) }

    override fun onCleared() { super.onCleared(); player.release() }
}
