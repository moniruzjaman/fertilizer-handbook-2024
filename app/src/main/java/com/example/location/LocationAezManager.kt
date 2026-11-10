package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.example.data.AezDatabase
import com.example.model.AezInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class LocationAezResult(
    val latitude: Double,
    val longitude: Double,
    val detectedDistrict: String,
    val aezInfo: AezInfo,
    val isRealGps: Boolean
)

class LocationAezManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationAez(): LocationAezResult? = withContext(Dispatchers.IO) {
        try {
            val location = getDeviceLocation()
            if (location != null) {
                val district = resolveDistrictFromCoordinates(location.latitude, location.longitude)
                val matchedAez = if (district.isNotBlank()) {
                    AezDatabase.findByDistrict(district) ?: AezDatabase.findNearestAez(location.latitude, location.longitude)
                } else {
                    AezDatabase.findNearestAez(location.latitude, location.longitude)
                }

                LocationAezResult(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    detectedDistrict = if (district.isNotBlank()) district else matchedAez.primaryDistricts.first(),
                    aezInfo = matchedAez,
                    isRealGps = true
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("LocationAezManager", "Error fetching GPS location: ${e.message}", e)
            null
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getDeviceLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val cts = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    continuation.resume(loc)
                } else {
                    // Fallback to getLastLocation
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { lastLoc -> continuation.resume(lastLoc) }
                        .addOnFailureListener { continuation.resume(null) }
                }
            }
            .addOnFailureListener {
                continuation.resume(null)
            }

        continuation.invokeOnCancellation {
            cts.cancel()
        }
    }

    @Suppress("DEPRECATION")
    private fun resolveDistrictFromCoordinates(lat: Double, lon: Double): String {
        return try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.ENGLISH)
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                val address = addresses?.firstOrNull()
                val district = address?.subAdminArea ?: address?.locality ?: address?.adminArea ?: ""
                district.replace("District", "").trim()
            } else ""
        } catch (e: Exception) {
            Log.w("LocationAezManager", "Geocoder reverse lookup failed: ${e.message}")
            ""
        }
    }
}
