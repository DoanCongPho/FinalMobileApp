
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
        private val USER_ID = intPreferencesKey("user_id")


        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHONE = stringPreferencesKey("user_phone")

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }


    val accessToken: Flow<String?> = context.dataStore.data.map { prefs -> prefs[ACCESS_TOKEN] }
    val userId: Flow<Int?> = context.dataStore.data.map { prefs -> prefs[USER_ID] }
    val userName: Flow<String?> = context.dataStore.data.map { prefs -> prefs[USER_NAME] }
    val userEmail: Flow<String?> = context.dataStore.data.map { prefs -> prefs[USER_EMAIL] }
    val userPhone: Flow<String?> = context.dataStore.data.map { prefs -> prefs[USER_PHONE] }


    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs -> prefs[ACCESS_TOKEN] = token }
    }

    suspend fun saveUserId(id: Int) {
        context.dataStore.edit { prefs -> prefs[USER_ID] = id }
    }

    suspend fun saveUserProfile(name: String?, email: String?, phone: String?) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name ?: ""
            prefs[USER_EMAIL] = email ?: ""
            prefs[USER_PHONE] = phone ?: ""
        }
    }


    suspend fun clear() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}