package io.github.betterclient.ascendium.demo.module.impl

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.demo.module.Module

object KeystrokesMod : Module("Keystrokes", "Show what keys you are pressing", 300, hasBackground = false) {
    val pressedColor by color("Pressed Color", 0x71000000)
    val unPressedColor by color("Unpressed Color", 0x51000000)
    val mouseKeys by boolean("Show mouse keys", true)
    val spaceBar by boolean("Show space bar", false)
    val joystick by boolean("Joystick mode", false)
    val joystickSize by number("Joystick ball size", 5.0, 1.0, 10.0) { joystick } //don't show if joystick mode is deactivated
    val joystickTrailSize by number("Joystick trail size", 10.0, 0.0, 50.0) { joystick }

    val keyForward = KeybindingBridge(true, "W")
    val keyBackward = KeybindingBridge(false, "S")
    val keyLeft = KeybindingBridge(true, "A")
    val keyRight = KeybindingBridge(false, "D")
    val keyAttack = KeybindingBridge(true, "LMB")
    val keyUse = KeybindingBridge(false, "RMB")
    val keyJump = KeybindingBridge(false, "Space")

    @Composable
    override fun Render() {
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
        Box(Modifier.aspectRatio(4/3f).background(Color(unPressedColor), RoundedCornerShape(16.dp))) {
            val trail = remember { mutableStateListOf<Pair<Float, Float>>() }

            val xPos by animateFloatAsState(
                when {
                    keyLeft.pressed && !keyRight.pressed -> -0.9f //larger
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
                        .background(Color(textColor).copy(alpha = (index + 1) / trail.size.toFloat()), RoundedCornerShape(joystickSize.dp))
                )
            }

            Box(
                Modifier
                    .align(BiasAlignment(xPos, yPos))
                    .size(joystickSize.dp)
                    .background(Color(textColor), RoundedCornerShape(50))
            )
        }
    }

    @Composable
    fun RenderKey(key: KeybindingBridge, label: String = key.boundKey, modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .background(
                    Color(if (key.pressed) pressedColor else unPressedColor),
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label.uppercase())
        }
    }

    @Composable
    fun RowScope.RenderKeyRow(key: KeybindingBridge, label: String = key.boundKey, forceSquare: Boolean = false) {
        Box(
            modifier = Modifier
                .background(
                    Color(if (key.pressed) pressedColor else unPressedColor),
                    RoundedCornerShape(8.dp)
                )
                .weight(1f)
                .then(if (forceSquare) Modifier.aspectRatio(1f) else Modifier)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label.uppercase())
        }
    }

    class KeybindingBridge(
        val pressed: Boolean, val boundKey: String
    )
}