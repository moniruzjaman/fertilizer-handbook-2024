package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FrgCropsData
import com.example.model.AezInfo
import com.example.model.AgriWeatherReport
import com.example.model.Crop
import com.example.model.CropCategory
import com.example.model.FertilizerCalculationResult
import com.example.model.LandUnit
import com.example.model.SoilFertilityClass
import com.example.ui.components.AgriWeatherCard
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.FertilizerItemCard
import com.example.ui.components.FertilizerResultSummaryHero
import com.example.ui.components.LocationAezCard
import com.example.ui.components.SoilFertilityAndInmCard
import com.example.ui.theme.AgrigreenPrimary
import com.example.util.AppLanguage
import com.example.util.AppStrings

import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Place
import com.example.model.MappedPlot

@Composable
fun CalculatorScreen(
    result: FertilizerCalculationResult,
    selectedCategory: CropCategory,
    selectedCrop: Crop,
    landArea: Float,
    landUnit: LandUnit,
    fertilityClass: SoilFertilityClass,
    cowdungTonnes: Float,
    useDap: Boolean,
    detectedAez: AezInfo?,
    locationDescription: String?,
    latitude: Double?,
    longitude: Double?,
    isLocating: Boolean,
    agriWeather: AgriWeatherReport?,
    isWeatherLoading: Boolean,
    isSavingCalculation: Boolean = false,
    language: AppLanguage = AppLanguage.BANGLA,
    activePlot: MappedPlot? = null,
    onDetectLocation: () -> Unit,
    onSelectAez: (AezInfo) -> Unit,
    onRefreshWeather: () -> Unit,
    onSaveCalculation: () -> Unit,
    onSelectCategory: (CropCategory) -> Unit,
    onSelectCrop: (Crop) -> Unit,
    onUpdateLandArea: (Float) -> Unit,
    onUpdateLandUnit: (LandUnit) -> Unit,
    onSelectFertility: (SoilFertilityClass) -> Unit,
    onUpdateCowdung: (Float) -> Unit,
    onToggleDap: (Boolean) -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToPlotMapping: () -> Unit = {},
    onClearActivePlot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filteredCrops = remember(selectedCategory) {
        FrgCropsData.crops.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calculator_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mandatory Statutory Disclaimer Banner
        item {
            DisclaimerBanner(language = language)
        }

        // Realtime Location & AEZ Land Type & Soil Profile Information
        item {
            LocationAezCard(
                detectedAez = detectedAez,
                locationDescription = locationDescription,
                latitude = latitude,
                longitude = longitude,
                isLocating = isLocating,
                language = language,
                onDetectLocation = onDetectLocation,
                onSelectAez = onSelectAez
            )
        }

        // Plot Mapping & Geofencing Card
        item {
            if (activePlot != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_active_mapped_plot"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF4CAF50))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E7D32)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "📍 সক্রিয় জিওফেন্সড প্লট" else "📍 Active Geofenced Plot",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            Text(
                                text = activePlot.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.1f", activePlot.areaDecimals)} শতক (${String.format(java.util.Locale.US, "%.2f", activePlot.areaBigha)} বিঘা) | ${activePlot.upazila}, ${activePlot.district} (AEZ ${activePlot.aezNumber})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            OutlinedButton(
                                onClick = onNavigateToPlotMapping,
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("button_view_active_plot"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "ম্যাপ দেখুন" else "View Map",
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "রিসেট" else "Clear",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .clickable { onClearActivePlot() }
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_start_plot_mapping"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgrigreenPrimary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AgrigreenPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = null,
                                tint = AgrigreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "🗺️ জমির প্লট ম্যাপ ও জিওফেন্সিং" else "🗺️ Plot Mapping & Geofencing",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (language == AppLanguage.BANGLA)
                                    "জমির চারপাশ চিহ্নিত করে সঠিক শতক ও FRG-2024 সার সুপারিশ পান"
                                else
                                    "Map your plot boundary to get exact decimal area & FRG-2024 fertilizer rates",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = onNavigateToPlotMapping,
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("button_open_plot_mapping"),
                            colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "ম্যাপ করুন" else "Map Plot",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Agri-Weather Indices Card
        item {
            AgriWeatherCard(
                weather = agriWeather,
                isLoading = isWeatherLoading,
                language = language,
                onRefresh = onRefreshWeather
            )
        }

        // Crop Category Selector
        item {
            Column {
                Text(
                    text = AppStrings.cropCategoryLabel(language),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CropCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onSelectCategory(cat) },
                            label = {
                                Text(
                                    if (language == AppLanguage.BANGLA) cat.bengaliName else cat.displayName,
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = if (selectedCategory == cat) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            modifier = Modifier.testTag("category_chip_${cat.name}")
                        )
                    }
                }
            }
        }

        // Crop Selection Pills
        item {
            Column {
                Text(
                    text = AppStrings.cropVarietyLabel(language),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredCrops) { crop ->
                        val isSelected = crop.id == selectedCrop.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AgrigreenPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onSelectCrop(crop) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("crop_pill_${crop.id}")
                        ) {
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) crop.bengaliName else crop.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) crop.name else crop.bengaliName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Land Area & Unit Input Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("land_area_input_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = AppStrings.landAreaLabel(language),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = AppStrings.landAreaSubtitle(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Unit Selection Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(LandUnit.values()) { unit ->
                            FilterChip(
                                selected = landUnit == unit,
                                onClick = { onUpdateLandUnit(unit) },
                                label = {
                                    Text(
                                        if (language == AppLanguage.BANGLA) unitLabelBn(unit) else unit.displayName,
                                        fontSize = 11.sp
                                    )
                                },
                                modifier = Modifier.testTag("unit_chip_${unit.name}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Counter with Stepper Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilledIconButton(
                            onClick = {
                                val step = if (landUnit == LandUnit.DECIMAL) 5f else 1f
                                onUpdateLandArea(landArea - step)
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.size(44.dp).testTag("button_decrement_land")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%.1f", landArea),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = AgrigreenPrimary
                            )
                            Text(
                                text = "${if (language == AppLanguage.BANGLA) unitLabelBn(landUnit) else landUnit.displayName} (${String.format("%.3f", result.areaInHectares)} ha)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        FilledIconButton(
                            onClick = {
                                val step = if (landUnit == LandUnit.DECIMAL) 5f else 1f
                                onUpdateLandArea(landArea + step)
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.size(44.dp).testTag("button_increment_land")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(10f, 33f, 50f, 100f).forEach { preset ->
                            OutlinedButton(
                                onClick = { onUpdateLandArea(preset) },
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = if (preset == 33f) "৩৩ (১ বিঘা)" else "${preset.toInt()}",
                                    fontSize = 11.sp,
                                    fontWeight = if (landArea == preset) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Soil Fertility Class & INM Manure Card
        item {
            SoilFertilityAndInmCard(
                fertilityClass = fertilityClass,
                cowdungTonnes = cowdungTonnes,
                useDap = useDap,
                onSelectFertility = onSelectFertility,
                onUpdateCowdung = onUpdateCowdung,
                onToggleDap = onToggleDap
            )
        }

        // Result Hero
        item {
            FertilizerResultSummaryHero(result = result)
        }

        // Save Calculation to Cloud Action
        item {
            OutlinedButton(
                onClick = onSaveCalculation,
                enabled = !isSavingCalculation,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("button_save_calculation")
            ) {
                if (isSavingCalculation) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (language == AppLanguage.BANGLA) "সংরক্ষণ হচ্ছে..." else "Saving...")
                } else {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp), tint = AgrigreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ক্লাউডে সার হিসাব সংরক্ষণ করুন (Firestore)" else "Save Calculation to Firestore Cloud",
                        fontWeight = FontWeight.Bold,
                        color = AgrigreenPrimary
                    )
                }
            }
        }

        // Quick Navigation Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToSchedule,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("button_view_schedule"),
                    colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text(AppStrings.navSchedule(language), fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onNavigateToAi,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("button_consult_ai")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text(AppStrings.navAiAdvisor(language), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Fertilizer Breakdown Items Header
        item {
            Text(
                text = if (language == AppLanguage.BANGLA) "প্রয়োজনীয় রাসায়নিক সারের পরিমাণ" else "Fertilizer Requirement Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // List of items
        items(result.items) { item ->
            FertilizerItemCard(item = item)
        }

        // Closing Statutory Disclaimer Banner
        item {
            DisclaimerBanner(language = language)
        }
    }
}

private fun unitLabelBn(unit: LandUnit): String = when (unit) {
    LandUnit.DECIMAL -> "শতক / শতাংশ"
    LandUnit.BIGHA -> "বিঘা (৩৩ শতক)"
    LandUnit.KATHA -> "কাঠা (১.৬৫ শতক)"
    LandUnit.ACRE -> "একর (১০০ শতক)"
    LandUnit.HECTARE -> "হেক্টর (২৪৭ শতক)"
}
