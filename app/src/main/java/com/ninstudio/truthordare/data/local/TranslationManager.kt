package com.ninstudio.truthordare.data.local

import android.content.Context
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import android.util.Log

class TranslationManager(context: Context) {
    
    // Cache in SharedPreferences for simplicity in this setup
    private val prefs = context.getSharedPreferences("translation_cache", Context.MODE_PRIVATE)

    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.VIETNAMESE)
        .build()

    private val englishVietnameseTranslator = Translation.getClient(options)

    suspend fun translateToVietnamese(id: String, text: String): String {
        // Check cache first
        val cached = prefs.getString(id, null)
        if (cached != null) {
            Log.d("TranslationManager", "Returning cached translation for $id")
            return cached
        }

        return try {
            // Ensure model is downloaded
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            
            englishVietnameseTranslator.downloadModelIfNeeded(conditions).await()
            
            val result = englishVietnameseTranslator.translate(text).await()
            
            // Save to cache
            prefs.edit().putString(id, result).apply()
            
            Log.d("TranslationManager", "Translated and cached: $result")
            result
        } catch (e: Exception) {
            Log.e("TranslationManager", "Translation failed", e)
            text // Fallback to original text
        }
    }
}
