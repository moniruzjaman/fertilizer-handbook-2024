package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiAgronomyAdvice
import com.example.model.FertilizerCalculationResult
import com.example.ui.components.DisclaimerBanner
import com.example.ui.theme.AgrigreenDark
import com.example.ui.theme.AgrigreenPrimary
import com.example.ui.theme.HarvestGold

@Composable
fun AiAgronomistScreen(
    result: FertilizerCalculationResult,
    advice: AiAgronomyAdvice?,
    isLoading: Boolean,
    onRequestAdvice: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_agronomist_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DisclaimerBanner()
        }
        // Hero Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_agri_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    AgrigreenDark,
                                    Color(0xFF0F321B)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "GEMINI 3.5 FLASH AGRONOMIST",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f),
                                letterSpacing = 1.1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "AI Nutrient & Agronomy Advisor",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Tailored fertilizer timing, nutrient use efficiency (FUE), and climate-smart Bangladesh farming practices.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Loading or Empty State
        if (isLoading) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_loading_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = AgrigreenPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(46.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Synthesizing FRG-2024 recommendations with Gemini...",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Modeling soil nutrient dynamics for ${result.crop.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else if (advice == null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_empty_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ready to Consult Gemini Agronomist",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Analyze ${result.crop.name} for your ${result.landArea} ${result.landUnit.displayName} plot. Gemini will synthesize fertilizer split strategies, nutrient efficiency, and precautions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onRequestAdvice,
                            colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary),
                            modifier = Modifier.testTag("button_request_ai")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Consult Gemini Agronomist", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Advice Content
            item {
                AdviceSectionCard(
                    title = "Nutritional Overview",
                    content = advice.cropOverview,
                    icon = Icons.Default.Info,
                    tint = AgrigreenPrimary
                )
            }

            item {
                AdviceSectionCard(
                    title = "Soil & Application Timing",
                    content = advice.soilAndTimingAnalysis,
                    icon = Icons.Default.Timeline,
                    tint = Color(0xFF1976D2)
                )
            }

            item {
                AdviceSectionCard(
                    title = "Maximizing Nutrient Efficiency (FUE)",
                    content = advice.splitApplicationGuidance,
                    icon = Icons.Default.Lightbulb,
                    tint = HarvestGold
                )
            }

            item {
                AdviceSectionCard(
                    title = "Warnings & Antagonisms",
                    content = advice.warningAndPrecautions,
                    icon = Icons.Default.ReportProblem,
                    tint = Color(0xFFD32F2F)
                )
            }

            item {
                AdviceSectionCard(
                    title = "Climate-Smart Practice",
                    content = advice.climateSmartTip,
                    icon = Icons.Default.Eco,
                    tint = Color(0xFF2E7D32)
                )
            }

            item {
                OutlinedButton(
                    onClick = onRequestAdvice,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_refresh_ai")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Re-consult Gemini Agronomist")
                }
            }
        }
    }
}

@Composable
private fun AdviceSectionCard(
    title: String,
    content: String,
    icon: ImageVector,
    tint: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )
        }
    }
}
