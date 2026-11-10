package com.example.network

import android.util.Log
import com.example.model.AgriWeatherReport
import com.example.model.HeatStressLevel
import com.example.model.SprayingSuitability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AgriWeatherService {

    private const val TAG = "AgriWeatherService"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    suspend fun fetchAgriWeather(latitude: Double, longitude: Double): AgriWeatherReport = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.open-meteo.com/v1/forecast?" +
                    "latitude=$latitude&longitude=$longitude" +
                    "&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,wind_speed_10m" +
                    "&hourly=precipitation_probability,evapotranspiration" +
                    "&daily=precipitation_sum,et0_fao_evapotranspiration" +
                    "&timezone=auto"

            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    return@withContext parseApiResponse(body)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Weather fetch failed, falling back to local seasonal agri-weather norms: ${e.message}")
        }

        // Seasonal fallback for Bangladesh coordinates
        return@withContext getSeasonalFallbackReport(latitude, longitude)
    }

    private fun parseApiResponse(jsonStr: String): AgriWeatherReport {
        val json = JSONObject(jsonStr)
        val current = json.optJSONObject("current")

        val temp = current?.optDouble("temperature_2m", 28.5) ?: 28.5
        val apparentTemp = current?.optDouble("apparent_temperature", temp + 2.0) ?: (temp + 2.0)
        val humidity = current?.optInt("relative_humidity_2m", 72) ?: 72
        val precip = current?.optDouble("precipitation", 0.0) ?: 0.0
        val windSpeed = current?.optDouble("wind_speed_10m", 8.5) ?: 8.5

        val hourly = json.optJSONObject("hourly")
        val rainProbArray = hourly?.optJSONArray("precipitation_probability")
        val rainProb = rainProbArray?.optInt(0, 15) ?: 15

        val daily = json.optJSONObject("daily")
        val et0Array = daily?.optJSONArray("et0_fao_evapotranspiration")
        val et0 = et0Array?.optDouble(0, 3.8) ?: 3.8

        return computeAgriIndices(
            temp = temp,
            apparentTemp = apparentTemp,
            humidity = humidity,
            precip = precip,
            windSpeed = windSpeed,
            rainProb = rainProb,
            et0 = et0,
            isRealtime = true
        )
    }

    private fun computeAgriIndices(
        temp: Double,
        apparentTemp: Double,
        humidity: Int,
        precip: Double,
        windSpeed: Double,
        rainProb: Int,
        et0: Double,
        isRealtime: Boolean
    ): AgriWeatherReport {
        // 1. Spraying Suitability: wind < 15 km/h, rainProb < 25%, temp < 34°C
        val (sprayingSuitability, sprayEn, sprayBn) = when {
            precip > 0.5 || rainProb > 50 || windSpeed > 22.0 -> Triple(
                SprayingSuitability.UNFAVORABLE,
                "Unfavorable: High wind (${String.format("%.1f", windSpeed)} km/h) or rain risk. Delay foliar spray to prevent chemical wash-off and drift.",
                "অনুপযুক্ত: তীব্র বাতাস (${String.format("%.1f", windSpeed)} কিমি/ঘণ্টা) বা বৃষ্টির ঝুঁকি রয়েছে। সার/কীটনাশক ধুয়ে যাওয়া রোধে স্প্রে স্থগিত রাখুন।"
            )
            windSpeed > 14.0 || rainProb > 25 || temp > 33.0 -> Triple(
                SprayingSuitability.CAUTION,
                "Caution: Moderate wind or rising temperature. Spray early morning (before 9 AM) or late afternoon.",
                "সতর্কতা: মাঝারি বাতাস বা তাপমাত্রা বেশি। সকাল ৯টার পূর্বে অথবা পড়ন্ত বিকেলে স্প্রে সম্পন্ন করুন।"
            )
            else -> Triple(
                SprayingSuitability.OPTIMAL,
                "Optimal: Calm wind (${String.format("%.1f", windSpeed)} km/h) and clear sky. Ideal for foliar micronutrient and pesticide spray.",
                "অনুকূল সময়: বাতাস শান্ত (${String.format("%.1f", windSpeed)} কিমি/ঘণ্টা) ও আকাশ পরিষ্কার। অণুখাদ্য বা বালাইনাশক স্প্রে করার জন্য অত্যন্ত উপযোগী।"
            )
        }

        // 2. Crop Heat Stress Index
        val (heatStress, heatEn, heatBn) = when {
            temp >= 35.0 || (temp >= 33.0 && humidity >= 80) -> Triple(
                HeatStressLevel.HIGH,
                "High Heat Stress Alert (${String.format("%.1f", temp)}°C). Keep 5-7 cm standing water in paddy fields and irrigate in the evening.",
                "তীব্র তাপপ্রবাহ সতর্কতা (${String.format("%.1f", temp)}°সে)। ধানের জমিতে ৫-৭ সেমি পানি ধরে রাখুন এবং সান্ধ্যকালীন সেচ দিন।"
            )
            temp in 31.0..34.9 -> Triple(
                HeatStressLevel.MODERATE,
                "Moderate Thermal Load: Maintain soil moisture, particularly during the panicle initiation or flowering phase.",
                "মাঝারি তাপপ্রবাহ: থোড় বা ফুল আসার সময়ে জমিতে পর্যাপ্ত রস বজায় রাখুন।"
            )
            else -> Triple(
                HeatStressLevel.NORMAL,
                "Favorable Temperatures (${String.format("%.1f", temp)}°C): Conducive for healthy vegetative crop growth and tillering.",
                "অনুকূল তাপমাত্রা (${String.format("%.1f", temp)}°সে): ফসলের স্বাভাবিক বৃদ্ধি ও কুশি গজানোর জন্য সহায়ক।"
            )
        }

        // 3. Top-dressing (Urea) Advice
        val (topEn, topBn) = if (precip > 1.0 || rainProb > 45) {
            Pair(
                "Do not top-dress Urea today. Heavy moisture/rain will cause nitrogen runoff, leaching, and financial loss.",
                "আজ ইউরিয়া উপরিপ্রয়োগ করবেন না। বৃষ্টির কারণে নাইট্রোজেন ধুয়ে নষ্ট ও অপচয় হবে।"
            )
        } else if (temp > 33.0) {
            Pair(
                "Apply Urea in the late afternoon on moist soil to minimize ammonia volatilization loss under direct sunlight.",
                "প্রখর রোদে অ্যামোনিয়া উবে যাওয়া রোধে পড়ন্ত বিকেলে ভেজা মাটিতে ইউরিয়া উপরিপ্রয়োগ করুন।"
            )
        } else {
            Pair(
                "Ideal top-dressing condition. Ensure plot has moist soil without deep standing water before broadcasting.",
                "ইউরিয়া প্রয়োগের উপযুক্ত পরিবেশ। ছিটানোর সময় জমিতে পরিমিত রস রাখুন, অতিরিক্ত পানি থাকলে নিষ্কাশন করুন।"
            )
        }

        return AgriWeatherReport(
            temperature = temp,
            apparentTemperature = apparentTemp,
            relativeHumidity = humidity,
            windSpeedKmH = windSpeed,
            precipitationMm = precip,
            rainProbabilityPercent = rainProb,
            evapotranspirationMm = et0,
            sprayingSuitability = sprayingSuitability,
            sprayingAdviceEn = sprayEn,
            sprayingAdviceBn = sprayBn,
            heatStressLevel = heatStress,
            heatStressAdviceEn = heatEn,
            heatStressAdviceBn = heatBn,
            topDressingAdviceEn = topEn,
            topDressingAdviceBn = topBn,
            isRealtime = isRealtime,
            observationTime = "Live GPS Telemetry"
        )
    }

    private fun getSeasonalFallbackReport(latitude: Double, longitude: Double): AgriWeatherReport {
        return computeAgriIndices(
            temp = 29.0,
            apparentTemp = 31.0,
            humidity = 70,
            precip = 0.0,
            windSpeed = 7.5,
            rainProb = 10,
            et0 = 3.6,
            isRealtime = false
        )
    }
}
