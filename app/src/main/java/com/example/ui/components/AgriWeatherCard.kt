package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgriWeatherReport
import com.example.model.HeatStressLevel
import com.example.model.SprayingSuitability
import com.example.ui.theme.AgrigreenDark
import com.example.ui.theme.AgrigreenPrimary
import com.example.util.AppLanguage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgriWeatherCard(
    weather: AgriWeatherReport?,
    isLoading: Boolean,
    language: AppLanguage,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedDetails by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("agri_weather_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row
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
                            .background(Color(0xFF0288D1).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = "Agri Weather",
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "লাইভ কৃষি আবহাওয়া সূচক" else "Live Agri-Weather Indices",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "সার প্রয়োগ ও স্প্রে উপযোগিতার তাৎক্ষণিক আবহাওয়া"
                            else
                                "Real-time field telemetry for fertilizer & spraying",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    enabled = !isLoading,
                    modifier = Modifier.testTag("button_refresh_weather")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color(0xFF0288D1),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Weather",
                            tint = Color(0xFF0288D1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (weather != null) {
                // Weather Telemetry Badges
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeatherMetricChip(
                        icon = Icons.Default.Thermostat,
                        label = if (language == AppLanguage.BANGLA) "তাপমাত্রা" else "Temp",
                        value = "${String.format("%.1f", weather.temperature)}°C",
                        tint = Color(0xFFD84315)
                    )
                    WeatherMetricChip(
                        icon = Icons.Default.Opacity,
                        label = if (language == AppLanguage.BANGLA) "আর্দ্রতা" else "Humidity",
                        value = "${weather.relativeHumidity}%",
                        tint = Color(0xFF0288D1)
                    )
                    WeatherMetricChip(
                        icon = Icons.Default.Air,
                        label = if (language == AppLanguage.BANGLA) "বাতাস" else "Wind",
                        value = "${String.format("%.1f", weather.windSpeedKmH)} km/h",
                        tint = Color(0xFF455A64)
                    )
                    WeatherMetricChip(
                        icon = Icons.Default.WaterDrop,
                        label = if (language == AppLanguage.BANGLA) "বৃষ্টির সম্ভাবনা" else "Rain Risk",
                        value = "${weather.rainProbabilityPercent}%",
                        tint = if (weather.rainProbabilityPercent > 35) Color(0xFFC62828) else Color(0xFF2E7D32)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Index: Spraying Suitability Banner
                val (sprayColor, sprayBg, sprayIcon) = when (weather.sprayingSuitability) {
                    SprayingSuitability.OPTIMAL -> Triple(Color(0xFF1B5E20), Color(0xFFE8F5E9), Icons.Default.CheckCircle)
                    SprayingSuitability.CAUTION -> Triple(Color(0xFFE65100), Color(0xFFFFF3E0), Icons.Default.Warning)
                    SprayingSuitability.UNFAVORABLE -> Triple(Color(0xFFB71C1C), Color(0xFFFFEBEE), Icons.Default.Warning)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(sprayBg)
                        .border(1.dp, sprayColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = sprayIcon,
                            contentDescription = null,
                            tint = sprayColor,
                            modifier = Modifier
                                .size(22.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "স্প্রে উপযোগিতা: " else "Spraying Index: ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) weather.sprayingSuitability.displayNameBn else weather.sprayingSuitability.displayNameEn,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = sprayColor
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) weather.sprayingAdviceBn else weather.sprayingAdviceEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Index: Top-Dressing & Heat Stress expandable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { expandedDetails = !expandedDetails }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            if (expandedDetails) "▲ সার প্রয়োগ ও তাপপ্রবাহ সূচক সংক্ষেপ করুন" else "▼ ইউরিয়া উপরিপ্রয়োগ ও তাপচাপ সূচক দেখুন"
                        else
                            if (expandedDetails) "▲ Hide Top-Dressing & Heat Stress Details" else "▼ View Top-Dressing & Heat Stress Advisories",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1)
                    )
                }

                AnimatedVisibility(visible = expandedDetails) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        // Top Dressing Advice Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = AgrigreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "ইউরিয়া উপরিপ্রয়োগ পরামর্শ" else "Urea Top-Dressing Guidance",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AgrigreenDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) weather.topDressingAdviceBn else weather.topDressingAdviceEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Crop Heat Stress Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "শস্য তাপচাপ সূচক (Heat Stress)" else "Crop Heat Stress Status",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) weather.heatStressAdviceBn else weather.heatStressAdviceEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA)
                                        "দৈনিক বাষ্পীভবন (ET0): ${String.format("%.1f", weather.evapotranspirationMm)} মিমি/দিন"
                                    else
                                        "Daily Reference Evapotranspiration (ET0): ${String.format("%.1f", weather.evapotranspirationMm)} mm/day",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = if (language == AppLanguage.BANGLA)
                        "আপনার জিপিএস অবস্থানের ভিত্তিতে লাইভ আবহাওয়া লোড হচ্ছে..."
                    else
                        "Fetching live agricultural weather metrics for your location...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeatherMetricChip(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
