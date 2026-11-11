package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cached_calculations")
data class CachedCalculationEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val cropId: String,
    val cropName: String,
    val cropBengaliName: String,
    val cropCategory: String,
    val landArea: Float,
    val landUnit: String,
    val areaInHectares: Float,
    val soilFertilityClass: String,
    val cowdungTonnesPerHa: Float,
    val useDapInsteadOfTsp: Boolean,
    val aezNumber: Int?,
    val aezName: String?,
    val locationDescription: String?,
    val totalCostTk: Float,
    val totalBags50kg: Float,
    val ureaKg: Float,
    val tspOrDapKg: Float,
    val mopKg: Float,
    val gypsumKg: Float,
    val zincKg: Float,
    val boronKg: Float,
    val timestamp: Long = System.currentTimeMillis()
)
