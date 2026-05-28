package com.example.truthordare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.truthordare.data.local.SettingsManager
import com.example.truthordare.data.repository.QuestionRepository
import com.example.truthordare.ui.components.AdBanner
import com.example.truthordare.ui.legal.PrivacyPolicyScreen
import com.example.truthordare.ui.legal.TermsOfServiceScreen
import com.example.truthordare.ui.screens.OnboardingScreen
import com.example.truthordare.ui.screens.SettingsScreen
import com.example.truthordare.ui.screens.TruthOrDareScreen
import com.example.truthordare.ui.theme.NeonDarkBackground
import com.example.truthordare.ui.theme.TruthordareTheme
import com.example.truthordare.viewmodel.MainViewModel
import com.example.truthordare.viewmodel.MainViewModelFactory
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        MobileAds.initialize(this) {}

        enableEdgeToEdge()

        val repository = QuestionRepository(this)
        val settingsManager = SettingsManager(this)
        val factory = MainViewModelFactory(repository, settingsManager)

        setContent {
            TruthordareTheme {
                val viewModel: MainViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsState()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (uiState.isOnboardingCompleted == true) {
                            Box(contentAlignment = Alignment.BottomCenter) {
                                AdBanner()
                            }
                        }
                    },
                    containerColor = NeonDarkBackground
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).background(NeonDarkBackground)) {
                        AppNavigation(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    // isOnboardingCompleted is null while loading from DataStore
    val onboardingState = uiState.isOnboardingCompleted ?: return

    val startDestination = if (onboardingState) "game" else "onboarding"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen { lang, rating ->
                viewModel.completeOnboarding(lang, rating)
                navController.navigate("game") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        }
        composable("game") {
            TruthOrDareScreen(viewModel = viewModel, onSettingsClick = {
                navController.navigate("settings")
            })
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPrivacy = { navController.navigate("privacy") },
                onNavigateToTerms = { navController.navigate("terms") }
            )
        }
        composable("privacy") {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }
        composable("terms") {
            TermsOfServiceScreen(onBack = { navController.popBackStack() })
        }
    }
}
