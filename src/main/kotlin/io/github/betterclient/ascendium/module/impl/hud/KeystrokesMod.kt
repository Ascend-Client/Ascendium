package io.github.betterclient.ascendium.module.impl.hud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.KeybindingBridge
import io.github.betterclient.ascendium.minecraft
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.module.HUDModule

object KeystrokesMod : HUDModule("Keystrokes", "Show what keys you are pressing", hasBackground = false) {
    val pressedColor by color("Pressed Color", 0x71000000)
    val unPressedColor by color("Unpressed Color", 0x51000000)
    val mouseKeys by boolean("Show mouse keys", true)
    val spaceBar by boolean("Show space bar", false)

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
            RenderKey(keyForward, modifier = Modifier.fillMaxWidth(1/3f).aspectRatio(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                RenderKeyRow(keyLeft, forceSquare = true)
                RenderKeyRow(keyBackward, forceSquare = true)
                RenderKeyRow(keyRight, forceSquare = true)
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
    override fun RenderPreview() {
        Render()
    }

    @Composable
    fun RenderKey(key: KeybindingBridge, label: String = key.getBoundKey, modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .dropShadow(
                    shape = RoundedCornerShape(8.dp),
                    shadow = Shadow(color = Color(if (key.pressed) pressedColor else unPressedColor), radius = 16.dp)
                )
                .background(
                    Color(if (key.pressed) pressedColor else unPressedColor),
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label)
        }
    }

    @Composable
    fun RowScope.RenderKeyRow(key: KeybindingBridge, label: String = key.getBoundKey, forceSquare: Boolean = false) {
        Box(
            modifier = Modifier
                .dropShadow(
                    shape = RoundedCornerShape(8.dp),
                    shadow = Shadow(color = Color(if (key.pressed) pressedColor else unPressedColor), radius = 16.dp)
                )
                .background(
                    Color(if (key.pressed) pressedColor else unPressedColor),
                    RoundedCornerShape(8.dp)
                )
                .weight(1f)
                .then(if (forceSquare) Modifier.aspectRatio(1f) else Modifier)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label)
        }
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