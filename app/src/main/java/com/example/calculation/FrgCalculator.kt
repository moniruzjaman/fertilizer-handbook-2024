package com.example.calculation

import com.example.model.AezInfo
import com.example.model.Crop
import com.example.model.FertilizerCalculationResult
import com.example.model.FertilizerRequirementItem
import com.example.model.FertilizerType
import com.example.model.LandUnit
import com.example.model.ScheduleDoseResult
import com.example.model.SoilFertilityClass
import kotlin.math.max

object FrgCalculator {

    fun calculate(
        crop: Crop,
        landArea: Float,
        landUnit: LandUnit,
        fertilityClass: SoilFertilityClass,
        cowdungTonnesPerHa: Float = 0f,
        useDapInsteadOfTsp: Boolean = false,
        detectedAez: AezInfo? = null,
        locationDescription: String? = null
    ): FertilizerCalculationResult {
        val safeLandArea = max(0.01f, landArea)
        val areaInHa = (safeLandArea * landUnit.factorToHectare).toFloat()
        val mult = fertilityClass.multiplier

        // Base requirements scaled by fertility
        val nReq = crop.baseNutrients.nKgPerHa * mult
        val pReq = crop.baseNutrients.pKgPerHa * mult
        val kReq = crop.baseNutrients.kKgPerHa * mult
        val sReq = crop.baseNutrients.sKgPerHa * mult
        val znReq = crop.baseNutrients.znKgPerHa * mult
        val bReq = crop.baseNutrients.bKgPerHa * mult

        // INM credits from organic manure (well-decomposed cowdung)
        val nCredit = cowdungTonnesPerHa * 5.0f
        val pCredit = cowdungTonnesPerHa * 2.5f
        val kCredit = cowdungTonnesPerHa * 5.0f

        val netN = max(0f, nReq - nCredit)
        val netP = max(0f, pReq - pCredit)
        val netK = max(0f, kReq - kCredit)
        val netS = sReq
        val netZn = znReq
        val netB = bReq

        val items = mutableListOf<FertilizerRequirementItem>()

        val ureaKg: Float
        val tspOrDapKg: Float

        if (useDapInsteadOfTsp) {
            // DAP supplies 20% P and 18% N
            tspOrDapKg = if (netP > 0) (netP / 0.20f) * areaInHa else 0f
            val nSuppliedByDapPerHa = if (areaInHa > 0) (tspOrDapKg / areaInHa) * 0.18f else 0f
            val remainingNPerHa = max(0f, netN - nSuppliedByDapPerHa)
            ureaKg = if (remainingNPerHa > 0) (remainingNPerHa / 0.46f) * areaInHa else 0f

            items.add(
                createItem(FertilizerType.UREA, "Nitrogen (Adjusted for DAP)", ureaKg)
            )
            items.add(
                createItem(FertilizerType.DAP, "Phosphorus + Nitrogen", tspOrDapKg)
            )
        } else {
            // Standard TSP + Urea
            ureaKg = if (netN > 0) (netN / 0.46f) * areaInHa else 0f
            tspOrDapKg = if (netP > 0) (netP / 0.20f) * areaInHa else 0f

            items.add(
                createItem(FertilizerType.UREA, "Nitrogen (N)", ureaKg)
            )
            items.add(
                createItem(FertilizerType.TSP, "Phosphorus (P)", tspOrDapKg)
            )
        }

        val mopKg = if (netK > 0) (netK / 0.50f) * areaInHa else 0f
        items.add(createItem(FertilizerType.MOP, "Potassium (K)", mopKg))

        val gypsumKg = if (netS > 0) (netS / 0.18f) * areaInHa else 0f
        if (gypsumKg > 0.01f) {
            items.add(createItem(FertilizerType.GYPSUM, "Sulfur (S)", gypsumKg))
        }

        val zincKg = if (netZn > 0) (netZn / 0.21f) * areaInHa else 0f
        if (zincKg > 0.01f) {
            items.add(createItem(FertilizerType.ZINC_SULPHATE, "Zinc (Zn)", zincKg))
        }

        val boronKg = if (netB > 0) (netB / 0.17f) * areaInHa else 0f
        if (boronKg > 0.01f) {
            items.add(createItem(FertilizerType.BORIC_ACID, "Boron (B)", boronKg))
        }

        val totalCost = items.sumOf { it.estimatedCostTk.toDouble() }.toFloat()
        val totalBags = items.sumOf { it.bags50kg.toDouble() }.toFloat()

        // Calculate split schedule
        val scheduleDoses = crop.splitSchedule.map { split ->
            ScheduleDoseResult(
                stageName = split.stageName,
                bengaliStageName = split.bengaliStageName,
                timingDescription = split.timingDescription,
                ureaKg = ureaKg * (split.ureaPercent / 100f),
                tspKg = tspOrDapKg * (split.tspPercent / 100f),
                mopKg = mopKg * (split.mopPercent / 100f),
                gypsumKg = gypsumKg * (split.gypsumPercent / 100f),
                zincKg = zincKg * (split.zincPercent / 100f),
                boronKg = boronKg * (split.boronPercent / 100f)
            )
        }

        return FertilizerCalculationResult(
            crop = crop,
            landArea = safeLandArea,
            landUnit = landUnit,
            areaInHectares = areaInHa,
            fertilityClass = fertilityClass,
            cowdungTonnesPerHa = cowdungTonnesPerHa,
            useDapInsteadOfTsp = useDapInsteadOfTsp,
            items = items,
            totalCostTk = totalCost,
            totalBags50kg = totalBags,
            scheduleDoses = scheduleDoses,
            detectedAez = detectedAez,
            locationDescription = locationDescription
        )
    }

    private fun createItem(type: FertilizerType, nutrient: String, kg: Float): FertilizerRequirementItem {
        val safeKg = max(0f, kg)
        return FertilizerRequirementItem(
            fertilizerType = type,
            nutrientName = nutrient,
            recommendedKg = safeKg,
            bags50kg = safeKg / 50f,
            estimatedCostTk = safeKg * type.defaultPricePerKgTk
        )
    }
}
