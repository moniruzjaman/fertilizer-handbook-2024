package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LandUnit
import com.example.model.SoilFertilityClass
import com.example.ui.theme.AgrigreenPrimary

@Composable
fun SoilFertilityAndInmCard(
    fertilityClass: SoilFertilityClass,
    cowdungTonnes: Float,
    useDap: Boolean,
    onSelectFertility: (SoilFertilityClass) -> Unit,
    onUpdateCowdung: (Float) -> Unit,
    onToggleDap: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fertility_inm_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "Soil Fertility Status (মাটির উর্বরতা মাত্রা)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Multipliers scale recommended nutrients (VL: +25%, Low: +10%, Med: 100%, Opt: -25%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SoilFertilityClass.values().toList().chunked(3).forEach { rowClasses ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowClasses.forEach { item ->
                            FilterChip(
                                selected = fertilityClass == item,
                                onClick = { onSelectFertility(item) },
                                label = { Text(item.displayName.substringBefore(" "), fontSize = 11.sp) },
                                leadingIcon = if (fertilityClass == item) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chip_fertility_${item.name}")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Organic Manure (INM)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Organic Manure / Cowdung (গোবর সার)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "INM credit offsets chemical N, P, K",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${"%.1f".format(cowdungTonnes)} t/ha",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgrigreenPrimary
                )
            }

            Slider(
                value = cowdungTonnes,
                onValueChange = onUpdateCowdung,
                valueRange = 0f..15f,
                steps = 29,
                modifier = Modifier.testTag("slider_cowdung")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // DAP vs TSP Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (useDap) "Phosphorus Source: DAP (ডিএপি)" else "Phosphorus Source: TSP (টিএসপি)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (useDap) "DAP supplies 18% N + 20% P (Urea automatically reduced)" else "Standard TSP (20% P) and separate Urea",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = useDap,
                    onCheckedChange = onToggleDap,
                    modifier = Modifier.testTag("switch_dap_mode")
                )
            }
        }
    }
}
