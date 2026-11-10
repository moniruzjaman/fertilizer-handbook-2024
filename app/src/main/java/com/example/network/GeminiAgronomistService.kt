package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.model.AiAgronomyAdvice
import com.example.model.FertilizerCalculationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAgronomistService {

    private const val TAG = "GeminiAgronomist"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getAgronomyAdvice(result: FertilizerCalculationResult): Result<AiAgronomyAdvice> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is placeholder or empty. Using expert offline agronomist database.")
            return@withContext Result.success(generateOfflineAdvice(result))
        }

        val prompt = buildPrompt(result)

        try {
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray()
                val contentObj = JSONObject()
                val partsArray = JSONArray()
                partsArray.put(JSONObject().put("text", prompt))
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
                put("contents", contentsArray)

                val generationConfig = JSONObject().apply {
                    put("temperature", 0.3)
                    put("responseMimeType", "application/json")
                }
                put("generationConfig", generationConfig)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                Log.e(TAG, "Gemini API failed with code ${response.code}: $errorBody")
                return@withContext Result.success(generateOfflineAdvice(result))
            }

            val responseBody = response.body?.string() ?: ""
            val parsedResult = parseResponse(responseBody, result)
            Result.success(parsedResult)
        } catch (e: Exception) {
            Log.e(TAG, "Error generating agronomy advice: ${e.message}", e)
            Result.success(generateOfflineAdvice(result))
        }
    }

    private fun buildPrompt(result: FertilizerCalculationResult): String {
        val fertList = result.items.joinToString(", ") { "${it.fertilizerType.englishName}: ${"%.1f".format(it.recommendedKg)} kg" }
        return """
You are a senior agronomist at Bangladesh Agricultural Research Council (BARC) specializing in the Fertilizer Recommendation Guide 2024 (FRG-2024).
A farmer has requested fertilizer guidance with the following parameters:

Crop: ${result.crop.name} (${result.crop.bengaliName})
Target Yield: ${result.crop.targetYieldText}
Land Area: ${result.landArea} ${result.landUnit.displayName} (${"%.4f".format(result.areaInHectares)} Hectare)
Soil Fertility Status: ${result.fertilityClass.displayName}
Organic Manure: ${result.cowdungTonnesPerHa} tonnes/ha cowdung applied
Fertilizer Requirement Calculated: $fertList
Estimated Total Cost: ৳ ${"%.0f".format(result.totalCostTk)} BDT

Please provide comprehensive agronomic guidance formatted as a valid JSON object matching this schema:
{
  "cropOverview": "2-3 sentences assessing the nutrient demands of this crop in Bangladesh agro-climatic conditions.",
  "soilAndTimingAnalysis": "Detailed analysis of soil status impact, organic manure integration, and when to apply basal vs top dressing.",
  "splitApplicationGuidance": "Step-by-step guidance on Urea and MoP split application to maximize fertilizer use efficiency (FUE).",
  "warningAndPrecautions": "Key precautions (e.g. wet leaf scorching, drainage before top dressing, deep placement, zinc-phosphate antagonism).",
  "climateSmartTip": "One practical climate-smart practice (e.g. Guti Urea / USG, AWD irrigation, green manuring, biofertilizer)."
}
Output pure JSON only.
""".trimIndent()
    }

    private fun parseResponse(rawResponse: String, result: FertilizerCalculationResult): AiAgronomyAdvice {
        try {
            val root = JSONObject(rawResponse)
            val candidates = root.optJSONArray("candidates")
            val content = candidates?.optJSONObject(0)?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            val cleaned = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val json = JSONObject(cleaned)

            return AiAgronomyAdvice(
                cropOverview = json.optString("cropOverview", "${result.crop.name} requires balanced NPK and micronutrients for optimal yield."),
                soilAndTimingAnalysis = json.optString("soilAndTimingAnalysis", "Soil test based split application ensures high nutrient uptake without leaching."),
                splitApplicationGuidance = json.optString("splitApplicationGuidance", "Apply all TSP, Gypsum and Zinc at final land prep; split Urea according to physiological growth stages."),
                warningAndPrecautions = json.optString("warningAndPrecautions", "Never apply urea during midday heat or when leaves are dewy/wet."),
                climateSmartTip = json.optString("climateSmartTip", "Implement Alternate Wetting and Drying (AWD) water management to save irrigation and reduce methane.")
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse Gemini JSON: ${e.message}")
            return generateOfflineAdvice(result)
        }
    }

    fun generateOfflineAdvice(result: FertilizerCalculationResult): AiAgronomyAdvice {
        val cropName = result.crop.name
        val landDesc = "${result.landArea} ${result.landUnit.displayName}"

        val overview = "${cropName} (${result.crop.bengaliName}) requires systematic nutrient balancing under BARC FRG-2024 standards. " +
                "For your plot of $landDesc, timely supply of primary NPK alongside secondary sulfur and micronutrients is essential to reach ${result.crop.targetYieldText}."

        val timing = "Soil fertility is categorized as ${result.fertilityClass.displayName}. " +
                if (result.cowdungTonnesPerHa > 0) {
                    "Your application of ${result.cowdungTonnesPerHa} t/ha organic manure supplements soil organic matter and offsets chemical nitrogen/potash."
                } else {
                    "Applying well-decomposed cowdung (3-5 t/ha) or vermicompost will improve water holding capacity and fertilizer use efficiency."
                }

        val split = "Ensure basal fertilizers (TSP/DAP, Gypsum, Zinc Sulphate, and Boric Acid) are thoroughly mixed into the top 10-15 cm soil layer during final land preparation. " +
                "Divide Urea into the designated split doses to avoid volatilization and leaching."

        val warning = "1. Avoid broadcasting Urea on wet foliage to prevent severe leaf burning.\n" +
                "2. When applying Zinc Sulphate and TSP together, apply Zinc in final ploughing or maintain 3-5 days gap to prevent Zinc-Phosphate fixation.\n" +
                "3. In flooded fields, drain standing water slightly before top dressing, then re-flood after 24-48 hours."

        val tip = "Consider Urea Deep Placement (UDP / Guti Urea) or leaf color chart (LCC) monitoring. This can reduce urea consumption by 25-30% while increasing yield by 10%."

        return AiAgronomyAdvice(
            cropOverview = overview,
            soilAndTimingAnalysis = timing,
            splitApplicationGuidance = split,
            warningAndPrecautions = warning,
            climateSmartTip = tip
        )
    }
}
