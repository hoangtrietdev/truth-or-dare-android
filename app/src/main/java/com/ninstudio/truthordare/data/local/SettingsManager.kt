package com.ninstudio.truthordare.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val RATING_KEY = stringPreferencesKey("rating")
        val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { it[LANGUAGE_KEY] ?: "en" }
    val ratingFlow: Flow<String> = context.dataStore.data.map { it[RATING_KEY] ?: "normal" }
    val isOnboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED_KEY] ?: false }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = language }
    }

    suspend fun saveRating(rating: String) {
        context.dataStore.edit { it[RATING_KEY] = rating }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED_KEY] = completed }
    }
}
