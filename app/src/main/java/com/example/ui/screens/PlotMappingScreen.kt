package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AezDatabase
import com.example.data.repository.PlotRepository
import com.example.location.PlotGeofenceManager
import com.example.model.GeofenceState
import com.example.model.GeofenceStatus
import com.example.model.GeoPoint
import com.example.model.MappedPlot
import com.example.model.ReverseGeoAddress
import com.example.ui.components.PlotCanvasView
import com.example.ui.theme.AgrigreenDark
import com.example.ui.theme.AgrigreenPrimary
import com.example.util.AppLanguage
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotMappingScreen(
    currentLatitude: Double?,
    currentLongitude: Double?,
    language: AppLanguage,
    onApplyPlotToCalculator: (MappedPlot) -> Unit,
    onDetectLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val plotRepo = remember { PlotRepository(context) }

    // Plot Editing State
    var plotName by remember { mutableStateOf(if (language == AppLanguage.BANGLA) "আমার কৃষি প্লট ১" else "My Farm Plot 1") }
    var isPolygonMode by remember { mutableStateOf(true) }
    var boundaryPoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var circularRadiusMeters by remember { mutableFloatStateOf(30f) }

    // Reverse Geo Address & AEZ
    var reverseAddress by remember { mutableStateOf(ReverseGeoAddress()) }
    var isAddressLoading by remember { mutableStateOf(false) }

    // Saved Plots Modal
    var savedPlots by remember { mutableStateOf<List<MappedPlot>>(emptyList()) }
    var showSavedPlotsDialog by remember { mutableStateOf(false) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Load saved plots on initial composition
    LaunchedEffect(Unit) {
        savedPlots = plotRepo.getSavedPlots()
    }

    // Default sample field initialization if points are empty and coordinates are present
    LaunchedEffect(currentLatitude, currentLongitude) {
        if (boundaryPoints.isEmpty() && currentLatitude != null && currentLongitude != null) {
            // Initialize with a practical rectangular 33-decimal (1 bigha) boundary around user coordinates
            val offsetLat = 0.00032 // approx 35 meters
            val offsetLon = 0.00038 // approx 38 meters
            boundaryPoints = listOf(
                GeoPoint(currentLatitude - offsetLat, currentLongitude - offsetLon),
                GeoPoint(currentLatitude - offsetLat, currentLongitude + offsetLon),
                GeoPoint(currentLatitude + offsetLat, currentLongitude + offsetLon),
                GeoPoint(currentLatitude + offsetLat, currentLongitude - offsetLon)
            )

            // Trigger reverse geocoding
            isAddressLoading = true
            reverseAddress = PlotGeofenceManager.reverseGeocode(context, currentLatitude, currentLongitude)
            isAddressLoading = false
        }
    }

    // Centroid calculation
    val centroid = remember(boundaryPoints, isPolygonMode, currentLatitude, currentLongitude) {
        if (isPolygonMode && boundaryPoints.isNotEmpty()) {
            PlotGeofenceManager.calculateCentroid(boundaryPoints)
        } else {
            GeoPoint(currentLatitude ?: 25.85, currentLongitude ?: 88.55)
        }
    }

    // Area calculations
    val calculatedAreaM2 = remember(boundaryPoints, isPolygonMode, circularRadiusMeters) {
        if (isPolygonMode) {
            PlotGeofenceManager.calculatePolygonAreaSquareMeters(boundaryPoints)
        } else {
            Math.PI * circularRadiusMeters * circularRadiusMeters
        }
    }
    val calculatedDecimals = remember(calculatedAreaM2) {
        PlotGeofenceManager.convertSquareMetersToDecimals(calculatedAreaM2)
    }
    val calculatedBigha = remember(calculatedDecimals) {
        PlotGeofenceManager.convertDecimalsToBigha(calculatedDecimals)
    }
    val calculatedAcres = remember(calculatedDecimals) {
        PlotGeofenceManager.convertDecimalsToAcres(calculatedDecimals)
    }
    val calculatedHectares = remember(calculatedAreaM2) {
        PlotGeofenceManager.convertSquareMetersToHectares(calculatedAreaM2)
    }
    val perimeterMeters = remember(boundaryPoints, isPolygonMode, circularRadiusMeters) {
        if (isPolygonMode) {
            PlotGeofenceManager.calculatePerimeterMeters(boundaryPoints)
        } else {
            2 * Math.PI * circularRadiusMeters
        }
    }

    // Match AEZ based on centroid and reverse geocoded district
    val matchedAez = remember(centroid, reverseAddress) {
        if (reverseAddress.district.isNotBlank()) {
            AezDatabase.findByDistrict(reverseAddress.district)
                ?: AezDatabase.findNearestAez(centroid.latitude, centroid.longitude)
        } else {
            AezDatabase.findNearestAez(centroid.latitude, centroid.longitude)
        }
    }

    // Construct current active plot object
    val activePlot = remember(
        plotName, boundaryPoints, isPolygonMode, circularRadiusMeters,
        centroid, calculatedAreaM2, calculatedDecimals, calculatedBigha,
        calculatedAcres, calculatedHectares, perimeterMeters, reverseAddress, matchedAez
    ) {
        MappedPlot(
            id = UUID.randomUUID().toString(),
            name = plotName,
            boundaryPoints = boundaryPoints,
            centerLatitude = centroid.latitude,
            centerLongitude = centroid.longitude,
            radiusMeters = circularRadiusMeters.toDouble(),
            isPolygon = isPolygonMode,
            areaSquareMeters = calculatedAreaM2,
            areaDecimals = calculatedDecimals,
            areaBigha = calculatedBigha,
            areaAcres = calculatedAcres,
            areaHectares = calculatedHectares,
            perimeterMeters = perimeterMeters,
            village = reverseAddress.village,
            union = reverseAddress.union,
            upazila = reverseAddress.upazila,
            district = reverseAddress.district,
            division = reverseAddress.division,
            postCode = reverseAddress.postCode,
            fullAddressBn = reverseAddress.fullAddressBn,
            aezNumber = matchedAez.aezNumber,
            aezName = matchedAez.name,
            aezBengaliName = matchedAez.bengaliName,
            dominantLandTypeBn = matchedAez.dominantLandTypeBn,
            soilTextureBn = matchedAez.soilTextureBn
        )
    }

    // Real-time Geofence status calculation
    val geofenceStatus = remember(currentLatitude, currentLongitude, activePlot) {
        if (currentLatitude != null && currentLongitude != null) {
            PlotGeofenceManager.calculateGeofenceStatus(currentLatitude, currentLongitude, activePlot)
        } else {
            null
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Screen Header Card with Gradient
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgrigreenDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(AgrigreenDark, AgrigreenPrimary)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CropFree,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "জমির প্লট ম্যাপিং ও জিওফেন্সিং" else "Plot Mapping & Geofencing",
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BANGLA)
                                            "সীমানা নির্ধারণ, শতক পরিমাপ ও সুষম সার সুপারিশ"
                                        else
                                            "Boundary mapping, decimal area & FRG-2024 guidance",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Saved Plots Action Button
                            OutlinedButton(
                                onClick = { showSavedPlotsDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                    brush = Brush.linearGradient(listOf(Color.White, Color.White))
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("button_open_saved_plots")
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "সংরক্ষিত (${savedPlots.size})" else "Saved (${savedPlots.size})",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1. Live Geofence Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (geofenceStatus?.state) {
                        GeofenceState.INSIDE -> Color(0xFFE8F5E9)
                        GeofenceState.NEARBY -> Color(0xFFFFF3E0)
                        else -> Color(0xFFECEFF1)
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    when (geofenceStatus?.state) {
                        GeofenceState.INSIDE -> Color(0xFF4CAF50)
                        GeofenceState.NEARBY -> Color(0xFFFF9800)
                        else -> Color(0xFF90A4AE)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when (geofenceStatus?.state) {
                                    GeofenceState.INSIDE -> Color(0xFF2E7D32)
                                    GeofenceState.NEARBY -> Color(0xFFE65100)
                                    else -> Color(0xFF455A64)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (geofenceStatus?.state) {
                                GeofenceState.INSIDE -> Icons.Default.CheckCircle
                                GeofenceState.NEARBY -> Icons.Default.Explore
                                else -> Icons.Default.LocationOn
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "রিয়েল-টাইম জিওফেন্স অবস্থা:" else "Real-Time Geofence Status:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (geofenceStatus != null) {
                                if (language == AppLanguage.BANGLA) geofenceStatus.messageBn else geofenceStatus.messageEn
                            } else {
                                if (language == AppLanguage.BANGLA) "GPS লোকেশন সংগ্রহ করা হচ্ছে..." else "Detecting GPS location..."
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Refresh Location button
                    IconButton(
                        onClick = { onDetectLocation() },
                        modifier = Modifier.testTag("button_refresh_geofence_gps")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Refresh GPS",
                            tint = AgrigreenPrimary
                        )
                    }
                }
            }
        }

        // 2. Interactive Plot Canvas View
        item {
            PlotCanvasView(
                plot = activePlot,
                userLatitude = currentLatitude,
                userLongitude = currentLongitude,
                geofenceStatus = geofenceStatus,
                language = language
            )
        }

        // 3. Plot Details & Mapping Controls Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "প্লটের নাম ও সীমানা নির্ধারণ" else "Plot Name & Boundary Setup",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Plot Name Text Field
                    OutlinedTextField(
                        value = plotName,
                        onValueChange = { plotName = it },
                        label = { Text(if (language == AppLanguage.BANGLA) "প্লটের নাম" else "Plot Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_plot_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick name preset chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val presets = if (language == AppLanguage.BANGLA)
                            listOf("উত্তর মাঠের ধানী জমি", "দক্ষিণ ক্ষেতের বোরো", "পুকুর পাড়ের সবজি", "পশ্চিমের সরিষা ক্ষেত")
                        else
                            listOf("North Paddy Field", "South Boro Field", "Pond Garden", "West Mustard Field")

                        items(presets) { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                    .clickable { plotName = preset }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = preset, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // Mode Selection: Polygon vs Circle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "ম্যাপিং পদ্ধতি:" else "Mapping Method:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = isPolygonMode,
                                onClick = { isPolygonMode = true },
                                label = { Text(if (language == AppLanguage.BANGLA) "বহুভুজ সীমানা" else "Polygon Corners", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.CropFree, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AgrigreenPrimary.copy(alpha = 0.2f))
                            )

                            FilterChip(
                                selected = !isPolygonMode,
                                onClick = { isPolygonMode = false },
                                label = { Text(if (language == AppLanguage.BANGLA) "বৃত্তাকার ব্যাসার্ধ" else "Circular Radius", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AgrigreenPrimary.copy(alpha = 0.2f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isPolygonMode) {
                        // Polygon Boundary Pin Controls
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "জমির চারপাশে হেঁটে হেঁটে প্রতিটি কোণায় বর্তমান GPS পয়েন্ট যুক্ত করুন (${boundaryPoints.size}টি কোণা সংগৃহীত):"
                            else
                                "Walk around your plot perimeter and add corners via GPS (${boundaryPoints.size} corners mapped):",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (currentLatitude != null && currentLongitude != null) {
                                        boundaryPoints = boundaryPoints + GeoPoint(currentLatitude, currentLongitude)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("button_add_current_gps_point"),
                                colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary)
                            ) {
                                Icon(Icons.Default.AddLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "কোণা পিন করুন" else "Pin Corner",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    if (boundaryPoints.isNotEmpty()) {
                                        boundaryPoints = boundaryPoints.dropLast(1)
                                    }
                                },
                                enabled = boundaryPoints.isNotEmpty(),
                                modifier = Modifier.testTag("button_undo_point")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                            }

                            OutlinedButton(
                                onClick = { boundaryPoints = emptyList() },
                                enabled = boundaryPoints.isNotEmpty(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.testTag("button_clear_points")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    } else {
                        // Circular Radius Mode
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "প্লটের ব্যাসার্ধ (মিটার):" else "Plot Radius (meters):",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${circularRadiusMeters.toInt()} মি (${String.format(Locale.US, "%.1f", calculatedDecimals)} শতক)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgrigreenDark
                                )
                            }

                            Slider(
                                value = circularRadiusMeters,
                                onValueChange = { circularRadiusMeters = it },
                                valueRange = 10f..100f,
                                steps = 9,
                                colors = SliderDefaults.colors(thumbColor = AgrigreenPrimary, activeTrackColor = AgrigreenPrimary),
                                modifier = Modifier.testTag("slider_circular_radius")
                            )

                            // Quick radius presets
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val radiusPresets = listOf(15f, 25f, 35f, 50f, 75f)
                                items(radiusPresets) { r ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (circularRadiusMeters == r) AgrigreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable { circularRadiusMeters = r }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "${r.toInt()} মি",
                                            fontSize = 11.sp,
                                            color = if (circularRadiusMeters == r) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Reverse Geocoded Address Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = AgrigreenPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "বিপরীত জিও ঠিকানা (Reverse Geo Address)" else "Reverse Geo Address",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    isAddressLoading = true
                                    reverseAddress = PlotGeofenceManager.reverseGeocode(context, centroid.latitude, centroid.longitude)
                                    isAddressLoading = false
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            if (isAddressLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh Address", modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Address Hierarchy
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "গ্রাম / এলাকা:" else "Village / Area:",
                            value = reverseAddress.village.ifEmpty { "মাঠ সংলগ্ন এলাকা" }
                        )
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "ইউনিয়ন / মৌজা:" else "Union / Mouza:",
                            value = reverseAddress.union.ifEmpty { "স্থানীয় ইউনিয়ন পরিষদ" }
                        )
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "উপজেলা / থানা:" else "Upazila / Thana:",
                            value = reverseAddress.upazila.ifEmpty { "কাহারোল" },
                            isHighlighted = true
                        )
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "জেলা / জিলা:" else "District / Zilla:",
                            value = reverseAddress.district.ifEmpty { "দিনাজপুর" },
                            isHighlighted = true
                        )
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "বিভাগ:" else "Division:",
                            value = reverseAddress.division.ifEmpty { "রংপুর বিভাগ" }
                        )
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "GPS স্থানাঙ্ক:" else "GPS Coordinates:",
                            value = "${String.format(Locale.US, "%.5f", centroid.latitude)}° N, ${String.format(Locale.US, "%.5f", centroid.longitude)}° E"
                        )
                    }
                }
            }
        }

        // 5. Calculated Land Area & Dimensions Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Landscape, contentDescription = null, tint = AgrigreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "প্লটের মোট পরিমাপ ও আয়তন" else "Plot Area & Dimensions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Big Decimal Readout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AgrigreenPrimary.copy(alpha = 0.12f))
                            .border(1.dp, AgrigreenPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "মোট জমির আয়তন" else "Total Land Area",
                                fontSize = 12.sp,
                                color = AgrigreenDark
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.2f", calculatedDecimals)} ${if (language == AppLanguage.BANGLA) "শতক / শতাংশ" else "Decimals"}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AgrigreenDark
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.2f", calculatedBigha)} বিঘা (${String.format(Locale.US, "%.3f", calculatedAcres)} একর)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary units grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UnitMetricBox(
                            label = if (language == AppLanguage.BANGLA) "বিঘা (৩৩ শতক)" else "Bigha (33 Dec)",
                            value = String.format(Locale.US, "%.2f", calculatedBigha),
                            modifier = Modifier.weight(1f)
                        )
                        UnitMetricBox(
                            label = if (language == AppLanguage.BANGLA) "হেক্টর (১০,০০০ মি²)" else "Hectare",
                            value = String.format(Locale.US, "%.3f", calculatedHectares),
                            modifier = Modifier.weight(1f)
                        )
                        UnitMetricBox(
                            label = if (language == AppLanguage.BANGLA) "পরিসীমা (মিটার)" else "Perimeter (m)",
                            value = "${perimeterMeters.toInt()} মি",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 6. BARC FRG-2024 Soil & AEZ Information Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terrain, contentDescription = null, tint = AgrigreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "FRG 2024 মাটির ধরন ও কৃষি অঞ্চল (AEZ)" else "FRG 2024 Soil Profile & AEZ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "কৃষি পরিবেশ অঞ্চল:" else "Agro-Ecological Zone:",
                            value = "AEZ ${matchedAez.aezNumber} - ${if (language == AppLanguage.BANGLA) matchedAez.bengaliName else matchedAez.name}",
                            isHighlighted = true
                        )
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "প্রধান ভূমির শ্রেণি:" else "Dominant Land Type:",
                            value = if (language == AppLanguage.BANGLA) matchedAez.dominantLandTypeBn else matchedAez.dominantLandType
                        )
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "মাটির বুনট (Texture):" else "Soil Texture:",
                            value = if (language == AppLanguage.BANGLA) matchedAez.soilTextureBn else matchedAez.soilTexture
                        )
                        AddressRow(
                            label = if (language == AppLanguage.BANGLA) "মাটির পুষ্টি অবস্থা:" else "Nutrient Baseline:",
                            value = if (language == AppLanguage.BANGLA) matchedAez.nutrientStatusSummaryBn else matchedAez.nutrientStatusSummary
                        )
                    }
                }
            }
        }

        // 7. Action CTA: Calculate Fertilizer for this Plot (FRG 2024)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgrigreenDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(AgrigreenDark, AgrigreenPrimary)))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "🌾 এই প্লটের জন্য সার নির্ধারণ করুন (FRG 2024)"
                            else
                                "🌾 Calculate Fertilizer for this Plot (FRG 2024)",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "প্লটের আয়তন (${String.format(Locale.US, "%.1f", calculatedDecimals)} শতক), উপজেলা (${reverseAddress.upazila.ifEmpty { "কাহারোল" }}) ও AEZ ${matchedAez.aezNumber} স্বয়ংক্রিয়ভাবে ক্যালকুলেটরে যুক্ত হবে।"
                            else
                                "Applies plot size (${String.format(Locale.US, "%.1f", calculatedDecimals)} Dec), upazila & AEZ ${matchedAez.aezNumber} directly to calculator.",
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onApplyPlotToCalculator(activePlot) },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp)
                                    .testTag("button_apply_plot_to_calculator"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = AgrigreenDark
                                )
                            ) {
                                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "সারের হিসাব দেখুন" else "Calculate Fertilizer",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        plotRepo.savePlot(activePlot)
                                        savedPlots = plotRepo.getSavedPlots()
                                        saveSuccessMessage = if (language == AppLanguage.BANGLA)
                                            "প্লট '${activePlot.name}' সফলভাবে সংরক্ষিত হয়েছে!"
                                        else
                                            "Plot '${activePlot.name}' saved successfully!"
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("button_save_plot"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.22f),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "সংরক্ষণ" else "Save Plot",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        saveSuccessMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = msg,
                                color = Color(0xFFA5D6A7),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Saved Plots Dialog
    if (showSavedPlotsDialog) {
        AlertDialog(
            onDismissRequest = { showSavedPlotsDialog = false },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "সংরক্ষিত প্লটসমূহ" else "Saved Farm Plots",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (savedPlots.isEmpty()) {
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "কোনো সংরক্ষিত প্লট পাওয়া যায়নি। আপনার প্লট ম্যাপ করে 'সংরক্ষণ' বাটনে চাপুন।"
                        else
                            "No saved plots found. Map your plot and tap 'Save Plot' to keep it here.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(savedPlots) { p ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Load plot into view
                                        plotName = p.name
                                        isPolygonMode = p.isPolygon
                                        boundaryPoints = p.boundaryPoints
                                        circularRadiusMeters = p.radiusMeters.toFloat().coerceAtLeast(10f)
                                        reverseAddress = ReverseGeoAddress(
                                            village = p.village,
                                            union = p.union,
                                            upazila = p.upazila,
                                            district = p.district,
                                            division = p.division,
                                            fullAddressBn = p.fullAddressBn
                                        )
                                        showSavedPlotsDialog = false
                                    },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = AgrigreenPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = p.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            text = "${String.format(Locale.US, "%.1f", p.areaDecimals)} শতক | ${p.upazila}, ${p.district} (AEZ ${p.aezNumber})",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                plotRepo.deletePlot(p.id)
                                                savedPlots = plotRepo.getSavedPlots()
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSavedPlotsDialog = false }) {
                    Text(if (language == AppLanguage.BANGLA) "বন্ধ করুন" else "Close")
                }
            }
        )
    }
}

@Composable
private fun AddressRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlighted) AgrigreenDark else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun UnitMetricBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AgrigreenDark)
            Text(text = label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
