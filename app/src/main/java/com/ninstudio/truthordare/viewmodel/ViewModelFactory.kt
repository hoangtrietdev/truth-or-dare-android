package com.ninstudio.truthordare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ninstudio.truthordare.data.local.SettingsManager
import com.ninstudio.truthordare.data.local.TranslationManager
import com.ninstudio.truthordare.data.repository.QuestionRepository

class MainViewModelFactory(
    private val repository: QuestionRepository,
    private val settingsManager: SettingsManager,
    private val translationManager: TranslationManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, settingsManager, translationManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
