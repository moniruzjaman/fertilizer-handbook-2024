package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.R
import com.example.model.GeoPoint
import com.example.model.MappedPlot
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PlotRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("frg_mapped_plots_prefs", Context.MODE_PRIVATE)
    private val TAG = "PlotRepository"

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance(context.getString(R.string.firestore_database_id))
        } catch (e: Exception) {
            null
        }
    }

    suspend fun savePlot(plot: MappedPlot): Unit = withContext(Dispatchers.IO) {
        // 1. Save locally in SharedPreferences
        val existing = loadPlotsInternal().toMutableList()
        val index = existing.indexOfFirst { it.id == plot.id }
        if (index >= 0) {
            existing[index] = plot
        } else {
            existing.add(0, plot)
        }
        savePlotsInternal(existing)

        // 2. Backup to Firestore if user is authenticated
        val user = Firebase.auth.currentUser
        val firestore = getFirestore()
        if (user != null && firestore != null) {
            try {
                val boundaryArray = plot.boundaryPoints.map {
                    mapOf("latitude" to it.latitude, "longitude" to it.longitude)
                }
                val payload = hashMapOf<String, Any>(
                    "id" to plot.id,
                    "name" to plot.name,
                    "cropName" to plot.cropName,
                    "centerLatitude" to plot.centerLatitude,
                    "centerLongitude" to plot.centerLongitude,
                    "radiusMeters" to plot.radiusMeters,
                    "isPolygon" to plot.isPolygon,
                    "areaSquareMeters" to plot.areaSquareMeters,
                    "areaDecimals" to plot.areaDecimals,
                    "areaBigha" to plot.areaBigha,
                    "areaAcres" to plot.areaAcres,
                    "areaHectares" to plot.areaHectares,
                    "perimeterMeters" to plot.perimeterMeters,
                    "village" to plot.village,
                    "union" to plot.union,
                    "upazila" to plot.upazila,
                    "district" to plot.district,
                    "division" to plot.division,
                    "fullAddressBn" to plot.fullAddressBn,
                    "aezNumber" to plot.aezNumber,
                    "aezName" to plot.aezName,
                    "aezBengaliName" to plot.aezBengaliName,
                    "dominantLandTypeBn" to plot.dominantLandTypeBn,
                    "soilTextureBn" to plot.soilTextureBn,
                    "createdAtEpoch" to plot.createdAtEpoch,
                    "boundaryPoints" to boundaryArray
                )
                firestore.collection("users")
                    .document(user.uid)
                    .collection("plots")
                    .document(plot.id)
                    .set(payload)
                    .await()
                Log.d(TAG, "Plot ${plot.name} saved to Firestore successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Could not sync plot to Firestore: ${e.message}")
            }
        }
    }

    suspend fun getSavedPlots(): List<MappedPlot> = withContext(Dispatchers.IO) {
        loadPlotsInternal()
    }

    suspend fun deletePlot(plotId: String): Unit = withContext(Dispatchers.IO) {
        val existing = loadPlotsInternal().toMutableList()
        existing.removeAll { it.id == plotId }
        savePlotsInternal(existing)

        val user = Firebase.auth.currentUser
        val firestore = getFirestore()
        if (user != null && firestore != null) {
            try {
                firestore.collection("users")
                    .document(user.uid)
                    .collection("plots")
                    .document(plotId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.w(TAG, "Could not delete plot from Firestore: ${e.message}")
            }
        }
    }

    private fun loadPlotsInternal(): List<MappedPlot> {
        val rawJson = prefs.getString("saved_plots_json", null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(rawJson)
            val list = mutableListOf<MappedPlot>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val pointsArray = obj.optJSONArray("boundaryPoints")
                val pointsList = mutableListOf<GeoPoint>()
                if (pointsArray != null) {
                    for (j in 0 until pointsArray.length()) {
                        val pt = pointsArray.getJSONObject(j)
                        pointsList.add(GeoPoint(pt.getDouble("lat"), pt.getDouble("lon")))
                    }
                }
                list.add(
                    MappedPlot(
                        id = obj.getString("id"),
                        name = obj.optString("name", "আমার প্লট"),
                        cropName = obj.optString("cropName", "বোরো ধান"),
                        boundaryPoints = pointsList,
                        centerLatitude = obj.optDouble("centerLat", 0.0),
                        centerLongitude = obj.optDouble("centerLon", 0.0),
                        radiusMeters = obj.optDouble("radiusMeters", 0.0),
                        isPolygon = obj.optBoolean("isPolygon", true),
                        areaSquareMeters = obj.optDouble("areaM2", 0.0),
                        areaDecimals = obj.optDouble("areaDecimals", 0.0),
                        areaBigha = obj.optDouble("areaBigha", 0.0),
                        areaAcres = obj.optDouble("areaAcres", 0.0),
                        areaHectares = obj.optDouble("areaHa", 0.0),
                        perimeterMeters = obj.optDouble("perimeterMeters", 0.0),
                        village = obj.optString("village", ""),
                        union = obj.optString("union", ""),
                        upazila = obj.optString("upazila", ""),
                        district = obj.optString("district", ""),
                        division = obj.optString("division", ""),
                        postCode = obj.optString("postCode", ""),
                        fullAddressBn = obj.optString("fullAddressBn", ""),
                        aezNumber = obj.optInt("aezNumber", 1),
                        aezName = obj.optString("aezName", ""),
                        aezBengaliName = obj.optString("aezBengaliName", ""),
                        dominantLandTypeBn = obj.optString("dominantLandTypeBn", "মাঝারি উঁচু জমি"),
                        soilTextureBn = obj.optString("soilTextureBn", "পলি দোআঁশ"),
                        createdAtEpoch = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "Failed parsing saved plots: ${e.message}")
            emptyList()
        }
    }

    private fun savePlotsInternal(plots: List<MappedPlot>) {
        try {
            val jsonArray = JSONArray()
            for (p in plots) {
                val obj = JSONObject()
                obj.put("id", p.id)
                obj.put("name", p.name)
                obj.put("cropName", p.cropName)
                obj.put("centerLat", p.centerLatitude)
                obj.put("centerLon", p.centerLongitude)
                obj.put("radiusMeters", p.radiusMeters)
                obj.put("isPolygon", p.isPolygon)
                obj.put("areaM2", p.areaSquareMeters)
                obj.put("areaDecimals", p.areaDecimals)
                obj.put("areaBigha", p.areaBigha)
                obj.put("areaAcres", p.areaAcres)
                obj.put("areaHa", p.areaHectares)
                obj.put("perimeterMeters", p.perimeterMeters)
                obj.put("village", p.village)
                obj.put("union", p.union)
                obj.put("upazila", p.upazila)
                obj.put("district", p.district)
                obj.put("division", p.division)
                obj.put("postCode", p.postCode)
                obj.put("fullAddressBn", p.fullAddressBn)
                obj.put("aezNumber", p.aezNumber)
                obj.put("aezName", p.aezName)
                obj.put("aezBengaliName", p.aezBengaliName)
                obj.put("dominantLandTypeBn", p.dominantLandTypeBn)
                obj.put("soilTextureBn", p.soilTextureBn)
                obj.put("createdAt", p.createdAtEpoch)

                val pointsArr = JSONArray()
                for (pt in p.boundaryPoints) {
                    val ptObj = JSONObject()
                    ptObj.put("lat", pt.latitude)
                    ptObj.put("lon", pt.longitude)
                    pointsArr.put(ptObj)
                }
                obj.put("boundaryPoints", pointsArr)
                jsonArray.put(obj)
            }
            prefs.edit().putString("saved_plots_json", jsonArray.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed saving plots to prefs: ${e.message}")
        }
    }
}
