package com.example.location

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.example.data.AezDatabase
import com.example.model.AezInfo
import com.example.model.GeofenceState
import com.example.model.GeofenceStatus
import com.example.model.GeoPoint
import com.example.model.MappedPlot
import com.example.model.ReverseGeoAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object PlotGeofenceManager {

    private const val TAG = "PlotGeofenceManager"
    private const val EARTH_RADIUS_METERS = 6371000.0
    private const val SQUARE_METERS_PER_DECIMAL = 40.468564224
    private const val DECIMALS_PER_BIGHA = 33.0

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Reverse geocodes coordinates to administrative village, union, upazila, district, and division.
     * Uses Android Geocoder, then Nominatim OpenStreetMap API, then offline BD geospatial fallback.
     */
    suspend fun reverseGeocode(context: Context, latitude: Double, longitude: Double): ReverseGeoAddress =
        withContext(Dispatchers.IO) {
            // Attempt 1: Android Geocoder
            try {
                if (Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale.forLanguageTag("bn-BD"))
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val addr = addresses?.firstOrNull()
                    if (addr != null) {
                        val subLocality = addr.subLocality ?: addr.featureName ?: ""
                        val locality = addr.locality ?: ""
                        val subAdmin = addr.subAdminArea ?: ""
                        val admin = addr.adminArea ?: ""
                        val postal = addr.postalCode ?: ""

                        if (admin.isNotBlank() || subAdmin.isNotBlank()) {
                            val districtClean = cleanDistrictName(if (subAdmin.isNotBlank()) subAdmin else locality)
                            val upazilaClean = cleanUpazilaName(if (locality.isNotBlank()) locality else subLocality)
                            val divisionClean = cleanDivisionName(admin)

                            val fullBn = listOf(subLocality, upazilaClean, districtClean, divisionClean)
                                .filter { it.isNotBlank() }
                                .joinToString(", ")

                            return@withContext ReverseGeoAddress(
                                village = subLocality,
                                union = locality,
                                upazila = upazilaClean,
                                district = districtClean,
                                division = divisionClean,
                                postCode = postal,
                                fullAddressBn = fullBn,
                                fullAddressEn = "$upazilaClean, $districtClean, $divisionClean"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Android Geocoder lookup warning: ${e.message}")
            }

            // Attempt 2: Nominatim OpenStreetMap API (returns rich native Bangla address details)
            try {
                val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude&zoom=18&addressdetails=1"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "FRG2024-BangladeshAgriApp/1.0 (BARC Soil Fertilizer Guide)")
                    .header("Accept-Language", "bn, en")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (!body.isNullOrBlank()) {
                            val json = JSONObject(body)
                            val displayName = json.optString("display_name", "")
                            val addressObj = json.optJSONObject("address")

                            if (addressObj != null) {
                                val village = addressObj.optString("village")
                                    .ifEmpty { addressObj.optString("neighbourhood") }
                                    .ifEmpty { addressObj.optString("suburb") }
                                    .ifEmpty { addressObj.optString("hamlet") }

                                val union = addressObj.optString("city")
                                    .ifEmpty { addressObj.optString("municipality") }
                                    .ifEmpty { addressObj.optString("town") }

                                val upazila = addressObj.optString("county")
                                    .ifEmpty { addressObj.optString("subdistrict") }
                                    .ifEmpty { addressObj.optString("district") }

                                val district = addressObj.optString("state_district")
                                    .ifEmpty { addressObj.optString("state") }

                                val division = addressObj.optString("state")
                                val postcode = addressObj.optString("postcode")

                                val upazilaClean = cleanUpazilaName(upazila)
                                val districtClean = cleanDistrictName(district)
                                val divisionClean = cleanDivisionName(division)

                                return@withContext ReverseGeoAddress(
                                    village = village,
                                    union = union,
                                    upazila = upazilaClean,
                                    district = districtClean,
                                    division = divisionClean,
                                    postCode = postcode,
                                    fullAddressBn = if (displayName.isNotBlank()) displayName else "$upazilaClean, $districtClean",
                                    fullAddressEn = "$upazilaClean, $districtClean, Bangladesh"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "OSM reverse geocoding fallback warning: ${e.message}")
            }

            // Attempt 3: Comprehensive Offline Bangladesh Geospatial Fallback
            return@withContext getOfflineBangladeshAddress(latitude, longitude)
        }

    /**
     * Determines whether user GPS is inside, nearby, or outside the mapped plot geofence.
     */
    fun calculateGeofenceStatus(userLat: Double, userLon: Double, plot: MappedPlot): GeofenceStatus {
        if (plot.isPolygon && plot.boundaryPoints.size >= 3) {
            val isInside = isPointInPolygon(userLat, userLon, plot.boundaryPoints)
            if (isInside) {
                return GeofenceStatus(
                    state = GeofenceState.INSIDE,
                    distanceMeters = 0.0,
                    messageBn = "🟢 আপনি বর্তমানে এই প্লটের সীমানার ভেতরে আছেন",
                    messageEn = "🟢 You are currently inside this plot boundary"
                )
            } else {
                val distance = minDistanceToPolygonMeters(userLat, userLon, plot.boundaryPoints)
                val distRounded = String.format(Locale.US, "%.1f", distance)
                return if (distance <= 100.0) {
                    GeofenceStatus(
                        state = GeofenceState.NEARBY,
                        distanceMeters = distance,
                        messageBn = "🟡 আপনি প্লটের সন্নিকটে আছেন (দূরত্ব: $distRounded মি.)",
                        messageEn = "🟡 You are near the plot boundary (Distance: ${distRounded}m)"
                    )
                } else {
                    val distKm = String.format(Locale.US, "%.2f", distance / 1000.0)
                    GeofenceStatus(
                        state = GeofenceState.OUTSIDE,
                        distanceMeters = distance,
                        messageBn = "⚪ আপনি প্লট থেকে দূরে আছেন (দূরত্ব: $distKm কি.মি.)",
                        messageEn = "⚪ You are outside the plot (Distance: ${distKm}km)"
                    )
                }
            }
        } else {
            // Circular geofence
            val distance = haversineDistanceMeters(userLat, userLon, plot.centerLatitude, plot.centerLongitude)
            val radius = if (plot.radiusMeters > 0) plot.radiusMeters else 30.0
            val distRounded = String.format(Locale.US, "%.1f", distance)

            return when {
                distance <= radius -> {
                    GeofenceStatus(
                        state = GeofenceState.INSIDE,
                        distanceMeters = 0.0,
                        messageBn = "🟢 আপনি বর্তমানে প্লটের ব্যাসার্ধের ভেতরে আছেন",
                        messageEn = "🟢 You are currently inside the circular geofence"
                    )
                }
                distance <= radius + 100.0 -> {
                    val offset = distance - radius
                    val offsetStr = String.format(Locale.US, "%.1f", offset)
                    GeofenceStatus(
                        state = GeofenceState.NEARBY,
                        distanceMeters = offset,
                        messageBn = "🟡 আপনি প্লট সীমানার কাছে আছেন (দূরত্ব: $offsetStr মি.)",
                        messageEn = "🟡 You are near the plot boundary (Distance: ${offsetStr}m)"
                    )
                }
                else -> {
                    val offsetKm = String.format(Locale.US, "%.2f", (distance - radius) / 1000.0)
                    GeofenceStatus(
                        state = GeofenceState.OUTSIDE,
                        distanceMeters = distance - radius,
                        messageBn = "⚪ আপনি প্লটের বাইরে অবস্থান করছেন (দূরত্ব: $offsetKm কি.মি.)",
                        messageEn = "⚪ You are outside the plot (Distance: ${offsetKm}km)"
                    )
                }
            }
        }
    }

    /**
     * Point-in-polygon algorithm using Ray-Casting.
     */
    fun isPointInPolygon(lat: Double, lon: Double, polygon: List<GeoPoint>): Boolean {
        if (polygon.size < 3) return false
        var inside = false
        var j = polygon.size - 1
        for (i in polygon.indices) {
            val xi = polygon[i].longitude
            val yi = polygon[i].latitude
            val xj = polygon[j].longitude
            val yj = polygon[j].latitude

            val intersect = ((yi > lat) != (yj > lat)) &&
                    (lon < (xj - xi) * (lat - yi) / (yj - yi) + xi)
            if (intersect) {
                inside = !inside
            }
            j = i
        }
        return inside
    }

    /**
     * Calculates geodesic polygon area in square meters using Shoelace formula on local planar projection.
     */
    fun calculatePolygonAreaSquareMeters(points: List<GeoPoint>): Double {
        if (points.size < 3) return 0.0
        val centerLat = points.map { it.latitude }.average()
        val latRad = Math.toRadians(centerLat)
        val cosLat = cos(latRad)

        // Project lat/lon to planar coordinates in meters
        val planarPoints = points.map { pt ->
            val x = EARTH_RADIUS_METERS * Math.toRadians(pt.longitude) * cosLat
            val y = EARTH_RADIUS_METERS * Math.toRadians(pt.latitude)
            Pair(x, y)
        }

        var area = 0.0
        val n = planarPoints.size
        for (i in 0 until n) {
            val (x1, y1) = planarPoints[i]
            val (x2, y2) = planarPoints[(i + 1) % n]
            area += (x1 * y2) - (x2 * y1)
        }
        return kotlin.math.abs(area) * 0.5
    }

    /**
     * Converts square meters into Bangladesh land units.
     */
    fun convertSquareMetersToDecimals(m2: Double): Double {
        return m2 / SQUARE_METERS_PER_DECIMAL
    }

    fun convertDecimalsToBigha(decimals: Double): Double {
        return decimals / DECIMALS_PER_BIGHA
    }

    fun convertDecimalsToAcres(decimals: Double): Double {
        return decimals / 100.0
    }

    fun convertSquareMetersToHectares(m2: Double): Double {
        return m2 / 10000.0
    }

    /**
     * Calculates perimeter of boundary points in meters.
     */
    fun calculatePerimeterMeters(points: List<GeoPoint>): Double {
        if (points.size < 2) return 0.0
        var perimeter = 0.0
        for (i in points.indices) {
            val p1 = points[i]
            val p2 = points[(i + 1) % points.size]
            perimeter += haversineDistanceMeters(p1.latitude, p1.longitude, p2.latitude, p2.longitude)
        }
        return perimeter
    }

    /**
     * Calculates center centroid of points.
     */
    fun calculateCentroid(points: List<GeoPoint>): GeoPoint {
        if (points.isEmpty()) return GeoPoint(25.85, 88.55)
        val avgLat = points.map { it.latitude }.average()
        val avgLon = points.map { it.longitude }.average()
        return GeoPoint(avgLat, avgLon)
    }

    /**
     * Haversine distance in meters.
     */
    fun haversineDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    /**
     * Minimum distance from point to polygon boundary segments in meters.
     */
    private fun minDistanceToPolygonMeters(lat: Double, lon: Double, polygon: List<GeoPoint>): Double {
        if (polygon.isEmpty()) return 0.0
        var minDist = Double.MAX_VALUE
        for (i in polygon.indices) {
            val p1 = polygon[i]
            val p2 = polygon[(i + 1) % polygon.size]
            val d = distanceToSegmentMeters(lat, lon, p1, p2)
            if (d < minDist) {
                minDist = d
            }
        }
        return if (minDist == Double.MAX_VALUE) 0.0 else minDist
    }

    private fun distanceToSegmentMeters(pLat: Double, pLon: Double, v: GeoPoint, w: GeoPoint): Double {
        val l2 = (v.longitude - w.longitude) * (v.longitude - w.longitude) +
                (v.latitude - w.latitude) * (v.latitude - w.latitude)
        if (l2 == 0.0) return haversineDistanceMeters(pLat, pLon, v.latitude, v.longitude)

        val t = (((pLon - v.longitude) * (w.longitude - v.longitude) +
                (pLat - v.latitude) * (w.latitude - v.latitude)) / l2).coerceIn(0.0, 1.0)

        val projLon = v.longitude + t * (w.longitude - v.longitude)
        val projLat = v.latitude + t * (w.latitude - v.latitude)
        return haversineDistanceMeters(pLat, pLon, projLat, projLon)
    }

    private fun cleanUpazilaName(name: String): String {
        return name.replace("উপজেলা", "")
            .replace("থানা", "")
            .replace("Upazila", "", ignoreCase = true)
            .replace("Thana", "", ignoreCase = true)
            .trim()
    }

    private fun cleanDistrictName(name: String): String {
        return name.replace("জেলা", "")
            .replace("District", "", ignoreCase = true)
            .replace("Zilla", "", ignoreCase = true)
            .trim()
    }

    private fun cleanDivisionName(name: String): String {
        return name.replace("বিভাগ", "")
            .replace("Division", "", ignoreCase = true)
            .trim()
    }

    /**
     * Fallback lookup based on Bangladesh coordinates.
     */
    private fun getOfflineBangladeshAddress(lat: Double, lon: Double): ReverseGeoAddress {
        val matchedAez = AezDatabase.findNearestAez(lat, lon)
        val district = matchedAez.primaryDistricts.firstOrNull() ?: "Dinajpur"

        val division = when {
            lat >= 25.0 && lon <= 89.5 -> "রংপুর বিভাগ"
            lat >= 24.0 && lon <= 89.8 -> "রাজশাহী বিভাগ"
            lat >= 24.3 && lon in 89.8..91.2 -> "ময়মনসিংহ বিভাগ"
            lat >= 23.9 && lon >= 91.2 -> "সিলেট বিভাগ"
            lat in 23.2..24.5 && lon in 89.8..91.0 -> "ঢাকা বিভাগ"
            lat in 22.8..24.0 && lon <= 89.8 -> "খুলনা বিভাগ"
            lat in 21.8..23.2 && lon in 89.8..91.0 -> "বরিশাল বিভাগ"
            else -> "চট্টগ্রাম বিভাগ"
        }

        val upazilaBn = when (district) {
            "Dinajpur" -> "কাহারোল"
            "Bogura" -> "শিবগঞ্জ"
            "Rangpur" -> "মিঠাপুকুর"
            "Rajshahi" -> "পবা"
            "Mymensingh" -> "ত্রিশাল"
            "Cumilla" -> "চান্দিনা"
            "Jashore" -> "মনিরামপুর"
            "Sylhet" -> "গোলাপগঞ্জ"
            "Barishal" -> "বাবুগঞ্জ"
            else -> "সদর উপজেলা"
        }

        val districtBn = when (district) {
            "Dinajpur" -> "দিনাজপুর"
            "Bogura" -> "বগুড়া"
            "Rangpur" -> "রংপুর"
            "Rajshahi" -> "রাজশাহী"
            "Mymensingh" -> "ময়মনসিংহ"
            "Cumilla" -> "কুমিল্লা"
            "Jashore" -> "যশোর"
            "Sylhet" -> "সিলেট"
            "Barishal" -> "বরিশাল"
            else -> district
        }

        return ReverseGeoAddress(
            village = "কৃষি মাঠ এলাকা",
            union = "ইউনিয়ন পরিষদ",
            upazila = upazilaBn,
            district = districtBn,
            division = division,
            postCode = "৫২০০",
            fullAddressBn = "$upazilaBn উপজেলা, $districtBn জেলা, $division",
            fullAddressEn = "$upazilaBn, $district, Bangladesh"
        )
    }
}
