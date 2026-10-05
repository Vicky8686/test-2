package com.spotai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spotai.overlay.FloatingAssistantService

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                var mode by remember { mutableStateOf("Trading") }
                var game by remember { mutableStateOf("Texas Hold'em") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("SpotAI", style = MaterialTheme.typography.headlineLarge)
                            Text("Your real-time analysis assistant")
                        }

                        item {
                            Text("Choose analysis", style = MaterialTheme.typography.titleMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Trading", "Poker").forEach { item ->
                                    FilterChip(
                                        selected = mode == item,
                                        onClick = { mode = item },
                                        label = { Text(item) }
                                    )
                                }
                            }
                        }

                        item {
                            if (mode == "Trading") {
                                Text("Trading analysis", style = MaterialTheme.typography.titleLarge)
                                Text("Analyse a chart screenshot or connect a supported live data feed.")
                                Text("Signals: Bullish / Bearish / Neutral")
                            } else {
                                Text("Poker practice", style = MaterialTheme.typography.titleLarge)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Texas Hold'em", "Teen Patti").forEach { item ->
                                        FilterChip(
                                            selected = game == item,
                                            onClick = { game = item },
                                            label = { Text(item) }
                                        )
                                    }
                                }
                                Text("Card recognition and odds analysis enabled for practice.")
                            }
                        }

                        item {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    if (Settings.canDrawOverlays(this@MainActivity)) {
                                        val intent = Intent(
                                            this@MainActivity,
                                            FloatingAssistantService::class.java
                                        ).apply {
                                            putExtra("mode", mode)
                                        }
                                        startService(intent)
                                    } else {
                                        val intent = Intent(
                                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:$packageName")
                                        )
                                        startActivity(intent)
                                    }
                                }
                            ) {
                                Text("Enable floating assistant")
                            }
                        }
                    }
                }
            }
        }
    }
}
