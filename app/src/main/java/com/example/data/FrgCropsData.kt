package com.example.data

import com.example.model.BaseNutrientRequirement
import com.example.model.Crop
import com.example.model.CropCategory
import com.example.model.NutrientDeficiency
import com.example.model.SplitDose

object FrgCropsData {

    val crops: List<Crop> = listOf(
        Crop(
            id = "boro_hyv",
            name = "Boro Rice (HYV)",
            bengaliName = "বোরো ধান (উফশী)",
            category = CropCategory.CEREALS,
            targetYieldText = "6.5 - 7.5 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 140f,
                pKgPerHa = 20f,
                kKgPerHa = 65f,
                sKgPerHa = 15f,
                znKgPerHa = 2.0f,
                bKgPerHa = 0.5f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal (Final Land Preparation)",
                    bengaliStageName = "জমি তৈরির শেষ চাষে (মূল সার)",
                    timingDescription = "Apply at final land preparation before transplanting",
                    ureaPercent = 0f,
                    tspPercent = 100f,
                    mopPercent = 33.3f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "1st Top Dressing",
                    bengaliStageName = "১ম কিস্তি উপরিপ্রয়োগ",
                    timingDescription = "15-20 days after transplanting (early tillering)",
                    ureaPercent = 33.3f,
                    tspPercent = 0f,
                    mopPercent = 0f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                ),
                SplitDose(
                    stageName = "2nd Top Dressing",
                    bengaliStageName = "২য় কিস্তি উপরিপ্রয়োগ",
                    timingDescription = "30-35 days after transplanting (maximum tillering)",
                    ureaPercent = 33.3f,
                    tspPercent = 0f,
                    mopPercent = 0f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                ),
                SplitDose(
                    stageName = "3rd Top Dressing",
                    bengaliStageName = "৩য় কিস্তি উপরিপ্রয়োগ",
                    timingDescription = "5-7 days before panicle initiation (PI stage)",
                    ureaPercent = 33.4f,
                    tspPercent = 0f,
                    mopPercent = 66.7f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "Do not apply urea in flooded standing water. Drain water slightly before broadcasting and incorporate into mud. MoP split in 3rd stage significantly improves grain filling."
        ),

        Crop(
            id = "boro_hybrid",
            name = "Boro Rice (Hybrid)",
            bengaliName = "বোরো ধান (হাইব্রিড)",
            category = CropCategory.CEREALS,
            targetYieldText = "8.5 - 9.5 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 175f,
                pKgPerHa = 26f,
                kKgPerHa = 85f,
                sKgPerHa = 18f,
                znKgPerHa = 2.5f,
                bKgPerHa = 1.0f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application",
                    bengaliStageName = "জমি তৈরির শেষ চাষে (মূল সার)",
                    timingDescription = "Apply at final ploughing before planting",
                    ureaPercent = 0f,
                    tspPercent = 100f,
                    mopPercent = 33.3f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "1st Top Dressing",
                    bengaliStageName = "১ম কিস্তি উপরিপ্রয়োগ",
                    timingDescription = "15-20 DAT (active tillering)",
                    ureaPercent = 33.3f,
                    tspPercent = 0f,
                    mopPercent = 0f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                ),
                SplitDose(
                    stageName = "2nd Top Dressing",
                    bengaliStageName = "২য় কিস্তি উপরিপ্রয়োগ",
                    timingDescription = "30-35 DAT (peak vegetative stage)",
                    ureaPercent = 33.3f,
                    tspPercent = 0f,
                    mopPercent = 0f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                ),
                SplitDose(
                    stageName = "3rd Top Dressing",
                    bengaliStageName = "৩য় কিস্তি উপরিপ্রয়োগ",
                    timingDescription = "5-7 days before panicle emergence",
                    ureaPercent = 33.4f,
                    tspPercent = 0f,
                    mopPercent = 66.7f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "High nutrient feeder. Balance potassium to avoid lodging. Ensure zinc is applied if soil is continuously submerged."
        ),

        Crop(
            id = "aman_hyv",
            name = "T. Aman Rice (HYV)",
            bengaliName = "রোপা আমন ধান (উফশী)",
            category = CropCategory.CEREALS,
            targetYieldText = "4.5 - 5.5 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 85f,
                pKgPerHa = 14f,
                kKgPerHa = 45f,
                sKgPerHa = 10f,
                znKgPerHa = 1.5f,
                bKgPerHa = 0f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application",
                    bengaliStageName = "মূল সার (জমি তৈরিতে)",
                    timingDescription = "Apply during final land prep",
                    ureaPercent = 33.3f,
                    tspPercent = 100f,
                    mopPercent = 100f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "1st Top Dressing",
                    bengaliStageName = "১ম কিস্তি উপরিপ্রয়োগ",
                    timingDescription = "20-25 days after transplanting",
                    ureaPercent = 33.3f,
                    tspPercent = 0f,
                    mopPercent = 0f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                ),
                SplitDose(
                    stageName = "2nd Top Dressing",
                    bengaliStageName = "২য় কিস্তি উপরিপ্রয়োগ",
                    timingDescription = "5-7 days before panicle initiation",
                    ureaPercent = 33.4f,
                    tspPercent = 0f,
                    mopPercent = 0f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "Apply 1/3 Urea at basal for rainfed Aman to stimulate root establishment."
        ),

        Crop(
            id = "wheat",
            name = "Wheat",
            bengaliName = "গম",
            category = CropCategory.CEREALS,
            targetYieldText = "4.0 - 5.0 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 100f,
                pKgPerHa = 26f,
                kKgPerHa = 50f,
                sKgPerHa = 15f,
                znKgPerHa = 2.0f,
                bKgPerHa = 1.0f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application",
                    bengaliStageName = "জমি তৈরিতে (মূল সার)",
                    timingDescription = "Apply 2/3 Urea and all other fertilizers at final ploughing",
                    ureaPercent = 66.7f,
                    tspPercent = 100f,
                    mopPercent = 100f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "Top Dressing (CRI Stage)",
                    bengaliStageName = "উপরিপ্রয়োগ (প্রথম সেচে)",
                    timingDescription = "17-21 days after sowing during first irrigation (Crown Root Initiation)",
                    ureaPercent = 33.3f,
                    tspPercent = 0f,
                    mopPercent = 0f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "Crown Root Initiation (CRI) at 17-21 DAS is the most critical stage for wheat top dressing with irrigation. Boron prevents grain sterility (floret sterility)."
        ),

        Crop(
            id = "maize_hybrid",
            name = "Maize / Corn (Hybrid)",
            bengaliName = "ভুট্টা (হাইব্রিড)",
            category = CropCategory.CEREALS,
            targetYieldText = "9.5 - 11.5 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 210f,
                pKgPerHa = 42f,
                kKgPerHa = 95f,
                sKgPerHa = 25f,
                znKgPerHa = 3.5f,
                bKgPerHa = 1.5f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application",
                    bengaliStageName = "মূল সার (জমি তৈরিতে)",
                    timingDescription = "1/3 Urea, 1/3 MoP, and all TSP, Gypsum, Zinc, Boron",
                    ureaPercent = 33.3f,
                    tspPercent = 100f,
                    mopPercent = 33.3f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "1st Top Dressing (Knee-high)",
                    bengaliStageName = "১ম উপরিপ্রয়োগ (হাঁটু সমান)",
                    timingDescription = "30-35 days after sowing (knee-high stage) followed by earthing up",
                    ureaPercent = 33.3f,
                    tspPercent = 0f,
                    mopPercent = 33.3f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                ),
                SplitDose(
                    stageName = "2nd Top Dressing (Tasseling)",
                    bengaliStageName = "২য় উপরিপ্রয়োগ (মোচা আসার পূর্বে)",
                    timingDescription = "55-60 days after sowing before male flowering (tasseling)",
                    ureaPercent = 33.4f,
                    tspPercent = 0f,
                    mopPercent = 33.4f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "Maize is an exhaustive feeder. Apply side-band placement 5-8 cm away from plant rows followed by soil covering."
        ),

        Crop(
            id = "potato",
            name = "Potato",
            bengaliName = "আলু",
            category = CropCategory.VEGETABLES,
            targetYieldText = "30.0 - 35.0 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 135f,
                pKgPerHa = 32f,
                kKgPerHa = 115f,
                sKgPerHa = 16f,
                znKgPerHa = 2.5f,
                bKgPerHa = 1.2f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application",
                    bengaliStageName = "জমি তৈরিতে (মূল সার)",
                    timingDescription = "Furrow application before placing seed tubers",
                    ureaPercent = 50f,
                    tspPercent = 100f,
                    mopPercent = 50f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "Top Dressing (Earthing Up)",
                    bengaliStageName = "উপরিপ্রয়োগ (গাছে মাটি তোলার সময়)",
                    timingDescription = "30-35 days after planting during 1st earthing up",
                    ureaPercent = 50f,
                    tspPercent = 0f,
                    mopPercent = 50f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "Potash is vital for starch translocation and tuber size. Boron prevents hollow heart disease in tubers."
        ),

        Crop(
            id = "lentil",
            name = "Lentil (Masur)",
            bengaliName = "মসুর ডাল",
            category = CropCategory.PULSES,
            targetYieldText = "1.6 - 2.0 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 20f,
                pKgPerHa = 18f,
                kKgPerHa = 20f,
                sKgPerHa = 10f,
                znKgPerHa = 1.0f,
                bKgPerHa = 1.0f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application Only",
                    bengaliStageName = "এককালীন জমি তৈরির শেষ চাষে",
                    timingDescription = "Apply all fertilizers together at final ploughing",
                    ureaPercent = 100f,
                    tspPercent = 100f,
                    mopPercent = 100f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                )
            ),
            agronomicNotes = "Rhizobium root nodules fix atmospheric nitrogen. Starter N promotes early seedling vigor. Inoculate with biofertilizer if available."
        ),

        Crop(
            id = "mustard",
            name = "Mustard (Sarisha)",
            bengaliName = "সরিষা",
            category = CropCategory.OILSEEDS,
            targetYieldText = "1.8 - 2.2 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 90f,
                pKgPerHa = 24f,
                kKgPerHa = 40f,
                sKgPerHa = 22f,
                znKgPerHa = 2.0f,
                bKgPerHa = 1.0f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application",
                    bengaliStageName = "মূল সার (জমি তৈরিতে)",
                    timingDescription = "Apply 1/2 Urea and all other fertilizers at final land prep",
                    ureaPercent = 50f,
                    tspPercent = 100f,
                    mopPercent = 100f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "Top Dressing (Flower Emergence)",
                    bengaliStageName = "উপরিপ্রয়োগ (ফুল আসার প্রাক্কালে)",
                    timingDescription = "25-30 days after sowing before flowering with light irrigation",
                    ureaPercent = 50f,
                    tspPercent = 0f,
                    mopPercent = 0f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "Sulfur (Gypsum) is critical for oil synthesis and protein quality in mustard. Boron ensures full pod podding."
        ),

        Crop(
            id = "tomato",
            name = "Tomato",
            bengaliName = "টমেটো",
            category = CropCategory.VEGETABLES,
            targetYieldText = "65 - 75 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 140f,
                pKgPerHa = 36f,
                kKgPerHa = 90f,
                sKgPerHa = 18f,
                znKgPerHa = 2.5f,
                bKgPerHa = 1.5f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application in Pits/Rows",
                    bengaliStageName = "মাদায়/জমি তৈরিতে",
                    timingDescription = "Apply 1/3 Urea, 1/3 MoP and all TSP, Gypsum, Zinc, Boron",
                    ureaPercent = 33.3f,
                    tspPercent = 100f,
                    mopPercent = 33.3f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "1st Top Dressing",
                    bengaliStageName = "১ম উপরিপ্রয়োগ (২০ দিন পর)",
                    timingDescription = "20 days after transplanting (early vegetative growth)",
                    ureaPercent = 33.3f,
                    tspPercent = 0f,
                    mopPercent = 33.3f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                ),
                SplitDose(
                    stageName = "2nd Top Dressing",
                    bengaliStageName = "২য় উপরিপ্রয়োগ (৪০ দিন পর)",
                    timingDescription = "40 days after transplanting (first flowering/fruiting)",
                    ureaPercent = 33.4f,
                    tspPercent = 0f,
                    mopPercent = 33.4f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "Boron and Calcium prevent Blossom End Rot and fruit cracking in tomato."
        ),

        Crop(
            id = "onion",
            name = "Onion",
            bengaliName = "পেঁয়াজ",
            category = CropCategory.SPICES,
            targetYieldText = "16 - 20 t/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 110f,
                pKgPerHa = 35f,
                kKgPerHa = 80f,
                sKgPerHa = 20f,
                znKgPerHa = 2.0f,
                bKgPerHa = 1.0f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application",
                    bengaliStageName = "জমি তৈরিতে",
                    timingDescription = "Apply 1/3 Urea, 1/2 MoP, and all TSP, Gypsum, Zinc, Boron",
                    ureaPercent = 33.3f,
                    tspPercent = 100f,
                    mopPercent = 50f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "1st Top Dressing",
                    bengaliStageName = "১ম উপরিপ্রয়োগ (২৫ দিন পর)",
                    timingDescription = "25 days after planting",
                    ureaPercent = 33.3f,
                    tspPercent = 0f,
                    mopPercent = 25f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                ),
                SplitDose(
                    stageName = "2nd Top Dressing",
                    bengaliStageName = "২য় উপরিপ্রয়োগ (৫০ দিন পর)",
                    timingDescription = "50 days after planting before bulb swelling",
                    ureaPercent = 33.4f,
                    tspPercent = 0f,
                    mopPercent = 25f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "Sulfur gives onions their pungent aroma and prolongs storage shelf life."
        ),

        Crop(
            id = "jute",
            name = "Jute (Pat)",
            bengaliName = "তোষা পাট / দেশী পাট",
            category = CropCategory.CASH_CROPS,
            targetYieldText = "3.5 - 4.2 t fiber/ha",
            baseNutrients = BaseNutrientRequirement(
                nKgPerHa = 80f,
                pKgPerHa = 12f,
                kKgPerHa = 45f,
                sKgPerHa = 12f,
                znKgPerHa = 1.5f,
                bKgPerHa = 0f
            ),
            splitSchedule = listOf(
                SplitDose(
                    stageName = "Basal Application",
                    bengaliStageName = "জমি তৈরিতে (মূল সার)",
                    timingDescription = "1/2 Urea and all TSP, MoP, Gypsum, Zinc at final ploughing",
                    ureaPercent = 50f,
                    tspPercent = 100f,
                    mopPercent = 100f,
                    gypsumPercent = 100f,
                    zincPercent = 100f,
                    boronPercent = 100f
                ),
                SplitDose(
                    stageName = "Top Dressing (Weeding Stage)",
                    bengaliStageName = "উপরিপ্রয়োগ (নিড়ানির পর)",
                    timingDescription = "35-40 days after emergence after thinning and weeding",
                    ureaPercent = 50f,
                    tspPercent = 0f,
                    mopPercent = 0f,
                    gypsumPercent = 0f,
                    zincPercent = 0f,
                    boronPercent = 0f
                )
            ),
            agronomicNotes = "Never broadcast urea on wet jute leaves in the morning, which causes leaf scorching."
        )
    )

    val deficiencies: List<NutrientDeficiency> = listOf(
        NutrientDeficiency(
            nutrient = "Nitrogen",
            symbol = "N",
            bengaliName = "নাইট্রোজেন",
            primaryFunction = "Chlorophyll synthesis, rapid vegetative growth, and leaf elongation.",
            deficiencySymptoms = "Older leaves turn pale yellow (chlorosis) starting from the leaf tips along the midrib in a 'V' pattern. Stunted plant growth and reduced tillering.",
            correctiveAction = "Top dress with Urea (or 1% Urea foliar spray in emergencies). Ensure soil has adequate moisture."
        ),
        NutrientDeficiency(
            nutrient = "Phosphorus",
            symbol = "P",
            bengaliName = "ফসফরাস",
            primaryFunction = "Root development, early seedling establishment, and energy transfer (ATP).",
            deficiencySymptoms = "Leaves turn dark green with distinct purple, reddish, or bronze coloration on older leaves and stems. Delayed flowering and weak root system.",
            correctiveAction = "Apply TSP or DAP at basal stage. Phosphorus is immobile in soil; broadcast and mix with root zone soil."
        ),
        NutrientDeficiency(
            nutrient = "Potassium",
            symbol = "K",
            bengaliName = "পটাশিয়াম",
            primaryFunction = "Stomatal regulation, enzyme activation, disease resistance, and grain filling.",
            deficiencySymptoms = "Marginal scorching and necrosis (browning) on tips and edges of older leaves ('firing' appearance). Weak stems and susceptibility to lodging.",
            correctiveAction = "Apply MoP in split doses, especially at early tillering and panicle initiation."
        ),
        NutrientDeficiency(
            nutrient = "Sulfur",
            symbol = "S",
            bengaliName = "সালফার / গন্ধক",
            primaryFunction = "Essential constituent of sulfur amino acids (methionine, cysteine), chlorophyll, and oil content.",
            deficiencySymptoms = "Unlike Nitrogen, Sulfur is immobile in plants, so younger (upper) leaves turn pale yellow first while older leaves remain green.",
            correctiveAction = "Apply Gypsum (18% S) or Ammonium Sulfate during land preparation."
        ),
        NutrientDeficiency(
            nutrient = "Zinc",
            symbol = "Zn",
            bengaliName = "জিংক / দস্তা",
            primaryFunction = "Enzymatic function, auxin synthesis, and cell elongation.",
            deficiencySymptoms = "Khaira disease in rice: brownish/rusty blotches appear on lower leaves 2-3 weeks after transplanting. Interveinal chlorosis and shortened internodes.",
            correctiveAction = "Apply Zinc Sulphate (21% or 36%) at basal prep, or spray 0.5% Zinc Sulphate + 0.25% lime solution on foliage."
        ),
        NutrientDeficiency(
            nutrient = "Boron",
            symbol = "B",
            bengaliName = "বোরন",
            primaryFunction = "Pollen tube elongation, fertilization, grain setting, and sugar translocation.",
            deficiencySymptoms = "Floret sterility / chaffy grains in wheat and mustard; hollow heart in potato; cracked or deformed tomato fruits; brittle young leaves.",
            correctiveAction = "Apply Boric Acid (17% B) or Solubor (20% B) at land preparation, or spray 0.2% Solubor at flower bud initiation."
        )
    )
}
