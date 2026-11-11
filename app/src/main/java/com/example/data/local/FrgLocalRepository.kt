package com.example.data.local

import android.content.Context
import android.util.Log
import com.example.data.FrgCropsData
import com.example.data.local.entity.CachedCalculationEntity
import com.example.data.local.entity.CachedPlotEntity
import com.example.data.local.entity.FrgGuidelineEntity
import com.example.model.FertilizerCalculationResult
import com.example.model.FertilizerType
import com.example.model.MappedPlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class FrgLocalRepository(private val database: FrgAppDatabase) {

    constructor(context: Context) : this(FrgAppDatabase.getInstance(context))

    private val calculationDao = database.calculationDao()
    private val guidelineDao = database.frgGuidelineDao()
    private val plotDao = database.plotDao()
    private val TAG = "FrgLocalRepository"

    val recentCalculations: Flow<List<CachedCalculationEntity>> = calculationDao.getRecentCalculations(15)
    val allCachedCalculations: Flow<List<CachedCalculationEntity>> = calculationDao.getAllCalculations()
    val allCachedPlots: Flow<List<CachedPlotEntity>> = plotDao.getAllPlots()
    val allGuidelines: Flow<List<FrgGuidelineEntity>> = guidelineDao.getAllGuidelines()

    /**
     * Seeds Room cache with all official BARC FRG-2024 crop guidelines on initial launch
     * to guarantee 100% offline access in remote rural areas.
     */
    suspend fun seedVitalGuidelinesIfEmpty() = withContext(Dispatchers.IO) {
        try {
            val count = guidelineDao.getCount()
            if (count == 0) {
                val entities = FrgCropsData.crops.map { crop ->
                    val scheduleSummary = crop.splitSchedule.joinToString(" | ") { dose ->
                        "${dose.bengaliStageName}: ইউরিয়া ${dose.ureaPercent.toInt()}%"
                    }
                    val advice = if (crop.agronomicNotes.isNotBlank()) {
                        crop.agronomicNotes
                    } else {
                        "মাটির উর্বরতা ও আর্দ্রতা অনুযায়ী সুষম মাত্রায় সার ব্যবহার করুন।"
                    }

                    FrgGuidelineEntity(
                        cropId = crop.id,
                        cropName = crop.name,
                        bengaliName = crop.bengaliName,
                        category = crop.category.displayName,
                        targetYieldText = crop.targetYieldText,
                        baseN = crop.baseNutrients.nKgPerHa,
                        baseP = crop.baseNutrients.pKgPerHa,
                        baseK = crop.baseNutrients.kKgPerHa,
                        baseS = crop.baseNutrients.sKgPerHa,
                        baseZn = crop.baseNutrients.znKgPerHa,
                        baseB = crop.baseNutrients.bKgPerHa,
                        scheduleDescriptionBn = scheduleSummary,
                        specialAdviceBn = advice
                    )
                }
                guidelineDao.insertGuidelines(entities)
                Log.d(TAG, "Successfully seeded ${entities.size} vital FRG-2024 guidelines into Room database.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed seeding guidelines to Room: ${e.message}", e)
        }
    }

    /**
     * Automatically caches a calculation result to Room so users can review it without internet.
     */
    suspend fun cacheCalculation(result: FertilizerCalculationResult): String = withContext(Dispatchers.IO) {
        var urea = 0f
        var tspOrDap = 0f
        var mop = 0f
        var gypsum = 0f
        var zinc = 0f
        var boron = 0f

        result.items.forEach { item ->
            when (item.fertilizerType) {
                FertilizerType.UREA -> urea = item.recommendedKg
                FertilizerType.TSP, FertilizerType.DAP -> tspOrDap = item.recommendedKg
                FertilizerType.MOP -> mop = item.recommendedKg
                FertilizerType.GYPSUM -> gypsum = item.recommendedKg
                FertilizerType.ZINC_SULPHATE -> zinc = item.recommendedKg
                FertilizerType.BORIC_ACID -> boron = item.recommendedKg
            }
        }

        val entity = CachedCalculationEntity(
            cropId = result.crop.id,
            cropName = result.crop.name,
            cropBengaliName = result.crop.bengaliName,
            cropCategory = result.crop.category.displayName,
            landArea = result.landArea,
            landUnit = result.landUnit.name,
            areaInHectares = result.areaInHectares,
            soilFertilityClass = result.fertilityClass.name,
            cowdungTonnesPerHa = result.cowdungTonnesPerHa,
            useDapInsteadOfTsp = result.useDapInsteadOfTsp,
            aezNumber = result.detectedAez?.aezNumber,
            aezName = result.detectedAez?.name,
            locationDescription = result.locationDescription,
            totalCostTk = result.totalCostTk,
            totalBags50kg = result.totalBags50kg,
            ureaKg = urea,
            tspOrDapKg = tspOrDap,
            mopKg = mop,
            gypsumKg = gypsum,
            zincKg = zinc,
            boronKg = boron
        )

        calculationDao.insertCalculation(entity)
        Log.d(TAG, "Cached calculation ${entity.id} to local Room database.")
        entity.id
    }

    suspend fun deleteCachedCalculation(id: String) = withContext(Dispatchers.IO) {
        calculationDao.deleteCalculationById(id)
    }

    suspend fun clearAllCalculations() = withContext(Dispatchers.IO) {
        calculationDao.clearAllCalculations()
    }

    /**
     * Caches a mapped plot and its boundary points into Room.
     */
    suspend fun cachePlot(plot: MappedPlot) = withContext(Dispatchers.IO) {
        val boundaryJson = JSONArray().apply {
            plot.boundaryPoints.forEach { pt ->
                put(JSONObject().apply {
                    put("lat", pt.latitude)
                    put("lon", pt.longitude)
                })
            }
        }.toString()

        val entity = CachedPlotEntity(
            id = plot.id,
            name = plot.name,
            cropName = plot.cropName,
            centerLat = plot.centerLatitude,
            centerLon = plot.centerLongitude,
            radiusMeters = plot.radiusMeters,
            isPolygon = plot.isPolygon,
            boundaryJson = boundaryJson,
            areaDecimals = plot.areaDecimals,
            areaBigha = plot.areaBigha,
            areaAcres = plot.areaAcres,
            areaSquareMeters = plot.areaSquareMeters,
            perimeterMeters = plot.perimeterMeters,
            village = plot.village,
            union = plot.union,
            upazila = plot.upazila,
            district = plot.district,
            division = plot.division,
            aezNumber = plot.aezNumber,
            aezName = plot.aezName,
            dominantLandTypeBn = plot.dominantLandTypeBn,
            soilTextureBn = plot.soilTextureBn
        )
        plotDao.insertPlot(entity)
    }

    suspend fun deleteCachedPlot(id: String) = withContext(Dispatchers.IO) {
        plotDao.deletePlotById(id)
    }
}
