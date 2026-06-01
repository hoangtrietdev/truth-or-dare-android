package com.ninstudio.truthordare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninstudio.truthordare.data.api.TruthOrDareApi
import com.ninstudio.truthordare.data.local.SettingsManager
import com.ninstudio.truthordare.data.local.TranslationManager
import com.ninstudio.truthordare.data.model.Question
import com.ninstudio.truthordare.data.model.QuestionRating
import com.ninstudio.truthordare.data.repository.QuestionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class GameUiState(
    val questions: List<Question> = emptyList(),
    val currentQuestion: Question? = null,
    val selectedRating: String = QuestionRating.NORMAL,
    val language: String = "en",
    val isLoading: Boolean = false,
    val isOnboardingCompleted: Boolean? = null,
    val currentType: String = "BOTH"
)

class MainViewModel(
    private val repository: QuestionRepository,
    private val settingsManager: SettingsManager,
    private val translationManager: TranslationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val seenIds = mutableListOf<String>()
    private val MAX_HISTORY_SIZE = 15

    private val api = Retrofit.Builder()
        .baseUrl("https://api.truthordarebot.xyz/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TruthOrDareApi::class.java)

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            val language = settingsManager.languageFlow.first()
            val rating = settingsManager.ratingFlow.first()
            val onboardingCompleted = settingsManager.isOnboardingCompletedFlow.first()
            val allQuestions = repository.loadQuestions()

            _uiState.value = _uiState.value.copy(
                questions = allQuestions,
                selectedRating = rating,
                language = language,
                isOnboardingCompleted = onboardingCompleted
            )
            
            if (onboardingCompleted && _uiState.value.currentQuestion == null) {
                pickLocalQuestion()
            }
        }
    }

    fun completeOnboarding(language: String, rating: String) {
        viewModelScope.launch {
            settingsManager.saveLanguage(language)
            settingsManager.saveRating(rating)
            settingsManager.setOnboardingCompleted(true)
            
            _uiState.value = _uiState.value.copy(
                language = language,
                selectedRating = rating,
                isOnboardingCompleted = true
            )
            nextQuestion()
        }
    }

    fun setRating(rating: String) {
        if (_uiState.value.selectedRating == rating) return
        viewModelScope.launch {
            settingsManager.saveRating(rating)
            _uiState.value = _uiState.value.copy(selectedRating = rating)
        }
    }

    fun setLanguage(lang: String) {
        if (_uiState.value.language == lang) return
        viewModelScope.launch {
            settingsManager.saveLanguage(lang)
            _uiState.value = _uiState.value.copy(language = lang)
            
            // Trigger translation if Vietnamese is selected and not available
            if (lang == "vi") {
                ensureVietnameseTranslation()
            }
        }
    }

    private suspend fun ensureVietnameseTranslation() {
        val current = _uiState.value.currentQuestion ?: return
        val translations = current.translations
        
        // If vi is missing or is just a fallback to English, translate it
        if (!translations.containsKey("vi") || translations["vi"] == translations["en"]) {
            val enText = translations["en"] ?: return
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val translated = translationManager.translateToVietnamese(current.id, enText)
            
            val updatedTranslations = translations.toMutableMap()
            updatedTranslations["vi"] = translated
            
            val updatedQuestion = current.copy(translations = updatedTranslations)
            _uiState.value = _uiState.value.copy(
                currentQuestion = updatedQuestion,
                isLoading = false
            )
        }
    }

    fun setType(type: String) {
        if (_uiState.value.currentType == type) return
        _uiState.value = _uiState.value.copy(currentType = type)
    }

    fun nextQuestion() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(600)

            val currentState = _uiState.value
            val apiRating = if (currentState.selectedRating == QuestionRating.ADULT) "r" else "pg"

            try {
                val apiQuestion = when (currentState.currentType.uppercase()) {
                    "TRUTH" -> api.getTruth(apiRating)
                    "DARE" -> api.getDare(apiRating)
                    else -> api.getWyr(apiRating)
                }

                val translations = (apiQuestion.translations ?: emptyMap()).toMutableMap()
                if (!translations.containsKey("en")) {
                    translations["en"] = apiQuestion.question
                }
                
                // Fallback all languages to English if they are missing
                listOf("vi", "fr", "es", "de").forEach { lang ->
                    if (!translations.containsKey(lang) || translations[lang].isNullOrBlank()) {
                        translations[lang] = translations["en"] ?: ""
                    }
                }

                val question = Question(
                    id = apiQuestion.id + "_" + System.currentTimeMillis(),
                    type = apiQuestion.type,
                    rating = if (apiQuestion.rating == "r") QuestionRating.ADULT else QuestionRating.NORMAL,
                    translations = translations
                )

                updateCurrentQuestion(question)
                
                // Auto-translate if currently in Vietnamese mode
                if (_uiState.value.language == "vi") {
                    ensureVietnameseTranslation()
                }
                
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "API Error: ${e.message}")
                pickLocalQuestion()
                if (_uiState.value.language == "vi") {
                    ensureVietnameseTranslation()
                }
            }
        }
    }

    private fun pickLocalQuestion() {
        val currentState = _uiState.value
        val filtered = currentState.questions.filter { question ->
            val matchesRating = question.rating == currentState.selectedRating
            val matchesType = when (currentState.currentType) {
                "BOTH" -> true
                else -> question.type.lowercase() == currentState.currentType.lowercase()
            }
            val baseId = question.id.substringBefore("_")
            matchesRating && matchesType && !seenIds.contains(baseId)
        }
        
        val targetList = if (filtered.isEmpty()) {
            currentState.questions.filter { question ->
                val matchesRating = question.rating == currentState.selectedRating
                val matchesType = when (currentState.currentType) {
                    "BOTH" -> true
                    else -> question.type.lowercase() == currentState.currentType.lowercase()
                }
                matchesRating && matchesType
            }
        } else {
            filtered
        }

        if (targetList.isNotEmpty()) {
            val next = targetList.random()
            
            val translations = next.translations.toMutableMap()
            val enText = translations["en"] ?: ""
            listOf("vi", "fr", "es", "de").forEach { lang ->
                if (!translations.containsKey(lang) || translations[lang].isNullOrBlank()) {
                    translations[lang] = enText
                }
            }
            
            val uniqueQuestion = next.copy(
                id = next.id + "_" + System.currentTimeMillis(),
                translations = translations
            )
            updateCurrentQuestion(uniqueQuestion)
        } else {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun updateCurrentQuestion(question: Question) {
        val baseId = question.id.substringBefore("_")
        seenIds.add(baseId)
        if (seenIds.size > MAX_HISTORY_SIZE) {
            seenIds.removeAt(0)
        }

        _uiState.value = _uiState.value.copy(
            currentQuestion = question,
            isLoading = false
        )
    }
}
