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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FrgCropsData
import com.example.model.NutrientDeficiency
import com.example.ui.theme.AgrigreenPrimary
import com.example.ui.theme.BoronColor
import com.example.ui.theme.GypsumColor
import com.example.ui.theme.MopColor
import com.example.ui.theme.TspColor
import com.example.ui.theme.UreaColor
import com.example.ui.theme.ZincColor

fun getNutrientColor(symbol: String): Color {
    return when (symbol) {
        "N" -> UreaColor
        "P" -> TspColor
        "K" -> MopColor
        "S" -> GypsumColor
        "Zn" -> ZincColor
        "B" -> BoronColor
        else -> AgrigreenPrimary
    }
}

@Composable
fun DeficiencyGuideScreen(
    modifier: Modifier = Modifier
) {
    val list = FrgCropsData.deficiencies

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("deficiency_guide_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("deficiency_header_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Nutrient Deficiency Guide (পুষ্টি উপাদানের অভাবজনিত লক্ষণ)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Field diagnosis guide from BARC FRG-2024 to identify visual crop hunger signs and apply prompt remedial fertilizer sprays.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(list) { item ->
            DeficiencyCard(item = item)
        }
    }
}

@Composable
private fun DeficiencyCard(item: NutrientDeficiency) {
    val color = getNutrientColor(item.symbol)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("deficiency_card_${item.symbol}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "${item.nutrient} (${item.bengaliName})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.primaryFunction,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Symptoms Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF3E0))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Symptoms",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Visual Symptoms in Field:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBF360C)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.deficiencySymptoms,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF3E2723),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Corrective Action Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Healing,
                        contentDescription = "Correction",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Recommended Corrective Action:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.correctiveAction,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1B5E20),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
