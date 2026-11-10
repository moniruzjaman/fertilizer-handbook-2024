package com.example.model

enum class SprayingSuitability(val displayNameEn: String, val displayNameBn: String) {
    OPTIMAL("Optimal for Spraying", "স্প্রে করার অনুকূল সময়"),
    CAUTION("Caution / Moderate Wind or Rain Risk", "সতর্কতা / মধ্যম বাতাস বা বৃষ্টির ঝুঁকি"),
    UNFAVORABLE("Unfavorable - Avoid Spraying", "অনুপযুক্ত - স্প্রে পরিহার করুন")
}

enum class HeatStressLevel(val displayNameEn: String, val displayNameBn: String) {
    NORMAL("Normal Thermal Conditions", "স্বাভাবিক তাপীয় পরিবেশ"),
    MODERATE("Moderate Heat Stress", "মাঝারি তাপপ্রবাহ / শস্য চাপ"),
    HIGH("High Heat Stress Alert", "তীব্র তাপপ্রবাহ সতর্কতা")
}

data class AgriWeatherReport(
    val temperature: Double,
    val apparentTemperature: Double,
    val relativeHumidity: Int,
    val windSpeedKmH: Double,
    val precipitationMm: Double,
    val rainProbabilityPercent: Int,
    val evapotranspirationMm: Double,
    val sprayingSuitability: SprayingSuitability,
    val sprayingAdviceEn: String,
    val sprayingAdviceBn: String,
    val heatStressLevel: HeatStressLevel,
    val heatStressAdviceEn: String,
    val heatStressAdviceBn: String,
    val topDressingAdviceEn: String,
    val topDressingAdviceBn: String,
    val isRealtime: Boolean,
    val observationTime: String
)
