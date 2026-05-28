package com.example.truthordare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.truthordare.data.api.TruthOrDareApi
import com.example.truthordare.data.local.SettingsManager
import com.example.truthordare.data.model.Question
import com.example.truthordare.data.model.QuestionRating
import com.example.truthordare.data.repository.QuestionRepository
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
    private val settingsManager: SettingsManager
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
            val typeToFetch = if (currentState.currentType == "BOTH") {
                if (kotlin.random.Random.nextBoolean()) "truth" else "dare"
            } else {
                currentState.currentType.lowercase()
            }

            try {
                val apiQuestion = if (typeToFetch == "truth") api.getTruth(apiRating) else api.getDare(apiRating)

                // ENSURE EN IS ALWAYS PRESENT AND FALLBACK FOR ALL LANGUAGES
                val translations = (apiQuestion.translations ?: emptyMap()).toMutableMap()
                if (!translations.containsKey("en")) {
                    translations["en"] = apiQuestion.question
                }
                
                // Fallback all languages to English if they are missing
                listOf("vi", "fr", "es", "de").forEach { lang ->
                    if (!translations.containsKey(lang)) {
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
                
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "API Error: ${e.message}")
                pickLocalQuestion()
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
            
            // Ensure local questions also have fallback for all languages
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
