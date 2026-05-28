package com.example.truthordare.ui.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truthordare.ui.theme.NeonDarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NeonDarkBackground)
            )
        },
        containerColor = NeonDarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            LegalTextSection("1. Information We Collect", "We do not collect any personal identification information. We may collect anonymous usage statistics to improve the app experience.")
            LegalTextSection("2. How We Use Information", "Usage data is used solely for debugging and optimizing the Truth or Dare API calls.")
            LegalTextSection("3. Third Party Services", "Our app uses AdMob for advertisements and Google Play Billing for in-app purchases. These services may collect data as governed by their own privacy policies.")
            LegalTextSection("4. Children's Privacy", "The app contains an 18+ mode. We do not knowingly collect data from children under 13. Parental guidance is recommended for the Normal mode.")
            LegalTextSection("5. Contact Us", "If you have any questions, please contact us at support@example.com")
        }
    }
}

@Composable
fun LegalTextSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Text(content, color = Color.Gray, fontSize = 14.sp, lineHeight = 20.sp)
    }
}
