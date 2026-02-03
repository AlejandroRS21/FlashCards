package com.ramsalapps.flashcards.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.DataManager
import com.ramsalapps.flashcards.ui.theme.*
import com.ramsalapps.flashcards.ui.theme.Spacing
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import com.ramsalapps.flashcards.designsystem.components.DesignSystemCard

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dataManager = DataManager(context)

    var bionicReadingEnabled by remember {
        mutableStateOf(dataManager.isBionicReadingEnabled())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelPurple)
            .systemBarsPadding()
            .padding(Spacing.xl)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = TextDark,
                modifier = Modifier.clickable { onBack() }
            )
            Text(
                "Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bionic Reading Setting
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(BorderRadius.md)
        ) {
            Row(
                modifier = Modifier
                    .padding(Spacing.lg)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Bionic Reading", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Surface(
                            color = PastelBlue,
                            shape = RoundedCornerShape(BorderRadius.xs)
                        ) {
                            Text(
                                "BETA",
                                modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xs),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Improve focus by highlighting the start of each word.",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = bionicReadingEnabled,
                    onCheckedChange = {
                        bionicReadingEnabled = it
                        dataManager.setBionicReadingEnabled(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentBlue
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5E6)),
            shape = RoundedCornerShape(BorderRadius.md),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE4B5))
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg)
            ) {
                Text(
                    "About Bionic Reading",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    "Bionic Reading is a technique that highlights the first letter(s) of each word. This focuses your eyes on the beginning of each word, helping you read faster and with better comprehension.",
                    fontSize = 12.sp,
                    color = TextDark,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

