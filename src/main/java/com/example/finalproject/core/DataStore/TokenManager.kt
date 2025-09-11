package com.example.finalproject.core.DataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("auth_prefs")

class TokenManager private constructor(private val context: Context) {

    companion object {
        private var INSTANCE: TokenManager? = null
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val USER_ID = intPreferencesKey("user_id") // thêm user id

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // Flow token
    val accessToken: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[ACCESS_TOKEN] }

    // Flow user id
    val userId: Flow<Int?> = context.dataStore.data
        .map { prefs -> prefs[USER_ID] }

    // Lưu token
    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = token
        }
    }

    // Lưu user id
    suspend fun saveUserId(id: Int) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
        }
    }

    // Xoá token + user id
    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
