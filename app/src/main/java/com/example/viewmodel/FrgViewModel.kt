package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthManager
import com.example.calculation.FrgCalculator
import com.example.data.AezDatabase
import com.example.data.FrgCropsData
import com.example.data.repository.FirestoreUserRepository
import com.example.location.LocationAezManager
import com.example.model.AezInfo
import com.example.model.AgriWeatherReport
import com.example.model.AiAgronomyAdvice
import com.example.model.Crop
import com.example.model.CropCategory
import com.example.model.FertilizerCalculationResult
import com.example.model.LandUnit
import com.example.model.MappedPlot
import com.example.model.SavedCalculation
import com.example.model.SoilFertilityClass
import com.example.network.AgriWeatherService
import com.example.network.GeminiAgronomistService
import com.example.util.AppLanguage
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FrgUiState(
    val language: AppLanguage = AppLanguage.BANGLA, // Bangla First by default!
    val selectedCrop: Crop = FrgCropsData.crops.first(),
    val selectedCategory: CropCategory = CropCategory.CEREALS,
    val landArea: Float = 33f, // 33 decimals = 1 bigha
    val landUnit: LandUnit = LandUnit.DECIMAL,
    val soilFertilityClass: SoilFertilityClass = SoilFertilityClass.LOW,
    val cowdungTonnesPerHa: Float = 0f,
    val useDapInsteadOfTsp: Boolean = false,
    val detectedAez: AezInfo? = AezDatabase.aezList.first(), // Default to AEZ 1 (Dinajpur region)
    val locationDescription: String? = "Dinajpur (Old Himalayan Piedmont)",
    val latitude: Double? = 25.85,
    val longitude: Double? = 88.55,
    val isLocating: Boolean = false,
    val agriWeather: AgriWeatherReport? = null,
    val isWeatherLoading: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val savedCalculations: List<SavedCalculation> = emptyList(),
    val isSavingCalculation: Boolean = false,
    val saveToastMessage: String? = null,
    val result: FertilizerCalculationResult = FrgCalculator.calculate(
        crop = FrgCropsData.crops.first(),
        landArea = 33f,
        landUnit = LandUnit.DECIMAL,
        fertilityClass = SoilFertilityClass.LOW,
        cowdungTonnesPerHa = 0f,
        useDapInsteadOfTsp = false,
        detectedAez = AezDatabase.aezList.first(),
        locationDescription = "Dinajpur (Old Himalayan Piedmont)"
    ),
    val aiAdvice: AiAgronomyAdvice? = null,
    val isAiLoading: Boolean = false,
    val activePlot: MappedPlot? = null,
    val currentTab: Int = 0
)

class FrgViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FrgUiState())
    val uiState: StateFlow<FrgUiState> = _uiState.asStateFlow()

    private var userRepository: FirestoreUserRepository? = null

    init {
        recalculate()
        _uiState.update { it.copy(currentUser = AuthManager.getCurrentUser()) }
        fetchWeatherForCoordinates(25.85, 88.55)
    }

    fun initRepository(context: Context) {
        if (userRepository == null) {
            try {
                val repo = FirestoreUserRepository(context.applicationContext)
                userRepository = repo
                loadSavedCalculations()
            } catch (e: Exception) {
                // Keep offline safe
            }
        }
    }

    fun toggleLanguage() {
        _uiState.update {
            val nextLang = if (it.language == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
            it.copy(language = nextLang)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _uiState.update { it.copy(language = lang) }
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(currentTab = index) }
    }

    fun selectCategory(category: CropCategory) {
        val firstInCat = FrgCropsData.crops.firstOrNull { it.category == category } ?: _uiState.value.selectedCrop
        _uiState.update { it.copy(selectedCategory = category, selectedCrop = firstInCat) }
        recalculate()
    }

    fun selectCrop(crop: Crop) {
        _uiState.update { it.copy(selectedCrop = crop, selectedCategory = crop.category) }
        recalculate()
    }

    fun updateLandArea(area: Float) {
        _uiState.update { it.copy(landArea = area.coerceAtLeast(0.1f)) }
        recalculate()
    }

    fun updateLandUnit(unit: LandUnit) {
        _uiState.update { it.copy(landUnit = unit) }
        recalculate()
    }

    fun updateSoilFertility(fertility: SoilFertilityClass) {
        _uiState.update { it.copy(soilFertilityClass = fertility) }
        recalculate()
    }

    fun updateCowdung(tonnes: Float) {
        _uiState.update { it.copy(cowdungTonnesPerHa = tonnes.coerceIn(0f, 25f)) }
        recalculate()
    }

    fun toggleDapMode(useDap: Boolean) {
        _uiState.update { it.copy(useDapInsteadOfTsp = useDap) }
        recalculate()
    }

    fun selectAez(aez: AezInfo) {
        _uiState.update {
            it.copy(
                detectedAez = aez,
                locationDescription = "${aez.primaryDistricts.first()} (AEZ ${aez.aezNumber})",
                soilFertilityClass = aez.defaultFertility,
                latitude = aez.centerLatitude,
                longitude = aez.centerLongitude
            )
        }
        recalculate()
        fetchWeatherForCoordinates(aez.centerLatitude, aez.centerLongitude)
    }

    fun applyMappedPlot(plot: MappedPlot) {
        val targetAez = AezDatabase.findByAezNumber(plot.aezNumber)
            ?: AezDatabase.findNearestAez(plot.centerLatitude, plot.centerLongitude)

        val areaDec = plot.areaDecimals.toFloat().coerceAtLeast(0.1f)
        val locDesc = if (plot.upazila.isNotBlank() && plot.district.isNotBlank()) {
            "${plot.upazila}, ${plot.district} (AEZ ${plot.aezNumber})"
        } else {
            "${plot.district.ifEmpty { "Plot" }} (AEZ ${plot.aezNumber})"
        }

        _uiState.update {
            it.copy(
                activePlot = plot,
                landArea = areaDec,
                landUnit = LandUnit.DECIMAL,
                detectedAez = targetAez,
                locationDescription = locDesc,
                soilFertilityClass = targetAez.defaultFertility,
                latitude = plot.centerLatitude,
                longitude = plot.centerLongitude,
                currentTab = 0 // Navigate to Calculator
            )
        }
        recalculate()
        fetchWeatherForCoordinates(plot.centerLatitude, plot.centerLongitude)
    }

    fun clearActivePlot() {
        _uiState.update { it.copy(activePlot = null) }
        recalculate()
    }

    fun fetchCurrentLocation(context: Context) {
        _uiState.update { it.copy(isLocating = true) }
        viewModelScope.launch {
            val locationAezManager = LocationAezManager(context)
            val locResult = locationAezManager.getCurrentLocationAez()
            if (locResult != null) {
                _uiState.update {
                    it.copy(
                        isLocating = false,
                        detectedAez = locResult.aezInfo,
                        locationDescription = "${locResult.detectedDistrict} (AEZ ${locResult.aezInfo.aezNumber})",
                        soilFertilityClass = locResult.aezInfo.defaultFertility,
                        latitude = locResult.latitude,
                        longitude = locResult.longitude
                    )
                }
                fetchWeatherForCoordinates(locResult.latitude, locResult.longitude)
                // Sync user profile if signed in
                val currentUser = _uiState.value.currentUser
                if (currentUser != null && userRepository != null) {
                    userRepository?.syncUserProfile(
                        user = currentUser,
                        detectedAez = locResult.aezInfo.aezNumber,
                        detectedDistrict = locResult.detectedDistrict
                    )
                }
            } else {
                _uiState.update { it.copy(isLocating = false) }
            }
            recalculate()
        }
    }

    fun fetchWeatherForCoordinates(lat: Double, lon: Double) {
        _uiState.update { it.copy(isWeatherLoading = true) }
        viewModelScope.launch {
            val weather = AgriWeatherService.fetchAgriWeather(lat, lon)
            _uiState.update { it.copy(agriWeather = weather, isWeatherLoading = false) }
        }
    }

    fun refreshWeather() {
        val lat = _uiState.value.latitude ?: 25.85
        val lon = _uiState.value.longitude ?: 88.55
        fetchWeatherForCoordinates(lat, lon)
    }

    fun onUserSignedIn(user: FirebaseUser) {
        _uiState.update { it.copy(currentUser = user) }
        val state = _uiState.value
        viewModelScope.launch {
            userRepository?.syncUserProfile(
                user = user,
                detectedAez = state.detectedAez?.aezNumber,
                detectedDistrict = state.locationDescription
            )
            loadSavedCalculations()
        }
    }

    fun onUserSignedOut() {
        _uiState.update { it.copy(currentUser = null, savedCalculations = emptyList()) }
    }

    fun saveCurrentCalculation() {
        val repo = userRepository ?: return
        val currentResult = _uiState.value.result
        _uiState.update { it.copy(isSavingCalculation = true, saveToastMessage = null) }

        viewModelScope.launch {
            try {
                repo.saveCalculation(currentResult)
                _uiState.update {
                    it.copy(
                        isSavingCalculation = false,
                        saveToastMessage = if (it.language == AppLanguage.BANGLA)
                            "সারের হিসাব ক্লাউডে সংরক্ষিত হয়েছে!"
                        else
                            "Calculation saved to Firestore cloud!"
                    )
                }
                loadSavedCalculations()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSavingCalculation = false,
                        saveToastMessage = if (it.language == AppLanguage.BANGLA)
                            "সংরক্ষণ করতে প্রথমে গুগল সাইন-ইন সম্পন্ন করুন।"
                        else
                            "Please sign in with Google to save calculations."
                    )
                }
            }
        }
    }

    fun deleteSavedCalculation(calcId: String) {
        viewModelScope.launch {
            try {
                userRepository?.deleteCalculation(calcId)
                loadSavedCalculations()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadSavedCalculations() {
        val repo = userRepository ?: return
        viewModelScope.launch {
            repo.observeSavedCalculations().collect { list ->
                _uiState.update { it.copy(savedCalculations = list) }
            }
        }
    }

    fun clearSaveToast() {
        _uiState.update { it.copy(saveToastMessage = null) }
    }

    fun requestAiAdvice() {
        val currentResult = _uiState.value.result
        _uiState.update { it.copy(isAiLoading = true) }

        viewModelScope.launch {
            val response = GeminiAgronomistService.getAgronomyAdvice(currentResult)
            response.onSuccess { advice ->
                _uiState.update { it.copy(aiAdvice = advice, isAiLoading = false) }
            }.onFailure {
                _uiState.update { it.copy(isAiLoading = false) }
            }
        }
    }

    fun resetToDefaults() {
        val currentLang = _uiState.value.language
        val user = _uiState.value.currentUser
        _uiState.value = FrgUiState(language = currentLang, currentUser = user)
        recalculate()
    }

    private fun recalculate() {
        val s = _uiState.value
        val newResult = FrgCalculator.calculate(
            crop = s.selectedCrop,
            landArea = s.landArea,
            landUnit = s.landUnit,
            fertilityClass = s.soilFertilityClass,
            cowdungTonnesPerHa = s.cowdungTonnesPerHa,
            useDapInsteadOfTsp = s.useDapInsteadOfTsp,
            detectedAez = s.detectedAez,
            locationDescription = s.locationDescription
        )
        _uiState.update { it.copy(result = newResult) }
    }
}
