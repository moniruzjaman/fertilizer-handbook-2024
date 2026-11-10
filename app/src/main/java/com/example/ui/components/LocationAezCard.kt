package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AezDatabase
import com.example.model.AezInfo
import com.example.ui.theme.AgrigreenDark
import com.example.ui.theme.AgrigreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun LocationAezCard(
    detectedAez: AezInfo?,
    locationDescription: String?,
    latitude: Double? = null,
    longitude: Double? = null,
    isLocating: Boolean,
    language: AppLanguage = AppLanguage.BANGLA,
    onDetectLocation: () -> Unit,
    onSelectAez: (AezInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAezPicker by remember { mutableStateOf(false) }
    var expandedSoilProfile by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("location_aez_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with GPS trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AgrigreenPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = AgrigreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppStrings.autoGpsTitle(language),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppStrings.autoGpsSubtitle(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = onDetectLocation,
                    enabled = !isLocating,
                    colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary),
                    modifier = Modifier.testTag("button_detect_gps")
                ) {
                    if (isLocating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.BANGLA) "শনাক্ত হচ্ছে..." else "Locating...", fontSize = 12.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "GPS",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.BANGLA) "জিপিএস রিফ্রেশ" else "Get GPS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (detectedAez != null) {
                // Main AEZ, Land Type & Soil Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    Column {
                        // AEZ Number & District Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AgrigreenPrimary)
                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "AEZ ${detectedAez.aezNumber}",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) detectedAez.bengaliName else detectedAez.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AgrigreenDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) detectedAez.name else detectedAez.bengaliName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HarvestGold.copy(alpha = 0.25f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = locationDescription ?: detectedAez.primaryDistricts.first(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }

                        if (latitude != null && longitude != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "GPS: ${String.format("%.4f", latitude)}°N, ${String.format("%.4f", longitude)}°E",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Land Type Row
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Landscape,
                                contentDescription = "Land Type",
                                tint = Color(0xFF6D4C41),
                                modifier = Modifier.size(18.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "প্রধান জমির শ্রেণি (Land Type):" else "Dominant Land Type:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) detectedAez.dominantLandTypeBn else detectedAez.dominantLandType,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "বণ্টন: ${detectedAez.landTypeDistributionBn}" else "Distribution: ${detectedAez.landTypeDistribution}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Soil Profile Summary Row
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Soil Profile",
                                tint = AgrigreenPrimary,
                                modifier = Modifier.size(18.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "মাটির সার্বিক প্রোফাইল (Soil Profile):" else "General Soil Profile:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA)
                                        "${detectedAez.generalSoilTypeBn} | বুনট: ${detectedAez.soilTextureBn}"
                                    else
                                        "${detectedAez.generalSoilType} | Texture: ${detectedAez.soilTexture}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick Stats Row: pH, OM%, Salinity, Drainage
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "pH", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = detectedAez.phRange, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = AgrigreenDark)
                            }
                            Column {
                                Text(text = if (language == AppLanguage.BANGLA) "জৈব পদার্থ" else "Organic Matter", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = detectedAez.organicMatterStatus, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = AgrigreenDark)
                            }
                            Column {
                                Text(text = if (language == AppLanguage.BANGLA) "লবণাক্ততা" else "Salinity", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = if (language == AppLanguage.BANGLA) detectedAez.salinityStatusBn else detectedAez.salinityStatus, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = AgrigreenDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Toggle Expandable Full Profile
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { expandedSoilProfile = !expandedSoilProfile }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA)
                                    if (expandedSoilProfile) "▲ বিস্তারিত মৃত্তিকা প্রোফাইল সংক্ষেপ করুন" else "▼ মাটির পুষ্টি ঘাটতি ও নিকাশ প্রোফাইল দেখুন"
                                else
                                    if (expandedSoilProfile) "▲ Hide Soil Profile Details" else "▼ View Complete Soil Profile & Drainage",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgrigreenPrimary
                            )
                            Icon(
                                imageVector = if (expandedSoilProfile) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = AgrigreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        AnimatedVisibility(visible = expandedSoilProfile) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                // Drainage Box
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "নিকাশ অবস্থা: ${detectedAez.drainageStatusBn}" else "Drainage: ${detectedAez.drainageStatus}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "মাটির পুষ্টি সূচক: ${detectedAez.nutrientStatusSummaryBn}" else "Nutrient Baseline: ${detectedAez.nutrientStatusSummary}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFF3E0))
                                        .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFE65100),
                                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (language == AppLanguage.BANGLA) "অণুখাদ্য ঘাটতি সতর্কতা: ${detectedAez.micronutrientAlert}" else "Micronutrient Alert: ${detectedAez.micronutrientAlert}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF4E342E),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = if (language == AppLanguage.BANGLA)
                        "স্বয়ংক্রিয়ভাবে অবস্থান শনাক্ত করতে জিপিএস বাটন চাপুন অথবা ম্যানুয়ালি জেলা নির্বাচন করুন।"
                    else
                        "Tap 'Get GPS' to fetch your local AEZ and soil traits, or choose manually.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { showAezPicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("button_choose_aez_manually")
            ) {
                Icon(
                    imageVector = Icons.Default.EditLocation,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).padding(end = 6.dp)
                )
                Text(if (language == AppLanguage.BANGLA) "ম্যানুয়ালি জেলা বা অঞ্চল নির্বাচন করুন" else "Select Region / AEZ Manually")
            }
        }
    }

    // Manual AEZ Picker Dialog
    if (showAezPicker) {
        AezSelectionDialog(
            currentAez = detectedAez,
            language = language,
            onDismiss = { showAezPicker = false },
            onSelect = { aez ->
                onSelectAez(aez)
                showAezPicker = false
            }
        )
    }
}

@Composable
private fun AezSelectionDialog(
    currentAez: AezInfo?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSelect: (AezInfo) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == AppLanguage.BANGLA) "কৃষি পরিবেশ অঞ্চল (AEZ) নির্বাচন করুন" else "Select Agro-Ecological Zone (AEZ)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AezDatabase.aezList) { aez ->
                    val isSelected = currentAez?.aezNumber == aez.aezNumber
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) AgrigreenPrimary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) AgrigreenPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelect(aez) }
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AEZ ${aez.aezNumber}: ${if (language == AppLanguage.BANGLA) aez.bengaliName else aez.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) AgrigreenPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = aez.defaultFertility.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AgrigreenDark
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "জেলাসমূহ: ${aez.primaryDistricts.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "জমির শ্রেণি: ${if (language == AppLanguage.BANGLA) aez.dominantLandTypeBn else aez.dominantLandType}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = Color(0xFF6D4C41)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
            }
        }
    )
}
