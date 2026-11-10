package com.example

import com.example.calculation.FrgCalculator
import com.example.data.AezDatabase
import com.example.data.FrgCropsData
import com.example.model.FertilizerType
import com.example.model.LandUnit
import com.example.model.SoilFertilityClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FrgCalculatorTest {

    @Test
    fun testBoroRiceOneBighaCalculation() {
        val boro = FrgCropsData.crops.first { it.id == "boro_hyv" }
        // 33 decimals = 1 Bigha
        val result = FrgCalculator.calculate(
            crop = boro,
            landArea = 33f,
            landUnit = LandUnit.DECIMAL,
            fertilityClass = SoilFertilityClass.MEDIUM,
            cowdungTonnesPerHa = 0f,
            useDapInsteadOfTsp = false
        )

        assertTrue("Area in hectares should be ~0.1335", result.areaInHectares in 0.13f..0.14f)
        assertTrue("Total cost should be greater than 0", result.totalCostTk > 0f)
        assertTrue("Should include Urea, TSP, MoP", result.items.any { it.fertilizerType == FertilizerType.UREA })
        assertTrue("Should include TSP", result.items.any { it.fertilizerType == FertilizerType.TSP })
        assertTrue("Should include MoP", result.items.any { it.fertilizerType == FertilizerType.MOP })

        val urea = result.items.first { it.fertilizerType == FertilizerType.UREA }
        assertTrue("Urea should be approx 40 kg for 1 bigha", urea.recommendedKg in 35f..45f)
    }

    @Test
    fun testSoilFertilityMultiplierScaling() {
        val crop = FrgCropsData.crops.first()
        val resMedium = FrgCalculator.calculate(crop, 1f, LandUnit.HECTARE, SoilFertilityClass.MEDIUM)
        val resVeryLow = FrgCalculator.calculate(crop, 1f, LandUnit.HECTARE, SoilFertilityClass.VERY_LOW)
        val resOptimum = FrgCalculator.calculate(crop, 1f, LandUnit.HECTARE, SoilFertilityClass.OPTIMUM)

        val ureaMed = resMedium.items.first { it.fertilizerType == FertilizerType.UREA }.recommendedKg
        val ureaVL = resVeryLow.items.first { it.fertilizerType == FertilizerType.UREA }.recommendedKg
        val ureaOpt = resOptimum.items.first { it.fertilizerType == FertilizerType.UREA }.recommendedKg

        assertEquals(ureaMed * 1.25f, ureaVL, 1.0f)
        assertEquals(ureaMed * 0.75f, ureaOpt, 1.0f)
    }

    @Test
    fun testInmManureCreditReducesUreaAndTsp() {
        val crop = FrgCropsData.crops.first()
        val withoutManure = FrgCalculator.calculate(crop, 1f, LandUnit.HECTARE, SoilFertilityClass.MEDIUM, 0f)
        val withManure = FrgCalculator.calculate(crop, 1f, LandUnit.HECTARE, SoilFertilityClass.MEDIUM, 5f)

        val ureaNo = withoutManure.items.first { it.fertilizerType == FertilizerType.UREA }.recommendedKg
        val ureaWith = withManure.items.first { it.fertilizerType == FertilizerType.UREA }.recommendedKg

        assertTrue("Manure should reduce Urea requirement", ureaWith < ureaNo)
    }

    @Test
    fun testDapModeReducesUreaRequirement() {
        val crop = FrgCropsData.crops.first()
        val tspMode = FrgCalculator.calculate(crop, 1f, LandUnit.HECTARE, SoilFertilityClass.MEDIUM, 0f, useDapInsteadOfTsp = false)
        val dapMode = FrgCalculator.calculate(crop, 1f, LandUnit.HECTARE, SoilFertilityClass.MEDIUM, 0f, useDapInsteadOfTsp = true)

        val ureaTsp = tspMode.items.first { it.fertilizerType == FertilizerType.UREA }.recommendedKg
        val ureaDap = dapMode.items.first { it.fertilizerType == FertilizerType.UREA }.recommendedKg

        assertTrue("DAP provides nitrogen so Urea should be lower", ureaDap < ureaTsp)
    }

    @Test
    fun testSplitDosesSumEqualsTotal() {
        val crop = FrgCropsData.crops.first { it.id == "boro_hyv" }
        val result = FrgCalculator.calculate(crop, 33f, LandUnit.DECIMAL, SoilFertilityClass.MEDIUM)

        val totalUrea = result.items.first { it.fertilizerType == FertilizerType.UREA }.recommendedKg
        val splitUreaSum = result.scheduleDoses.sumOf { it.ureaKg.toDouble() }.toFloat()

        assertEquals("Sum of split urea should match total urea", totalUrea, splitUreaSum, 0.5f)
    }

    @Test
    fun testAezNearestMatching() {
        // Dinajpur coordinates ~25.63, 88.64
        val matchedAez = AezDatabase.findNearestAez(25.63, 88.64)
        assertNotNull(matchedAez)
        assertEquals(1, matchedAez.aezNumber)
        assertTrue(matchedAez.primaryDistricts.contains("Dinajpur"))
    }

    @Test
    fun testAezDistrictLookup() {
        val aez = AezDatabase.findByDistrict("Mymensingh")
        assertNotNull(aez)
        assertTrue("Mymensingh should map to Old Brahmaputra or Madhupur", aez?.aezNumber == 9 || aez?.aezNumber == 28)
    }

    @Test
    fun testCalculationWithAezInfo() {
        val aez1 = AezDatabase.aezList.first { it.aezNumber == 1 }
        val crop = FrgCropsData.crops.first()
        val result = FrgCalculator.calculate(
            crop = crop,
            landArea = 33f,
            landUnit = LandUnit.DECIMAL,
            fertilityClass = aez1.defaultFertility,
            detectedAez = aez1,
            locationDescription = "Dinajpur (AEZ 1)"
        )

        assertEquals(aez1, result.detectedAez)
        assertEquals("Dinajpur (AEZ 1)", result.locationDescription)
    }
}
