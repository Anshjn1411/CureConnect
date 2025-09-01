package com.project.cureconnect.data.datastore.UserSessionLayer

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.project.cureconnect.presentation.screens.AuthScreen.User
import com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet.Doctor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable


import kotlinx.serialization.encodeToString

import kotlinx.serialization.json.Json
private val Context.dataStore by preferencesDataStore("user_session")

@Serializable
data class CachedUser(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val role :String="",
)



class UserSessionManager(private val context: Context) {

    companion object {
        private val USER_JSON = stringPreferencesKey("user_json")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val userData: Flow<CachedUser?> = context.dataStore.data.map { preferences ->
        preferences[USER_JSON]?.let {
            try {
                Json.decodeFromString<CachedUser>(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    suspend fun saveUser(user: User ) {
        Log.d("UserSessionManager", "Saving user: $user")
        val cached = CachedUser(
            uid = user.uid,
            name = user.name,
            email = user.email,
            phone = user.phone,
            role = user.role
        )
        val json = Json.encodeToString(cached)
        context.dataStore.edit {
            it[USER_JSON] = json
            it[IS_LOGGED_IN] = true
        }
    }

    suspend fun saveDoctor(user: Doctor) {
        val cached = CachedUser(
            uid = user.uid,
            name = user.name,
            email = user.email,
            phone = user.phoneNumber,
            role = user.role
        )
        val json = Json.encodeToString(cached)
        context.dataStore.edit {
            it[USER_JSON] = json
            it[IS_LOGGED_IN] = true
        }
    }


    suspend fun clearUser() {
        context.dataStore.edit {
            it.clear()
        }
    }

    suspend fun clearDoctor() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
