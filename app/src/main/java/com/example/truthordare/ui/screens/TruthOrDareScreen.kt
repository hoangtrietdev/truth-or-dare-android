package com.example.truthordare.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truthordare.data.model.Question
import com.example.truthordare.data.model.QuestionRating
import com.example.truthordare.data.model.QuestionType
import com.example.truthordare.ui.theme.*
import com.example.truthordare.viewmodel.MainViewModel

@Composable
fun TruthOrDareScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val currentQuestion = uiState.currentQuestion
    val language = uiState.language
    val currentType = uiState.currentType
    val selectedRating = uiState.selectedRating
    val isLoading = uiState.isLoading

    val mainBgGradient = Brush.verticalGradient(
        colors = listOf(NeonDarkBackground, Color(0xFF1A1A2E), NeonDarkBackground)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NeonDarkBackground
    ) {
        Box(modifier = Modifier.fillMaxSize().background(mainBgGradient)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Settings Button
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(24.dp))
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TRUTH OR",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGradientCyan,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "DARE",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGradientPurple,
                            letterSpacing = 2.sp,
                            modifier = Modifier.offset(y = (-8).dp)
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TypeSelector(
                    selectedType = currentType,
                    enabled = !isLoading,
                    onTypeSelected = { viewModel.setType(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                RatingToggle(
                    selectedRating = selectedRating,
                    enabled = !isLoading,
                    onToggle = { 
                        val newRating = if (it) QuestionRating.ADULT else QuestionRating.NORMAL
                        viewModel.setRating(newRating)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.8f)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(listOf(NeonGradientCyan, NeonGradientPurple)),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(AppleCardBackground.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    QuestionCardContent(
                        question = currentQuestion,
                        currentLanguage = language,
                        onLanguageChange = { viewModel.setLanguage(it) }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                val buttonBrush = if (isLoading) {
                    Brush.horizontalGradient(listOf(Color.DarkGray, Color.Gray))
                } else {
                    Brush.horizontalGradient(listOf(NeonGradientCyan, NeonGradientPurple))
                }

                Button(
                    onClick = { viewModel.nextQuestion() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(buttonBrush),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = "LOADING...",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "NEXT CHALLENGE",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Powered by Truth or Dare Bot API",
                    fontSize = 10.sp,
                    color = AppleSystemGrey.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun QuestionCardContent(
    question: Question?, 
    currentLanguage: String, 
    onLanguageChange: (String) -> Unit
) {
    if (question == null) {
        Text("Wait a second...", color = AppleSystemGrey, fontSize = 16.sp)
        return
    }

    val typeColor = if (question.type.lowercase() == "truth") NeonPink else NeonCyan
    val text = question.translations[currentLanguage] ?: question.translations["en"] ?: "..."
    
    // Dynamically adjust font size for long messages
    val fontSize = when {
        text.length > 150 -> 14.sp
        text.length > 80 -> 16.sp
        else -> 18.sp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(AppleSystemGrey2.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = question.type.uppercase(),
                color = typeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            lineHeight = if (fontSize < 16.sp) 20.sp else 26.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppleSystemGrey2.copy(alpha = 0.3f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                val rating = question.rating.lowercase()
                val ratingText = if (rating == "adult_18" || rating == "r") "R" else "E"
                Text(
                    text = "RATING: $ratingText",
                    fontSize = 9.sp,
                    color = AppleSystemGrey,
                    fontWeight = FontWeight.Bold
                )
            }
            
            LanguagePickerSmall(currentLanguage, onLanguageChange)
        }
    }
}

@Composable
fun TypeSelector(selectedType: String, enabled: Boolean, onTypeSelected: (String) -> Unit) {
    val types = listOf("TRUTH", "DARE", "BOTH")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        types.forEach { type ->
            val isSelected = selectedType.uppercase() == type
            val bgBrush = if (isSelected) {
                Brush.linearGradient(listOf(NeonGradientCyan, NeonGradientPurple))
            } else {
                null
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (bgBrush != null) Color.Transparent else AppleCardBackground)
                    .then(if (bgBrush != null) Modifier.background(bgBrush) else Modifier)
                    .border(1.dp, if (isSelected) Color.Transparent else AppleSystemGrey2, RoundedCornerShape(12.dp))
                    .clickable(enabled = enabled) { onTypeSelected(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type,
                    color = if (isSelected) Color.White else AppleSystemGrey,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun RatingToggle(selectedRating: String, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    val isAdult = selectedRating == QuestionRating.ADULT
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppleCardBackground)
            .border(1.dp, AppleSystemGrey2, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔞", fontSize = 16.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("18+ Mode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Mature content", color = AppleSystemGrey, fontSize = 11.sp)
            }
        }
        Switch(
            checked = isAdult,
            enabled = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NeonGradientPurple,
                uncheckedThumbColor = AppleSystemGrey,
                uncheckedTrackColor = AppleSystemGrey2
            )
        )
    }
}

@Composable
fun LanguagePickerSmall(currentLanguage: String, onLanguageChange: (String) -> Unit) {
    val languages = listOf("en", "vi", "fr", "es", "de")
    val flags = mapOf("en" to "🇺🇸", "vi" to "🇻🇳", "fr" to "🇫🇷", "es" to "🇪🇸", "de" to "🇩🇪")
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppleSystemGrey2.copy(alpha = 0.5f))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        languages.forEach { lang ->
            val isSelected = currentLanguage == lang
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onLanguageChange(lang) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = flags[lang] ?: "", fontSize = 14.sp)
            }
        }
    }
}
