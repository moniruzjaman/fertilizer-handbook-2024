package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "frg_guidelines")
data class FrgGuidelineEntity(
    @PrimaryKey
    val cropId: String,
    val cropName: String,
    val bengaliName: String,
    val category: String,
    val targetYieldText: String,
    val baseN: Float,
    val baseP: Float,
    val baseK: Float,
    val baseS: Float,
    val baseZn: Float,
    val baseB: Float,
    val scheduleDescriptionBn: String,
    val specialAdviceBn: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
