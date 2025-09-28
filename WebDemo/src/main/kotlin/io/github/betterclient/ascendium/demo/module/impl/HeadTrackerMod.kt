package io.github.betterclient.ascendium.demo.module.impl

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.demo.config.rgb
import io.github.betterclient.ascendium.demo.module.Module
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

object HeadTrackerMod : Module("Mouse Tracker", "Track your mouse movements", 300) {
    val BALL_SIZE by number("Ball Size", 5.0, 1.0, 10.0)
    val BALL_COLOR by color("Ball color", Color.Red.rgb)
    val TRAIL_SIZE by number("Trail size", 30.0, 0.0, 100.0)
    const val SIZE = 100
    const val SIZE_2 = 50

    init {
        settings.removeAll { it.name == "Text Color" } //yea
    }

    @Composable
    override fun Render() {
        var targetX by remember { mutableStateOf(0f) }
        var targetY by remember { mutableStateOf(0f) }
        val x by animateFloatAsState(targetX)
        val y by animateFloatAsState(targetY)
        LaunchedEffect(Unit) {
            while (true) {
                targetX = (Random.nextFloat() * 360).wrapDegrees()
                targetY = (Random.nextFloat() * 180).minus(90f).coerceIn(-90f, 90f)
                delay(500)
            }
        }

        HeadTracker(x, y)
    }

    fun Float.wrapDegrees(): Float {
        var f: Float = this % 360.0f
        if (f >= 180.0f) {
            f -= 360.0f
        }

        if (f < -180.0f) {
            f += 360.0f
        }
        return f
    }

    @Composable
    fun HeadTracker(
        yaw: Float,
        pitch: Float,
        modifier: Modifier = Modifier
    ) {
        val scope = rememberCoroutineScope()
        var posX by remember { mutableStateOf(0f) }
        var posY by remember { mutableStateOf(0f) }

        val x by animateFloatAsState(posX)
        val y by animateFloatAsState(posY)
        val trail = remember { mutableStateListOf<Pair<Float, Float>>() }

        var lastYaw by remember { mutableStateOf(yaw) }
        var lastPitch by remember { mutableStateOf(pitch) }
        var resetJob: Job? by remember { mutableStateOf(null) }

        fun normalizeAngleDiff(new: Float, old: Float): Float {
            var diff = new - old
            if (diff > 180f) diff -= 360f
            if (diff < -180f) diff += 360f
            return diff
        }

        LaunchedEffect(yaw, pitch) {
            val dyaw = normalizeAngleDiff(yaw, lastYaw)
            val dpitch = pitch - lastPitch

            lastYaw = yaw
            lastPitch = pitch

            if (abs(dyaw) > 0.1f || abs(dpitch) > 0.1f) {
                posX += (dyaw / 180f).coerceIn(-1f, 1f) * SIZE_2.toFloat()
                posY += (dpitch / 90f).coerceIn(-1f, 1f) * SIZE_2.toFloat()
            }

            //j*b j*b j*b
            resetJob?.cancel()
            resetJob = scope.launch {
                delay(300)
                if (abs(yaw - lastYaw) <= 0.1f && abs(pitch - lastPitch) <= 0.1f) {
                    posX = 0f
                    posY = 0f
                }
            }
            posX = posX.coerceIn(-SIZE_2.toFloat(), SIZE_2.toFloat())
            posY = posY.coerceIn(-SIZE_2.toFloat(), SIZE_2.toFloat())
        }

        LaunchedEffect(x, y) {
            trail.add(x to y)
            if (trail.size > TRAIL_SIZE) {
                trail.removeAt(0)
            }
        }

        Box(
            modifier = modifier.size(SIZE.dp),
            contentAlignment = Alignment.Center
        ) {
            trail.forEachIndexed { index, (tx, ty) ->
                Box(
                    modifier = Modifier
                        .offset(x = tx.dp, y = ty.dp)
                        .size(BALL_SIZE.dp)
                        .background(Color(BALL_COLOR).copy(alpha = (index + 1) / trail.size.toFloat()), RoundedCornerShape(BALL_SIZE.dp))
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = x.dp, y = y.dp)
                    .size(BALL_SIZE.dp)
                    .background(Color(BALL_COLOR), RoundedCornerShape(BALL_SIZE.dp))
            )
        }
    }
}