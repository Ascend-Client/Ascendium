package io.github.betterclient.ascendium.module.impl.hud

import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.bridge.Pos3D
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.module.HUDModule
import kotlin.math.absoluteValue
import kotlin.math.max

object AccelerometerMod : HUDModule("Accelerometer", "Track your speed") {
    val mode by dropdown("Mode", "Text", "Slider", "Circle")

    val template by string("Template", "%SPEED% m/s") { mode == "Text" }
    val speedPrecision by number("Precision", 2.0, 0.0, 4.0) { mode == "Text" }

    val orientation by dropdown("Orientation", "Vertical", "Horizontal") { mode == "Slider" }
    val maxTime by number("Max time", 5.0, 2.0, 10.0) { mode != "Text" } //max time of slider/circle

    //tracking
    lateinit var lastPos: Pos3D
    var speed by mutableStateOf(0.0)

    @Composable
    override fun Render() {
        when(mode) {
            "Text" -> Text(template.replace("%SPEED%", String.format("%.${speedPrecision.toInt()}f", speed)))
            "Slider" -> SliderRender()
            "Circle" -> CircleRender()
        }
    }

    @Composable
    fun CircleRender() {
        val (max, value) = LinearValues()

        CircularProgressIndicator(
            progress = {
                (value / max).coerceIn(0f, 1f)
            }
        )
    }

    @Composable
    fun SliderRender() {
        val (max, value) = LinearValues()

        when(orientation) {
            "Vertical" ->
                VerticalSlider(
                    value,
                    {},
                    modifier = Modifier.size(25.dp, 100.dp),
                    valueRange = 0f..max
                )
            "Horizontal" ->
                Slider(
                    value,
                    {},
                    modifier = Modifier.size(100.dp, 25.dp),
                    valueRange = 0f..max
                )
        }
    }

    @Composable
    fun LinearValues(): Pair<Float, Float> {
        val max by animateFloatAsState(
            targetValue = range,
            animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
        )

        val sliderValue by animateFloatAsState(
            targetValue = speed.toFloat(),
            animationSpec = spring( //animate sliderValue slower so it doesn't lag back
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioNoBouncy
            )
        )

        return max to sliderValue.coerceIn(0f, max)
    }

    @Composable
    override fun RenderPreview() {
        Render()
    }

    var range by mutableStateOf(1f)
    var deltas1Sec = mutableListOf<Pair<Long, Double>>()
    var deltasMax = mutableListOf<Pair<Long, Double>>()

    @EventTarget
    fun onRender(e: RenderHudEvent) {
        if (::lastPos.isInitialized) {
            val delta = lastPos.distanceTo(minecraft.player.getPos())
            if (delta < 20) {
                deltas1Sec.add(System.currentTimeMillis() to delta)
            }
        }

        lastPos = minecraft.player.getPos()

        val now = System.currentTimeMillis()
        deltasMax.removeAll { it.first < now - maxTime }
        deltas1Sec.removeAll { it.first < now - 1000 }

        speed = deltas1Sec.sumOf { it.second.absoluteValue }
        deltasMax.add(now to speed)

        range = max(deltasMax.maxOfOrNull { it.second } ?: 1.0, 1.0).toFloat()
    }

    @Composable
    fun VerticalSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        modifier: Modifier = Modifier,
        valueRange: ClosedFloatingPointRange<Float> = 0f..1f
    ) {
        Layout(
            modifier = modifier,
            content = {
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    modifier = Modifier.rotate(-90f)
                )
            }
        ) { measurables, constraints ->
            val placeable = measurables.first().measure(
                constraints.copy(
                    minWidth = constraints.minHeight,
                    maxWidth = constraints.maxHeight,
                    minHeight = constraints.minWidth,
                    maxHeight = constraints.maxWidth
                )
            )

            layout(placeable.height, placeable.width) {
                val x = (placeable.height - placeable.width) / 2
                val y = (placeable.width - placeable.height) / 2
                placeable.place(x, y)
            }
        }
    }
}