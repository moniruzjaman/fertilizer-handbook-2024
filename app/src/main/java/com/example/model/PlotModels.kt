package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class GeoPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class ReverseGeoAddress(
    val village: String = "",
    val union: String = "",
    val upazila: String = "",
    val district: String = "",
    val division: String = "",
    val postCode: String = "",
    val fullAddressBn: String = "",
    val fullAddressEn: String = ""
) {
    fun shortAddressBn(): String {
        val parts = listOf(village, union, upazila, district).filter { it.isNotBlank() }
        return if (parts.isNotEmpty()) parts.joinToString(", ") else "অজ্ঞাত এলাকা"
    }

    fun shortAddressEn(): String {
        val parts = listOf(village, upazila, district).filter { it.isNotBlank() }
        return if (parts.isNotEmpty()) parts.joinToString(", ") else "Unknown Location"
    }
}

enum class GeofenceState {
    INSIDE,
    NEARBY,
    OUTSIDE
}

data class GeofenceStatus(
    val state: GeofenceState,
    val distanceMeters: Double,
    val messageBn: String,
    val messageEn: String
)

@IgnoreExtraProperties
data class MappedPlot(
    @DocumentId val id: String = UUID.randomUUID().toString(),
    val name: String = "আমার কৃষি প্লট",
    val cropName: String = "বোরো ধান (BRRI dhan28/29)",
    val boundaryPoints: List<GeoPoint> = emptyList(),
    val centerLatitude: Double = 0.0,
    val centerLongitude: Double = 0.0,
    val radiusMeters: Double = 0.0,
    val isPolygon: Boolean = true,
    val areaSquareMeters: Double = 0.0,
    val areaDecimals: Double = 0.0,
    val areaBigha: Double = 0.0,
    val areaAcres: Double = 0.0,
    val areaHectares: Double = 0.0,
    val perimeterMeters: Double = 0.0,
    val village: String = "",
    val union: String = "",
    val upazila: String = "",
    val district: String = "",
    val division: String = "",
    val postCode: String = "",
    val fullAddressBn: String = "",
    val aezNumber: Int = 1,
    val aezName: String = "Old Himalayan Piedmont Plain",
    val aezBengaliName: String = "পুরাতন হিমালয় পাদদেশীয় সমভূমি",
    val dominantLandTypeBn: String = "মাঝারি উঁচু জমি",
    val soilTextureBn: String = "পলি দোআঁশ",
    val createdAtEpoch: Long = System.currentTimeMillis()
)
