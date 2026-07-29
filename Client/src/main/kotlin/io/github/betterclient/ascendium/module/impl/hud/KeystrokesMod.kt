package io.github.betterclient.ascendium.module.impl.hud

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.bridge.KeybindingBridge
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.bridge.vulkanChecker
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.module.HUDModule
import io.github.betterclient.ascendium.util.ui.blurBackground

object KeystrokesMod : HUDModule("Keystrokes", "Show what keys you are pressing", hasBackground = false) {
    val pressedColor by color("Pressed Color", 0x71000000)
    val unPressedColor by color("Unpressed Color", 0x51000000)
    val mouseKeys by boolean("Show mouse keys", true)
    val spaceBar by boolean("Show space bar", false)
    val joystick by boolean("Joystick mode", false)
    val joystickSize by number("Joystick ball size", 5.0, 1.0, 10.0) { joystick } //don't show if joystick mode is deactivated
    val joystickTrailSize by number("Joystick trail size", 10.0, 0.0, 50.0) { joystick }

    private val keyForward = minecraft.gameOptions.keyForward
    private val keyBackward = minecraft.gameOptions.keyBackward
    private val keyLeft = minecraft.gameOptions.keyLeft
    private val keyRight = minecraft.gameOptions.keyRight
    private val keyAttack = minecraft.gameOptions.keyAttack
    private val keyUse = minecraft.gameOptions.keyUse
    private val keyJump = minecraft.gameOptions.keyJump
    private val allKeys = listOf(keyForward, keyBackward, keyLeft, keyRight, keyAttack, keyUse, keyJump)

    @Composable
    override fun Render() {
        previewHeight = 300
        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            if (joystick) {
                JoystickMovement()
            } else {
                RenderKey(keyForward, modifier = Modifier.fillMaxWidth(1/3f).aspectRatio(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    RenderKeyRow(keyLeft, forceSquare = true)
                    RenderKeyRow(keyBackward, forceSquare = true)
                    RenderKeyRow(keyRight, forceSquare = true)
                }
            }

            if (mouseKeys) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    RenderKeyRow(keyAttack, label = "LMB")
                    RenderKeyRow(keyUse, label = "RMB")
                }
            }

            if (spaceBar) {
                RenderKey(keyJump, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    @Composable
    fun JoystickMovement() {
        val currentColor = Color(unPressedColor)
        val blur by backgroundBlur.state

        Box(
            modifier = Modifier
                .then(if (!mouseKeys && !spaceBar) Modifier.width(100.dp) else Modifier)
                .aspectRatio(4 / 3f)
                .dropShadow(
                    shape = RoundedCornerShape(16.dp),
                    shadow = Shadow(color = currentColor, radius = 16.dp)
                )
                .blurBackground(
                    enabled = blur && vulkanChecker.isVulkan,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = currentColor
                )
                .padding(8.dp)
        ) {
            val trail = remember { mutableStateListOf<Pair<Float, Float>>() }

            val xPos by animateFloatAsState(
                when {
                    keyLeft.pressed && !keyRight.pressed -> -0.9f
                    keyRight.pressed && !keyLeft.pressed -> 0.9f
                    else -> 0f
                }
            )
            val yPos by animateFloatAsState(
                when {
                    keyForward.pressed && !keyBackward.pressed -> -0.8f
                    keyBackward.pressed && !keyForward.pressed -> 0.8f
                    else -> 0f
                }
            )

            LaunchedEffect(xPos, yPos) {
                trail.add(xPos to yPos)
                if (trail.size > joystickTrailSize) {
                    trail.removeAt(0)
                }
            }

            trail.forEachIndexed { index, (tx, ty) ->
                Box(
                    modifier = Modifier
                        .align(BiasAlignment(tx, ty))
                        .size(joystickSize.dp)
                        .background(
                            color = Color(textColor).copy(alpha = (index + 1) / trail.size.toFloat()),
                            shape = RoundedCornerShape(joystickSize.dp)
                        )
                )
            }

            Box(
                Modifier
                    .align(BiasAlignment(xPos, yPos))
                    .size(joystickSize.dp)
                    .background(
                        color = Color(textColor),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }

    @Composable
    override fun RenderPreview() {
        Render()
    }

    @Composable
    fun RenderKey(
        key: KeybindingBridge,
        label: String = key.getBoundKey,
        modifier: Modifier = Modifier
    ) {
        val currentColor = Color(if (key.pressed) pressedColor else unPressedColor)
        val blur by backgroundBlur.state

        Box(
            modifier = modifier
                .padding(2.dp)
                .dropShadow(
                    shape = RoundedCornerShape(8.dp),
                    shadow = Shadow(color = currentColor, radius = 16.dp)
                )
                .blurBackground(
                    enabled = blur && vulkanChecker.isVulkan,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = currentColor
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label.uppercase())
        }
    }

    @Composable
    fun RowScope.RenderKeyRow(
        key: KeybindingBridge,
        label: String = key.getBoundKey,
        forceSquare: Boolean = false
    ) {
        RenderKey(
            key = key,
            label = label,
            modifier = Modifier
                .weight(1f)
                .then(if (forceSquare) Modifier.aspectRatio(1f) else Modifier)
        )
    }

    @EventTarget
    fun onRender(renderHudEvent: RenderHudEvent) {
        for (bridge in allKeys) {
            bridge.pressed = bridge.buttonPressed
        }
    }

    val pressedMap = mutableMapOf<KeybindingBridge, MutableState<Boolean>>()
    var KeybindingBridge.pressed: Boolean
        get() = pressedMap.getOrPut(this) { mutableStateOf(false) }.value
        set(value) { pressedMap.getOrPut(this) { mutableStateOf(false) }.value = value }
}