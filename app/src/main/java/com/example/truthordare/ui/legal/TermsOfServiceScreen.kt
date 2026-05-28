package com.example.truthordare.ui.legal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.truthordare.ui.theme.NeonDarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms of Service", color = Color.White) },
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
            LegalTextSection("1. Acceptance of Terms", "By using this app, you agree to these terms. If you do not agree, please do not use the application.")
            LegalTextSection("2. User Conduct", "Users are responsible for their own actions while performing dares or answering truths. We are not liable for any physical or psychological harm.")
            LegalTextSection("3. Age Restriction", "The 18+ Mode is intended for adult users only. By enabling this mode, you confirm you are of legal age in your jurisdiction.")
            LegalTextSection("4. Intellectual Property", "All app content, layout, and designs are the property of the developers.")
            LegalTextSection("5. Disclaimer", "The app is provided 'as is' without warranties of any kind.")
        }
    }
}
