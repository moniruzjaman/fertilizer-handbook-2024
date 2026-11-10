package com.example.model

enum class CropCategory(val displayName: String, val bengaliName: String) {
    CEREALS("Cereals & Grains", "দানাশস্য"),
    PULSES("Pulses & Legumes", "ডাল ফসল"),
    OILSEEDS("Oilseeds", "তৈলবীজ"),
    VEGETABLES("Vegetables & Tubers", "শাকসবজি ও কন্দাল"),
    SPICES("Spices", "মসলা ফসল"),
    CASH_CROPS("Cash Crops", "অর্থকরী ফসল")
}

enum class SoilFertilityClass(val displayName: String, val multiplier: Float, val description: String) {
    VERY_LOW("Very Low (খুব কম)", 1.25f, "Severely depleted soil; maximum fertilizer response"),
    LOW("Low (কম)", 1.10f, "Deficient soil; requires 10% above standard rate"),
    MEDIUM("Medium (মধ্যম)", 1.00f, "Standard baseline fertility as per FRG 2024"),
    OPTIMUM("Optimum (অনুকূল)", 0.75f, "Good fertility; requires 25% lower application"),
    HIGH("High (উচ্চ)", 0.45f, "Rich fertility; apply maintenance dose only"),
    VERY_HIGH("Very High (খুব উচ্চ)", 0.20f, "No nitrogen needed; trace replenishment only")
}

enum class LandUnit(val displayName: String, val bengaliName: String, val factorToHectare: Double) {
    DECIMAL("Decimal / Shatak", "শতাংশ / শতক", 0.00404686),
    BIGHA("Bigha (33 Dec)", "বিঘা (৩৩ শতক)", 0.133546),
    KATHA("Katha (1.65 Dec)", "কাঠা (১.৬৫ শতক)", 0.006677),
    ACRE("Acre (100 Dec)", "একর (১০০ শতক)", 0.404686),
    HECTARE("Hectare (10,000 m²)", "হেক্টর", 1.0)
}

enum class FertilizerType(
    val englishName: String,
    val bengaliName: String,
    val primaryNutrient: String,
    val nutrientPercentage: Float,
    val defaultPricePerKgTk: Float,
    val description: String
) {
    UREA("Urea", "ইউরিয়া", "N (Nitrogen)", 46f, 27f, "46% Nitrogen. Essential for leafy vegetative growth."),
    TSP("TSP (Triple Superphosphate)", "টিএসপি", "P (Phosphorus)", 20f, 27f, "20% Phosphorus (46% P2O5). Root and seedling development."),
    DAP("DAP (Di-Ammonium Phosphate)", "ডিএপি", "N + P", 20f, 21f, "18% N + 20% P. Subsidized alternative to TSP."),
    MOP("MoP (Muriate of Potash)", "এমওপি", "K (Potassium)", 50f, 20f, "50% Potassium (60% K2O). Disease resistance & grain filling."),
    GYPSUM("Gypsum", "জিপসাম", "S (Sulfur)", 18f, 12f, "18% Sulfur & Calcium. Crucial for oilseeds and cereals."),
    ZINC_SULPHATE("Zinc Sulphate (Hepta)", "দস্তা / জিংক সালফেট", "Zn (Zinc)", 21f, 170f, "21% Zinc. Prevents Khaira disease and chlorosis."),
    BORIC_ACID("Boric Acid / Solubor", "বোরিক এসিড / সলুবোর", "B (Boron)", 17f, 260f, "17% Boron. Essential for pollination, grain set and tubers.")
}

data class BaseNutrientRequirement(
    val nKgPerHa: Float,
    val pKgPerHa: Float,
    val kKgPerHa: Float,
    val sKgPerHa: Float,
    val znKgPerHa: Float = 0f,
    val bKgPerHa: Float = 0f
)

data class SplitDose(
    val stageName: String,
    val bengaliStageName: String,
    val timingDescription: String,
    val ureaPercent: Float,
    val tspPercent: Float = 100f,
    val mopPercent: Float = 100f,
    val gypsumPercent: Float = 100f,
    val zincPercent: Float = 100f,
    val boronPercent: Float = 100f
)

data class Crop(
    val id: String,
    val name: String,
    val bengaliName: String,
    val category: CropCategory,
    val targetYieldText: String,
    val baseNutrients: BaseNutrientRequirement,
    val splitSchedule: List<SplitDose>,
    val agronomicNotes: String
)

data class FertilizerRequirementItem(
    val fertilizerType: FertilizerType,
    val nutrientName: String,
    val recommendedKg: Float,
    val bags50kg: Float,
    val estimatedCostTk: Float
)

data class ScheduleDoseResult(
    val stageName: String,
    val bengaliStageName: String,
    val timingDescription: String,
    val ureaKg: Float,
    val tspKg: Float,
    val mopKg: Float,
    val gypsumKg: Float,
    val zincKg: Float,
    val boronKg: Float
)

data class FertilizerCalculationResult(
    val crop: Crop,
    val landArea: Float,
    val landUnit: LandUnit,
    val areaInHectares: Float,
    val fertilityClass: SoilFertilityClass,
    val cowdungTonnesPerHa: Float,
    val useDapInsteadOfTsp: Boolean,
    val items: List<FertilizerRequirementItem>,
    val totalCostTk: Float,
    val totalBags50kg: Float,
    val scheduleDoses: List<ScheduleDoseResult>,
    val detectedAez: AezInfo? = null,
    val locationDescription: String? = null
)

data class NutrientDeficiency(
    val nutrient: String,
    val symbol: String,
    val bengaliName: String,
    val primaryFunction: String,
    val deficiencySymptoms: String,
    val correctiveAction: String
)

data class AiAgronomyAdvice(
    val cropOverview: String,
    val soilAndTimingAnalysis: String,
    val splitApplicationGuidance: String,
    val warningAndPrecautions: String,
    val climateSmartTip: String
)
