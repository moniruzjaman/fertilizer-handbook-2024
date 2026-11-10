package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val detectedAez: Int? = null,
    val detectedDistrict: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SavedCalculation(
    @DocumentId val id: String = "",
    val userId: String = "",
    val cropName: String = "",
    val cropBengaliName: String = "",
    val landArea: Double = 0.0,
    val landUnit: String = "",
    val areaInHectares: Double = 0.0,
    val soilFertilityClass: String = "",
    val cowdungTonnesPerHa: Double = 0.0,
    val totalCostTk: Double = 0.0,
    val totalBags50kg: Double = 0.0,
    val aezNumber: Int? = null,
    val aezName: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
