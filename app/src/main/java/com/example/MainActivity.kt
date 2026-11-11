package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.model.DisclaimerConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.AuthManager
import com.example.ui.components.UserAccountDialog
import com.example.ui.screens.AiAgronomistScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.DeficiencyGuideScreen
import com.example.ui.screens.PlotMappingScreen
import com.example.ui.screens.ResourcesScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.AgrigreenDark
import com.example.ui.theme.AgrigreenPrimary
import com.example.ui.theme.FrgTheme
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.viewmodel.FrgViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FrgViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FrgTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                var showAccountDialog by remember { mutableStateOf(false) }

                val prefs = remember { context.getSharedPreferences("frg_app_prefs", Context.MODE_PRIVATE) }
                var showDaeDisclaimerDialog by remember {
                    mutableStateOf(!prefs.getBoolean("dae_disclaimer_accepted", false))
                }

                // Initialize Firestore repository with Application Context
                LaunchedEffect(Unit) {
                    viewModel.initRepository(context)

                    // Auto-fetch device Google email and sign in silently
                    val credentialManager = CredentialManager.create(context)
                    AuthManager.attemptAutoSignIn(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = { user ->
                            viewModel.onUserSignedIn(user)
                        },
                        onUnauthenticated = {
                            // Guest mode ready
                        },
                        scope = scope
                    )
                }

                // Show toast for Firestore save events
                LaunchedEffect(state.saveToastMessage) {
                    state.saveToastMessage?.let { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        viewModel.clearSaveToast()
                    }
                }

                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    if (granted) {
                        viewModel.fetchCurrentLocation(context)
                    }
                }

                fun triggerLocationFetch() {
                    val hasFine = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasCoarse = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasFine || hasCoarse) {
                        viewModel.fetchCurrentLocation(context)
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }

                // Automatic location detection on app launch (Auto AEZ & Weather)
                LaunchedEffect(Unit) {
                    val hasFine = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasCoarse = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasFine || hasCoarse) {
                        viewModel.fetchCurrentLocation(context)
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }

                // Hardware Back button handling
                BackHandler(enabled = state.currentTab != 0) {
                    viewModel.setTab(0)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = AppStrings.appTitle(state.language),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            navigationIcon = {
                                Icon(
                                    imageVector = Icons.Default.Grass,
                                    contentDescription = "FRG Logo",
                                    tint = AgrigreenPrimary,
                                    modifier = Modifier
                                        .padding(start = 12.dp, end = 4.dp)
                                        .size(28.dp)
                                )
                            },
                            actions = {
                                // Google User Account Button
                                IconButton(
                                    onClick = { showAccountDialog = true },
                                    modifier = Modifier.testTag("action_user_account")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "User Account",
                                        tint = if (state.currentUser != null) AgrigreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Plot Mapping & Geofencing Shortcut
                                IconButton(
                                    onClick = { viewModel.setTab(5) },
                                    modifier = Modifier.testTag("action_plot_mapping")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CropFree,
                                        contentDescription = "Plot Mapping & Geofencing",
                                        tint = if (state.activePlot != null || state.currentTab == 5) AgrigreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Bangla / English Language Toggle Switch
                                LanguageToggleChip(
                                    currentLanguage = state.language,
                                    onToggle = { viewModel.toggleLanguage() }
                                )

                                Spacer(modifier = Modifier.width(2.dp))

                                IconButton(
                                    onClick = { viewModel.resetToDefaults() },
                                    modifier = Modifier.testTag("action_reset_defaults")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset Inputs",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        FrgNavigationBar(
                            currentTab = state.currentTab,
                            language = state.language,
                            onSelectTab = { viewModel.setTab(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (state.currentTab) {
                            0 -> CalculatorScreen(
                                result = state.result,
                                selectedCategory = state.selectedCategory,
                                selectedCrop = state.selectedCrop,
                                landArea = state.landArea,
                                landUnit = state.landUnit,
                                fertilityClass = state.soilFertilityClass,
                                cowdungTonnes = state.cowdungTonnesPerHa,
                                useDap = state.useDapInsteadOfTsp,
                                detectedAez = state.detectedAez,
                                locationDescription = state.locationDescription,
                                latitude = state.latitude,
                                longitude = state.longitude,
                                isLocating = state.isLocating,
                                agriWeather = state.agriWeather,
                                isWeatherLoading = state.isWeatherLoading,
                                isSavingCalculation = state.isSavingCalculation,
                                language = state.language,
                                onDetectLocation = ::triggerLocationFetch,
                                onSelectAez = viewModel::selectAez,
                                onRefreshWeather = viewModel::refreshWeather,
                                onSaveCalculation = viewModel::saveCurrentCalculation,
                                onSelectCategory = viewModel::selectCategory,
                                onSelectCrop = viewModel::selectCrop,
                                onUpdateLandArea = viewModel::updateLandArea,
                                onUpdateLandUnit = viewModel::updateLandUnit,
                                onSelectFertility = viewModel::updateSoilFertility,
                                onUpdateCowdung = viewModel::updateCowdung,
                                onToggleDap = viewModel::toggleDapMode,
                                onNavigateToSchedule = { viewModel.setTab(1) },
                                onNavigateToAi = {
                                    viewModel.setTab(3)
                                    if (state.aiAdvice == null && !state.isAiLoading) {
                                        viewModel.requestAiAdvice()
                                    }
                                },
                                activePlot = state.activePlot,
                                onNavigateToPlotMapping = { viewModel.setTab(5) },
                                onClearActivePlot = viewModel::clearActivePlot
                            )
                            1 -> ScheduleScreen(
                                result = state.result
                            )
                            2 -> DeficiencyGuideScreen()
                            3 -> AiAgronomistScreen(
                                result = state.result,
                                advice = state.aiAdvice,
                                isLoading = state.isAiLoading,
                                onRequestAdvice = viewModel::requestAiAdvice
                            )
                            4 -> ResourcesScreen(
                                result = state.result,
                                language = state.language
                            )
                            5 -> PlotMappingScreen(
                                currentLatitude = state.latitude,
                                currentLongitude = state.longitude,
                                language = state.language,
                                onApplyPlotToCalculator = viewModel::applyMappedPlot,
                                onDetectLocation = ::triggerLocationFetch
                            )
                        }
                    }

                    // User Profile Dialog
                    if (showAccountDialog) {
                        UserAccountDialog(
                            user = state.currentUser,
                            savedCalculations = state.savedCalculations,
                            cachedCalculations = state.recentCachedCalculations,
                            language = state.language,
                            onDismiss = { showAccountDialog = false },
                            onAuthSuccess = { user ->
                                viewModel.onUserSignedIn(user)
                            },
                            onSignOut = {
                                viewModel.onUserSignedOut()
                            },
                            onDeleteCalculation = { calcId ->
                                viewModel.deleteSavedCalculation(calcId)
                            },
                            onLoadCachedCalculation = { entity ->
                                viewModel.loadCachedCalculation(entity)
                                showAccountDialog = false
                            }
                        )
                    }

                    // Mandatory DAE & BARC First-Launch Statutory Disclaimer Dialog
                    if (showDaeDisclaimerDialog) {
                        AlertDialog(
                            onDismissRequest = { /* Require user to acknowledge */ },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🏛️", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (state.language == AppLanguage.BANGLA)
                                            "ডিএই (DAE) ও বার্ক বিধিবদ্ধ সতর্কবার্তা"
                                        else
                                            "DAE Advisory & Statutory Disclaimer",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFFF3E0))
                                            .border(1.dp, Color(0xFFFF9800), RoundedCornerShape(10.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = if (state.language == AppLanguage.BANGLA)
                                                "⚠️ জমিতে রাসায়নিক বা জৈব সার প্রয়োগের পূর্বে আপনার ব্লকের উপসহকারী কৃষি কর্মকর্তা (SAAO) বা উপজেলা কৃষি অফিসের (DAE - কৃষি সম্প্রসারণ অধিদপ্তর) সাথে সরাসরি পরামর্শ করা বাধ্যতামূলক।"
                                            else
                                                "⚠️ You must consult your local Sub-Assistant Agriculture Officer (SAAO) or Upazila Agriculture Office (DAE) before applying any chemical or organic fertilizers.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFBF360C),
                                            lineHeight = 17.sp
                                        )
                                    }

                                    Text(
                                        text = if (state.language == AppLanguage.BANGLA)
                                            DisclaimerConstants.FULL_STATUTORY_DISCLAIMER_BN
                                        else
                                            DisclaimerConstants.FULL_STATUTORY_DISCLAIMER_EN,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 16.sp
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        prefs.edit().putBoolean("dae_disclaimer_accepted", true).apply()
                                        showDaeDisclaimerDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AgrigreenPrimary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("button_accept_dae_disclaimer")
                                ) {
                                    Text(
                                        text = if (state.language == AppLanguage.BANGLA)
                                            "আমি বুঝেছি ও স্থানীয় DAE কর্মকর্তার পরামর্শ নেব"
                                        else
                                            "I Understand & Will Consult Local DAE Officials",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageToggleChip(
    currentLanguage: AppLanguage,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AgrigreenPrimary.copy(alpha = 0.12f))
            .border(1.dp, AgrigreenPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable { onToggle() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("button_language_toggle")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "বাং",
                fontSize = 11.sp,
                fontWeight = if (currentLanguage == AppLanguage.BANGLA) FontWeight.ExtraBold else FontWeight.Normal,
                color = if (currentLanguage == AppLanguage.BANGLA) AgrigreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = " | ",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "EN",
                fontSize = 11.sp,
                fontWeight = if (currentLanguage == AppLanguage.ENGLISH) FontWeight.ExtraBold else FontWeight.Normal,
                color = if (currentLanguage == AppLanguage.ENGLISH) AgrigreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class NavItem(val label: String, val icon: ImageVector, val tag: String)

@Composable
private fun FrgNavigationBar(
    currentTab: Int,
    language: AppLanguage,
    onSelectTab: (Int) -> Unit
) {
    val items = listOf(
        NavItem(AppStrings.navCalculator(language), Icons.Default.Calculate, "nav_calculator"),
        NavItem(AppStrings.navSchedule(language), Icons.Default.CalendarMonth, "nav_schedule"),
        NavItem(AppStrings.navDeficiencies(language), Icons.Default.Healing, "nav_deficiencies"),
        NavItem(AppStrings.navAiAdvisor(language), Icons.Default.AutoAwesome, "nav_ai_advisor"),
        NavItem(AppStrings.navResources(language), Icons.Default.Description, "nav_resources")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = currentTab == index,
                onClick = { onSelectTab(index) },
                icon = { Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(20.dp)) },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AgrigreenPrimary,
                    selectedTextColor = AgrigreenPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ),
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}
