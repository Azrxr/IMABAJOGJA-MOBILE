package com.imaba.imabajogja.data.model

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class UserPreference @Inject  constructor(@ApplicationContext private val context: Context) {

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
            preferences[ROLE_KEY] = user.role
            Log.d("token", "UserPreferece: token tersimpan: ${user.token} ")
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[ROLE_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false,
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getToken(): String {
        val prev = dataStore.data.first()
        return prev[TOKEN_KEY] ?: ""
    }

    suspend fun getRole(): String {
        val prev = dataStore.data.first()
        return prev[ROLE_KEY] ?: ""

    }

    companion object {
//        @Volatile
//        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val ROLE_KEY = stringPreferencesKey("role")

//        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
//            return INSTANCE ?: synchronized(this) {
//                val instance = UserPreference(dataStore)
//                INSTANCE = instance
//                instance
//            }
//        }
//        fun create(dataStore: DataStore<Preferences>): UserPreference {
//            return UserPreference(dataStore)
//        }
    }
}