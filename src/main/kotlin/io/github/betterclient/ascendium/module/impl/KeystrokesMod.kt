package io.github.betterclient.ascendium.module.impl

import io.github.betterclient.ascendium.Bridge
import io.github.betterclient.ascendium.KeybindingBridge
import io.github.betterclient.ascendium.module.HUDModule
import io.github.betterclient.ascendium.module.Renderable
import kotlin.math.floor
import kotlin.math.roundToInt

object KeystrokesMod : HUDModule("Keystrokes", "Show what keys you are pressing", hasBackground = false) {
    val keySize by number("Key size", 15.0, 10.0, 30.0)
    val pressedColor by color("Pressed Color", 0x71000000)
    val unPressedColor by color("Unpressed Color", 0x51000000)
    val mouseKeys by boolean("Show mouse keys", true)
    val spaceBar by boolean("Show space bar", false)

    private val keyForward = Bridge.client.gameOptions.keyForward
    private val keyBackward = Bridge.client.gameOptions.keyBackward
    private val keyLeft = Bridge.client.gameOptions.keyLeft
    private val keyRight = Bridge.client.gameOptions.keyRight
    private val keyAttack = Bridge.client.gameOptions.keyAttack
    private val keyUse = Bridge.client.gameOptions.keyUse
    private val keyJump = Bridge.client.gameOptions.keyJump

    override fun render(renderer: Renderable) {
        val size = keySize
        val spacing = 2.0

        val totalWidth = size * 3 + spacing * 2
        renderKey(renderer, keyForward, size + spacing, 0.0, size)
        renderKey(renderer, keyLeft, 0.0, size + spacing, size)
        renderKey(renderer, keyBackward, size + spacing, size + spacing, size)
        renderKey(renderer, keyRight, size * 2 + spacing * 2, size + spacing, size)

        if (mouseKeys) {
            val yPos = size * 2 + spacing * 2
            val lmbWidth = floor((totalWidth - spacing) / 2.0)
            val rmbWidth = totalWidth - lmbWidth - spacing

            renderKey(renderer, keyAttack, 0.0, yPos, lmbWidth, size, "LMB")
            renderKey(renderer, keyUse, lmbWidth + spacing, yPos, rmbWidth, size, "RMB")
        }

        if (spaceBar) {
            val wide = totalWidth

            val baseY = if (mouseKeys) {
                (size * 2 + spacing * 2) + size + spacing
            } else {
                (size + spacing) + size + spacing
            }
            renderKey(renderer, keyJump, 0.0, baseY, wide, size)
        }
    }

    private fun renderKey(renderer: Renderable, key: KeybindingBridge, x: Double, y: Double, sizeW: Double, sizeH: Double = sizeW, label: String = key.getBoundKey) {
        renderer.renderTextWithBG(
            label,
            x.roundToInt(), y.roundToInt(),
            sizeW.roundToInt(), sizeH.roundToInt(),
            if (key.buttonPressed) pressedColor else unPressedColor
        )
    }
}