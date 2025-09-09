package com.example.finalproject.pomodoro.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.finalproject.pomodoro.model.PomodoroEntry
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val Context.historyStore by preferencesDataStore("pomodoro_history")
private val KEY = stringPreferencesKey("entries_json")

class HistoryPrefs(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun push(entry: PomodoroEntry, keep: Int = 50) {
        val list = get().toMutableList().apply { add(0, entry) }
        val trimmed = if (list.size > keep) list.take(keep) else list
        context.historyStore.edit { it[KEY] = json.encodeToString(trimmed) }
    }

    suspend fun get(): List<PomodoroEntry> {
        val prefs = context.historyStore.data.first()
        val raw = prefs[KEY] ?: "[]"
        return runCatching { json.decodeFromString<List<PomodoroEntry>>(raw) }.getOrElse { emptyList() }
    }
}
