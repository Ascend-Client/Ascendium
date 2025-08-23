package io.github.betterclient.ascendium.ui.move

import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.module.ComposableHUDModule
import io.github.betterclient.ascendium.module.config.ConfigManager
import org.jetbrains.skia.Rect

class ModuleMover(val mods: List<ComposableHUDModule>) {
    val current = ComposeUI.current
    var isDragging = false
    var dragStartX = 0
    var dragStartY = 0
    var draggingModule: ComposableHUDModule? = null

    fun register() {
        current.addRenderHandler { renderer, mouseX, mouseY ->
            render(renderer, mouseX, mouseY)
        }

        current.addMouseHandler { _, mcCoords, button, clicked ->
            if (button != 0) return@addMouseHandler false
            if (!clicked) return@addMouseHandler isDragging.also { isDragging = false; ConfigManager.saveConfig() } //ew

            return@addMouseHandler handleClick(mcCoords.x, mcCoords.y)
        }
    }

    private fun handleClick(x: Float, y: Float): Boolean {
        mods.forEach {
            val modRect = Rect(it.x.toFloat() - 3, it.y.toFloat() - 3, (it.x + it.width + 3).toFloat(), (it.y + it.height + 3).toFloat())
            if (x >= modRect.left && x <= modRect.right &&
                y >= modRect.top && y <= modRect.bottom) {
                isDragging = true
                draggingModule = it
                dragStartX = x.toInt() - it.x
                dragStartY = y.toInt() - it.y

                return true
            }
        }
        return false
    }

    fun render(renderer: BridgeRenderer, mouseX: Int, mouseY: Int) {
        if (isDragging && draggingModule != null) {
            draggingModule!!.x = mouseX - dragStartX
            draggingModule!!.y = mouseY - dragStartY
        }

        ComposableHUDModule.renderAll(mods)
    }
}