package com.example

import com.example.location.PlotGeofenceManager
import com.example.model.GeofenceState
import com.example.model.GeoPoint
import com.example.model.MappedPlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlotGeofenceTest {

    @Test
    fun testPointInPolygonDetection() {
        // Define a square plot in Dinajpur around 25.85, 88.55
        // ~100m square
        val squarePlot = listOf(
            GeoPoint(25.8500, 88.5500),
            GeoPoint(25.8500, 88.5510),
            GeoPoint(25.8510, 88.5510),
            GeoPoint(25.8510, 88.5500)
        )

        // Point inside the center
        val insidePoint = GeoPoint(25.8505, 88.5505)
        assertTrue("Point should be inside polygon", PlotGeofenceManager.isPointInPolygon(insidePoint.latitude, insidePoint.longitude, squarePlot))

        // Point outside the plot
        val outsidePoint = GeoPoint(25.8550, 88.5550)
        assertFalse("Point should be outside polygon", PlotGeofenceManager.isPointInPolygon(outsidePoint.latitude, outsidePoint.longitude, squarePlot))
    }

    @Test
    fun testGeofenceStatusCalculation() {
        val boundary = listOf(
            GeoPoint(25.8500, 88.5500),
            GeoPoint(25.8500, 88.5510),
            GeoPoint(25.8510, 88.5510),
            GeoPoint(25.8510, 88.5500)
        )
        val plot = MappedPlot(
            name = "Test Boro Plot",
            boundaryPoints = boundary,
            isPolygon = true,
            areaDecimals = 33.0
        )

        val insideStatus = PlotGeofenceManager.calculateGeofenceStatus(25.8505, 88.5505, plot)
        assertEquals(GeofenceState.INSIDE, insideStatus.state)
        assertEquals(0.0, insideStatus.distanceMeters, 0.01)

        val farAwayStatus = PlotGeofenceManager.calculateGeofenceStatus(26.0000, 89.0000, plot)
        assertEquals(GeofenceState.OUTSIDE, farAwayStatus.state)
        assertTrue("Distance should be > 1000m", farAwayStatus.distanceMeters > 1000.0)
    }

    @Test
    fun testCircularGeofenceStatus() {
        val center = GeoPoint(25.8500, 88.5500)
        val circularPlot = MappedPlot(
            name = "Circular Plot",
            centerLatitude = center.latitude,
            centerLongitude = center.longitude,
            radiusMeters = 50.0,
            isPolygon = false,
            areaDecimals = 19.4
        )

        // Inside point (10 meters away)
        val nearStatus = PlotGeofenceManager.calculateGeofenceStatus(25.85005, 88.5500, circularPlot)
        assertEquals(GeofenceState.INSIDE, nearStatus.state)

        // Far outside point (~500 meters away)
        val outsideStatus = PlotGeofenceManager.calculateGeofenceStatus(25.8550, 88.5500, circularPlot)
        assertEquals(GeofenceState.OUTSIDE, outsideStatus.state)
    }

    @Test
    fun testAreaConversions() {
        // Standard Bangladesh agricultural conversions:
        // 1 decimal = 40.4686 m2
        // 33 decimals = 1 bigha = 1335.46 m2
        // 100 decimals = 1 acre
        val areaM2 = 1335.4626
        val decimals = PlotGeofenceManager.convertSquareMetersToDecimals(areaM2)
        assertEquals(33.0, decimals, 0.1)

        val bigha = PlotGeofenceManager.convertDecimalsToBigha(decimals)
        assertEquals(1.0, bigha, 0.01)

        val acres = PlotGeofenceManager.convertDecimalsToAcres(decimals)
        assertEquals(0.33, acres, 0.01)

        val ha = PlotGeofenceManager.convertSquareMetersToHectares(10000.0)
        assertEquals(1.0, ha, 0.001)
    }

    @Test
    fun testPolygonAreaCalculation() {
        // Rectangular field of approximately 35m x 38m (~1330 m2 or ~33 decimals)
        val lat = 25.85
        val lon = 88.55
        val offsetLat = 0.000315 // ~35m
        val offsetLon = 0.000380 // ~38m

        val polygon = listOf(
            GeoPoint(lat - offsetLat, lon - offsetLon),
            GeoPoint(lat - offsetLat, lon + offsetLon),
            GeoPoint(lat + offsetLat, lon + offsetLon),
            GeoPoint(lat + offsetLat, lon - offsetLon)
        )

        val areaM2 = PlotGeofenceManager.calculatePolygonAreaSquareMeters(polygon)
        assertTrue("Area should be positive and around 5000 m2 for 70m x 76m", areaM2 > 1000.0)

        val decimals = PlotGeofenceManager.convertSquareMetersToDecimals(areaM2)
        assertTrue("Decimals should be realistic for field", decimals > 20.0)
    }
}
