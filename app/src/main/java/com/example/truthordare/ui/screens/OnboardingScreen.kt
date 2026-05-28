package com.example.truthordare.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truthordare.data.model.QuestionRating
import com.example.truthordare.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onComplete: (language: String, rating: String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var selectedLanguage by remember { mutableStateOf("en") }
    var selectedRating by remember { mutableStateOf(QuestionRating.NORMAL) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppleDarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = true // Allow swiping in onboarding too
            ) { page ->
                when (page) {
                    0 -> LanguageSelectionPage(selectedLanguage) { selectedLanguage = it }
                    1 -> ModeSelectionPage(selectedRating) { selectedRating = it }
                    2 -> TutorialPage()
                }
            }

            // Bottom Navigation Area
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator (Apple Style dots)
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width by animateDpAsState(targetValue = if (isSelected) 24.dp else 8.dp)
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(if (isSelected) AppleBlue else AppleSystemGrey2)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onComplete(selectedLanguage, selectedRating)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppleBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage < 2) "Continue" else "Get Started",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W600
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionPage(selected: String, onSelect: (String) -> Unit) {
    val languages = listOf(
        "en" to "🇺🇸 English",
        "vi" to "🇻🇳 Tiếng Việt",
        "fr" to "🇫🇷 Français",
        "es" to "🇪🇸 Español",
        "de" to "🇩🇪 Deutsch"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Welcome",
            style = MaterialTheme.typography.titleLarge,
            color = AppleSystemGrey,
            fontWeight = FontWeight.Medium
        )
        Text(
            "Select Language",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            languages.forEach { (code, name) ->
                AppleSelectionCard(
                    text = name,
                    isSelected = selected == code,
                    onClick = { onSelect(code) }
                )
            }
        }
    }
}

@Composable
fun ModeSelectionPage(selected: String, onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Experience",
            style = MaterialTheme.typography.titleLarge,
            color = AppleSystemGrey,
            fontWeight = FontWeight.Medium
        )
        Text(
            "Choose Mode",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        AppleSelectionCard(
            text = "Normal 🌟",
            subText = "Clean fun for everyone",
            isSelected = selected == QuestionRating.NORMAL,
            onClick = { onSelect(QuestionRating.NORMAL) }
        )
        Spacer(Modifier.height(12.dp))
        AppleSelectionCard(
            text = "Adult 18+ 🔥",
            subText = "Bold & Spicy party vibes",
            isSelected = selected == QuestionRating.ADULT,
            onClick = { onSelect(QuestionRating.ADULT) }
        )
    }
}

@Composable
fun TutorialPage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "How to play",
            style = MaterialTheme.typography.titleLarge,
            color = AppleSystemGrey,
            fontWeight = FontWeight.Medium
        )
        Text(
            "Quick Tips",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        TutorialItem("Tap the card", "Flip to reveal your challenge", ApplePink)
        Spacer(Modifier.height(24.dp))
        TutorialItem("Swipe left or right", "Skip to the next question", AppleCyan)
        Spacer(Modifier.height(24.dp))
        TutorialItem("Have fun!", "The best way to enjoy with friends", AppleYellow)
    }
}

@Composable
fun TutorialItem(title: String, description: String, iconColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(iconColor)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(description, color = AppleSystemGrey, fontSize = 15.sp)
        }
    }
}

@Composable
fun AppleSelectionCard(
    text: String,
    subText: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AppleBlue.copy(alpha = 0.15f) else AppleCardBackground
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AppleBlue else Color.Transparent
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (subText != null) {
                    Text(
                        text = subText,
                        color = AppleSystemGrey,
                        fontSize = 14.sp
                    )
                }
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(AppleBlue),
                    contentAlignment = Alignment.Center
                ) {
                    // Checkmark simulation
                    Box(modifier = Modifier.size(10.dp).background(Color.White, CircleShape))
                }
            }
        }
    }
}
