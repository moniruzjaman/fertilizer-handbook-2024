package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.model.ScheduleDoseResult
import com.example.ui.theme.AgrigreenPrimary
import com.example.ui.theme.BoronColor
import com.example.ui.theme.GypsumColor
import com.example.ui.theme.MopColor
import com.example.ui.theme.TspColor
import com.example.ui.theme.UreaColor
import com.example.ui.theme.ZincColor

@Composable
fun ApplicationScheduleCard(
    stageIndex: Int,
    dose: ScheduleDoseResult,
    useDap: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("schedule_card_$stageIndex"),
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AgrigreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${stageIndex + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = dose.stageName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = dose.bengaliStageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Timing: ${dose.timingDescription}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quantities grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (dose.ureaKg > 0.01f) {
                    DoseRow("Urea (ইউরিয়া)", dose.ureaKg, UreaColor)
                }
                if (dose.tspKg > 0.01f) {
                    val pName = if (useDap) "DAP (ডিএপি)" else "TSP (টিএসপি)"
                    DoseRow(pName, dose.tspKg, TspColor)
                }
                if (dose.mopKg > 0.01f) {
                    DoseRow("MoP (পটাশ)", dose.mopKg, MopColor)
                }
                if (dose.gypsumKg > 0.01f) {
                    DoseRow("Gypsum (জিপসাম)", dose.gypsumKg, GypsumColor)
                }
                if (dose.zincKg > 0.01f) {
                    DoseRow("Zinc Sulphate (দস্তা)", dose.zincKg, ZincColor)
                }
                if (dose.boronKg > 0.01f) {
                    DoseRow("Boric Acid (বোরন)", dose.boronKg, BoronColor)
                }
            }
        }
    }
}

@Composable
private fun DoseRow(name: String, kg: Float, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "${"%.2f".format(kg)} kg",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
