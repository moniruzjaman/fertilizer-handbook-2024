package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cached_plots")
data class CachedPlotEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val cropName: String,
    val centerLat: Double,
    val centerLon: Double,
    val radiusMeters: Double,
    val isPolygon: Boolean,
    val boundaryJson: String,
    val areaDecimals: Double,
    val areaBigha: Double,
    val areaAcres: Double,
    val areaSquareMeters: Double,
    val perimeterMeters: Double,
    val village: String,
    val union: String,
    val upazila: String,
    val district: String,
    val division: String,
    val aezNumber: Int,
    val aezName: String,
    val dominantLandTypeBn: String,
    val soilTextureBn: String,
    val createdAt: Long = System.currentTimeMillis()
)
