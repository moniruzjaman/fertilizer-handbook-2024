package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.PlotGeofenceManager
import com.example.model.GeofenceState
import com.example.model.GeofenceStatus
import com.example.model.GeoPoint
import com.example.model.MappedPlot
import com.example.ui.theme.AgrigreenDark
import com.example.ui.theme.AgrigreenPrimary
import com.example.util.AppLanguage
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PlotCanvasView(
    plot: MappedPlot,
    userLatitude: Double?,
    userLongitude: Double?,
    geofenceStatus: GeofenceStatus?,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1F8F3))
            .border(1.5.dp, AgrigreenPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .testTag("canvas_plot_geofence_view")
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height

            // 1. Draw subtle agricultural coordinate grid lines
            drawGrid(width, height)

            // 2. Draw North Arrow
            drawNorthIndicator(width, height, language)

            // 3. Render Polygon or Circle Plot
            if (plot.isPolygon && plot.boundaryPoints.size >= 3) {
                drawPolygonPlot(
                    points = plot.boundaryPoints,
                    width = width,
                    height = height,
                    userLat = userLatitude,
                    userLon = userLongitude,
                    pulseRadius = pulseRadius,
                    pulseAlpha = pulseAlpha,
                    isInside = geofenceStatus?.state == GeofenceState.INSIDE,
                    plotAreaDecimals = plot.areaDecimals,
                    language = language
                )
            } else if (!plot.isPolygon && plot.centerLatitude != 0.0) {
                drawCircularPlot(
                    centerLat = plot.centerLatitude,
                    centerLon = plot.centerLongitude,
                    radiusMeters = if (plot.radiusMeters > 0) plot.radiusMeters else 30.0,
                    width = width,
                    height = height,
                    userLat = userLatitude,
                    userLon = userLongitude,
                    pulseRadius = pulseRadius,
                    pulseAlpha = pulseAlpha,
                    isInside = geofenceStatus?.state == GeofenceState.INSIDE,
                    plotAreaDecimals = plot.areaDecimals,
                    language = language
                )
            } else if (plot.boundaryPoints.isNotEmpty()) {
                // Incomplete polygon (1 or 2 points)
                drawIncompletePoints(plot.boundaryPoints, width, height, userLatitude, userLongitude)
            } else {
                drawEmptyState(width, height, language)
            }
        }

        // Live Geofence Banner inside canvas overlay
        geofenceStatus?.let { status ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when (status.state) {
                            GeofenceState.INSIDE -> Color(0xFF1B5E20).copy(alpha = 0.90f)
                            GeofenceState.NEARBY -> Color(0xFFE65100).copy(alpha = 0.90f)
                            GeofenceState.OUTSIDE -> Color(0xFF37474F).copy(alpha = 0.88f)
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.BANGLA) status.messageBn else status.messageEn,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun DrawScope.drawGrid(width: Float, height: Float) {
    val gridSpacing = 32f
    val gridColor = Color(0xFFD7EAD9)

    var x = 0f
    while (x <= width) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
        x += gridSpacing
    }

    var y = 0f
    while (y <= height) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += gridSpacing
    }
}

private fun DrawScope.drawNorthIndicator(width: Float, height: Float, language: AppLanguage) {
    val centerX = width - 24f
    val centerY = 24f

    // Circle background
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = 16f,
        center = Offset(centerX, centerY)
    )
    drawCircle(
        color = AgrigreenDark.copy(alpha = 0.4f),
        radius = 16f,
        center = Offset(centerX, centerY),
        style = Stroke(width = 1.5f)
    )

    // North arrow tip (red)
    val pathNorth = Path().apply {
        moveTo(centerX, centerY - 12f)
        lineTo(centerX - 5f, centerY)
        lineTo(centerX, centerY - 3f)
        close()
    }
    drawPath(pathNorth, color = Color(0xFFD32F2F))

    val pathSouth = Path().apply {
        moveTo(centerX, centerY + 12f)
        lineTo(centerX + 5f, centerY)
        lineTo(centerX, centerY + 3f)
        close()
    }
    drawPath(pathSouth, color = Color(0xFF757575))

    val label = if (language == AppLanguage.BANGLA) "উ" else "N"
    drawContext.canvas.nativeCanvas.drawText(
        label,
        centerX - 4f,
        centerY - 16f,
        android.graphics.Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 22f
            isFakeBoldText = true
        }
    )
}

private fun DrawScope.drawPolygonPlot(
    points: List<GeoPoint>,
    width: Float,
    height: Float,
    userLat: Double?,
    userLon: Double?,
    pulseRadius: Float,
    pulseAlpha: Float,
    isInside: Boolean,
    plotAreaDecimals: Double,
    language: AppLanguage
) {
    val allLats = points.map { it.latitude } + listOfNotNull(userLat)
    val allLons = points.map { it.longitude } + listOfNotNull(userLon)

    val minLat = allLats.minOrNull() ?: 0.0
    val maxLat = allLats.maxOrNull() ?: 1.0
    val minLon = allLons.minOrNull() ?: 0.0
    val maxLon = allLons.maxOrNull() ?: 1.0

    val latSpan = (maxLat - minLat).coerceAtLeast(0.0001)
    val lonSpan = (maxLon - minLon).coerceAtLeast(0.0001)

    val padding = 36f
    val drawWidth = width - (padding * 2)
    val drawHeight = height - (padding * 2)

    fun toScreen(lat: Double, lon: Double): Offset {
        val xNorm = ((lon - minLon) / lonSpan).toFloat()
        val yNorm = (1f - ((lat - minLat) / latSpan).toFloat()) // Invert Y for screen coordinates
        return Offset(padding + (xNorm * drawWidth), padding + (yNorm * drawHeight))
    }

    val screenPoints = points.map { toScreen(it.latitude, it.longitude) }

    // 1. Draw Field Polygon Fill & Stroke
    val polyPath = Path().apply {
        moveTo(screenPoints.first().x, screenPoints.first().y)
        for (i in 1 until screenPoints.size) {
            lineTo(screenPoints[i].x, screenPoints[i].y)
        }
        close()
    }

    // Fill with soft farm green
    drawPath(
        path = polyPath,
        color = if (isInside) Color(0xFF81C784).copy(alpha = 0.45f) else Color(0xFFA5D6A7).copy(alpha = 0.32f),
        style = Fill
    )

    // Boundary stroke with dash or solid
    drawPath(
        path = polyPath,
        color = if (isInside) Color(0xFF2E7D32) else AgrigreenPrimary,
        style = Stroke(
            width = 3.5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // 2. Draw Corner Nodes & Vertex Labels (P1, P2, P3...)
    for (i in screenPoints.indices) {
        val pt = screenPoints[i]
        // White border circle
        drawCircle(
            color = Color.White,
            radius = 10f,
            center = pt
        )
        // Colored node
        drawCircle(
            color = AgrigreenDark,
            radius = 7.5f,
            center = pt
        )

        // Draw pin number text
        drawContext.canvas.nativeCanvas.drawText(
            "${i + 1}",
            pt.x - 4f,
            pt.y - 12f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.rgb(27, 94, 32)
                textSize = 24f
                isFakeBoldText = true
            }
        )
    }

    // 3. Draw Center Area Tag
    val centerScreen = Offset(
        screenPoints.map { it.x }.average().toFloat(),
        screenPoints.map { it.y }.average().toFloat()
    )
    val areaStr = if (language == AppLanguage.BANGLA) {
        "${String.format(Locale.US, "%.1f", plotAreaDecimals)} শতক"
    } else {
        "${String.format(Locale.US, "%.1f", plotAreaDecimals)} Dec"
    }
    drawContext.canvas.nativeCanvas.drawText(
        areaStr,
        centerScreen.x - 30f,
        centerScreen.y + 6f,
        android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(27, 94, 32)
            textSize = 26f
            isFakeBoldText = true
        }
    )

    // 4. Draw User Live Location Marker if available
    if (userLat != null && userLon != null) {
        val userScreen = toScreen(userLat, userLon)

        // Radar pulse ring
        drawCircle(
            color = (if (isInside) Color(0xFF4CAF50) else Color(0xFFFF9800)).copy(alpha = pulseAlpha),
            radius = pulseRadius,
            center = userScreen,
            style = Stroke(width = 2.5f)
        )

        // User GPS dot
        drawCircle(
            color = Color.White,
            radius = 8f,
            center = userScreen
        )
        drawCircle(
            color = if (isInside) Color(0xFF2E7D32) else Color(0xFFE65100),
            radius = 5.5f,
            center = userScreen
        )

        // If outside, draw line to closest plot vertex
        if (!isInside) {
            val closest = screenPoints.minByOrNull { (it.x - userScreen.x) * (it.x - userScreen.x) + (it.y - userScreen.y) * (it.y - userScreen.y) }
            if (closest != null) {
                drawLine(
                    color = Color(0xFFFF9800).copy(alpha = 0.8f),
                    start = userScreen,
                    end = closest,
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )
            }
        }
    }
}

private fun DrawScope.drawCircularPlot(
    centerLat: Double,
    centerLon: Double,
    radiusMeters: Double,
    width: Float,
    height: Float,
    userLat: Double?,
    userLon: Double?,
    pulseRadius: Float,
    pulseAlpha: Float,
    isInside: Boolean,
    plotAreaDecimals: Double,
    language: AppLanguage
) {
    val centerScreen = Offset(width / 2f, height / 2f)
    val screenRadius = (height / 2.6f).coerceAtMost(width / 2.6f)

    // Circle fill
    drawCircle(
        color = if (isInside) Color(0xFF81C784).copy(alpha = 0.45f) else Color(0xFFA5D6A7).copy(alpha = 0.30f),
        radius = screenRadius,
        center = centerScreen
    )
    // Circle boundary
    drawCircle(
        color = if (isInside) Color(0xFF2E7D32) else AgrigreenPrimary,
        radius = screenRadius,
        center = centerScreen,
        style = Stroke(width = 3.5f)
    )

    // Center plot icon
    drawCircle(
        color = AgrigreenDark,
        radius = 6f,
        center = centerScreen
    )

    // Area text
    val areaStr = if (language == AppLanguage.BANGLA) {
        "ব্যাসার্ধ: ${radiusMeters.toInt()}মি (${String.format(Locale.US, "%.1f", plotAreaDecimals)} শতক)"
    } else {
        "R: ${radiusMeters.toInt()}m (${String.format(Locale.US, "%.1f", plotAreaDecimals)} Dec)"
    }
    drawContext.canvas.nativeCanvas.drawText(
        areaStr,
        centerScreen.x - 70f,
        centerScreen.y - 12f,
        android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(27, 94, 32)
            textSize = 24f
            isFakeBoldText = true
        }
    )

    // User marker
    if (userLat != null && userLon != null) {
        val dist = PlotGeofenceManager.haversineDistanceMeters(userLat, userLon, centerLat, centerLon)
        val userScreen = if (dist <= radiusMeters) {
            // inside: place proportionally
            val ratio = (dist / radiusMeters).toFloat().coerceIn(0f, 0.85f)
            Offset(centerScreen.x + (screenRadius * ratio * 0.7f), centerScreen.y + (screenRadius * ratio * 0.4f))
        } else {
            // outside
            Offset(centerScreen.x + screenRadius + 24f, centerScreen.y + 10f)
        }

        drawCircle(
            color = (if (isInside) Color(0xFF4CAF50) else Color(0xFFFF9800)).copy(alpha = pulseAlpha),
            radius = pulseRadius,
            center = userScreen,
            style = Stroke(width = 2.5f)
        )
        drawCircle(
            color = Color.White,
            radius = 7.5f,
            center = userScreen
        )
        drawCircle(
            color = if (isInside) Color(0xFF2E7D32) else Color(0xFFE65100),
            radius = 5f,
            center = userScreen
        )
    }
}

private fun DrawScope.drawIncompletePoints(
    points: List<GeoPoint>,
    width: Float,
    height: Float,
    userLat: Double?,
    userLon: Double?
) {
    val center = Offset(width / 2f, height / 2f)
    for (i in points.indices) {
        val pt = Offset(center.x + (i * 50f - 25f), center.y + (i * 20f - 10f))
        drawCircle(color = AgrigreenDark, radius = 7f, center = pt)
        drawContext.canvas.nativeCanvas.drawText(
            "P${i + 1}",
            pt.x - 6f,
            pt.y - 12f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 22f
            }
        )
    }
}

private fun DrawScope.drawEmptyState(width: Float, height: Float, language: AppLanguage) {
    val msg = if (language == AppLanguage.BANGLA)
        "প্লটের কোণাগুলো যুক্ত করতে 'বর্তমান GPS পয়েন্ট যোগ করুন' চাপুন"
    else
        "Tap 'Add GPS Point' while standing at plot corners to map boundary"

    drawContext.canvas.nativeCanvas.drawText(
        msg,
        width * 0.08f,
        height * 0.52f,
        android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 24f
        }
    )
}
