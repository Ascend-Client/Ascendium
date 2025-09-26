package io.github.betterclient.ascendium.module.impl.hud

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.module.HUDModule
import io.github.betterclient.ascendium.ui.config.rgb

object CompassMod : HUDModule("Compass", "Display the direction you're facing") {
    val indicatorColor by color("Indicator color", Color.Red.rgb)
    val tickColor by color("Tick color", Color.White.rgb)
    val tickSpacing by number("Tick spacing", 16.0, 16.0, 30.0)
    var yaw by mutableStateOf(0f)

    @Composable
    override fun Render() {
        val totalDegrees = 360
        val stepDegrees = 5
        val numTicks = totalDegrees / stepDegrees
        val viewportWidth = 200.dp

        val density = LocalDensity.current
        val tickSpacingPx = with(density) { tickSpacing.dp.toPx() }
        val viewportWidthPx = with(density) { viewportWidth.toPx() }
        val revolutionWidthPx = numTicks * tickSpacingPx

        /*val fadingEdgeBrush = remember(viewportWidthPx) {
            Brush.horizontalGradient(
                0.0f to Color.Transparent,
                0.1f to Color.Black,
                0.9f to Color.Black,
                1.0f to Color.Transparent,
                startX = 0f,
                endX = viewportWidthPx
            )
        }*/

        val scrollState = rememberScrollState()

        val targetScrollPx = remember(yaw, tickSpacing) {
            val correctedYaw = (yaw + 180f) % 360f
            val yawPositionPx = (correctedYaw / totalDegrees) * revolutionWidthPx
            val screenCenterOffsetPx = viewportWidthPx / 2
            (revolutionWidthPx + yawPositionPx - screenCenterOffsetPx + (tickSpacingPx / 2))
        }

        LaunchedEffect(targetScrollPx) {
            scrollState.animateScrollTo(
                value = targetScrollPx.toInt(),
                animationSpec = tween(durationMillis = 0)
            )
        }

        Box(
            modifier = Modifier
                .width(viewportWidth)
                .height(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState, false)
                    /*.graphicsLayer(alpha = 0.99f)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = fadingEdgeBrush,
                            blendMode = BlendMode.DstIn
                        )
                    }*/
            ) {
                repeat(3) {
                    Ticks()
                }
            }

            Box(
                Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(Color(indicatorColor))
            )
        }
    }

    @Composable
    private fun Ticks() {
        Row {
            for (degree in 0 until 360 step 5) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(tickSpacing.dp)
                ) {
                    Box(
                        Modifier
                            .width(2.dp)
                            .height(
                                when {
                                    degree % 90 == 0 -> 20.dp
                                    degree % 45 == 0 -> 15.dp
                                    else -> 10.dp
                                }
                            )
                            .background(Color(tickColor))
                    )

                    if (degree % 45 == 0) {
                        Text(
                            text = getDirectionLabel(degree),
                            color = Color(textColor),
                            fontSize = (if (degree % 90 == 0) 18.sp else 14.sp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    private fun getDirectionLabel(degree: Int): String {
        return when (degree) {
            0 -> "N"
            45 -> "NE"
            90 -> "E"
            135 -> "SE"
            180 -> "S"
            225 -> "SW"
            270 -> "W"
            315 -> "NW"
            else -> ""
        }
    }

    @Composable
    override fun RenderPreview() {
        Render()
    }

    @EventTarget
    fun onRenderEvent(event: RenderHudEvent) {
        yaw = wrapDegrees(minecraft.player.pYaw)
    }

    fun wrapDegrees(degrees: Float): Float {
        var f = degrees % 360.0f
        if (f >= 180.0f) {
            f -= 360.0f
        }

        if (f < -180.0f) {
            f += 360.0f
        }

        return f
    }
}