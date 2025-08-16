package io.github.betterclient.ascendium.module

import androidx.compose.ui.unit.IntSize
import io.github.betterclient.ascendium.Bridge
import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.compose.asPaint
import io.github.betterclient.ascendium.compose.getScaled
import io.github.betterclient.ascendium.compose.getUnscaled
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.Rect
import kotlin.math.max

interface ModRenderer {
    fun renderText(text: String, x: Int, y: Int, color: Int): IntSize
    fun renderRect(x: Int, y: Int, width: Int, height: Int, color: Int)
}

class NullMinecraftModRenderer(var size: Float) : ModRenderer {
    override fun renderText(text: String, x: Int, y: Int, color: Int): IntSize {
        val textRenderer = Bridge.client.textRenderer

        val width = textRenderer.getWidth(text) * size
        val height = textRenderer.getFontHeight() * size
        return IntSize(width.toInt(), height.toInt())
    }

    override fun renderRect(x: Int, y: Int, width: Int, height: Int, color: Int) {}
}

class NullSkiaModRenderer(size: Float) : ModRenderer {
    init {
        font.size = 16 * size
    }

    companion object {
        val font = Font(FontMgr.default.matchFamilyStyle("Arial", FontStyle.NORMAL), 16f)
    }

    override fun renderText(text: String, x: Int, y: Int, color: Int): IntSize {
        val textR = font.measureText(text)
        return IntSize(textR.right.getScaled().toInt(), textR.height.getScaled().toInt())
    }

    override fun renderRect(x: Int, y: Int, width: Int, height: Int, color: Int) {}
}

class SkiaModRenderer(size: Float, private val canvas: Canvas) : ModRenderer {
    init {
        font.size = 16 * size
    }

    companion object {
        val font = Font(FontMgr.default.matchFamilyStyle("Arial", FontStyle.NORMAL), 16f)
    }

    override fun renderText(text: String, x: Int, y: Int, color: Int): IntSize {
        val textR = font.measureText(text)

        canvas.drawString(text, x.getUnscaled(), y.getUnscaled() + (textR.height / 2), font, color.asPaint())

        return IntSize(textR.width.getScaled().toInt(), textR.height.getScaled().toInt())
    }

    override fun renderRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        val rect = Rect.makeXYWH(x.getUnscaled(), y.getUnscaled(), (width).getUnscaled(), (height).getUnscaled())
        canvas.drawRect(rect, color.asPaint())
    }
}

class MinecraftModRenderer(var size: Float, private val context: BridgeRenderer): ModRenderer {
    val font = Bridge.client.textRenderer
    override fun renderText(text: String, x: Int, y: Int, color: Int): IntSize {
        val textWidth = font.getWidth(text)
        val textHeight = font.getFontHeight()

        context.drawText(text, x, y, color, size)

        return IntSize(textWidth, textHeight)
    }

    override fun renderRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        context.drawRect(x, y, width, height, color)
    }
}

fun Renderer(size: Float, context: BridgeRenderer? = null, canvas: Canvas? = null): ModRenderer {
    //only one will be null
    return if (context == null) {
        SkiaModRenderer(size, canvas!!)
    } else {
        MinecraftModRenderer(size, context)
    }
}

fun NullRenderer(size: Float, mcRenderer: Boolean): ModRenderer {
    return if (mcRenderer) {
        NullMinecraftModRenderer(size)
    } else {
        NullSkiaModRenderer(size)
    }
}

/**
 * Renderable class that provides mods with a way to render text and rectangles while also keeping track of mod width/height.
 */
class Renderable(val mod: HUDModule, private val renderer: ModRenderer) {
    var width = 0
    var height = 0

    private val nullRenderer = NullRenderer(mod.scale.toFloat(), mod.minecraftRenderer)
    fun renderText(text: String, x: Int, y: Int) {
        val renderText = renderer.renderText(text, mod.x + x, mod.y + y, mod.textColor)
        width = max(x + renderText.width, width)
        height = max(y + renderText.height, height)
    }

    fun renderRect(x: Int, y: Int, width: Int, height: Int, color: Int = mod.backgroundColor.value) {
        renderer.renderRect(mod.x + x, mod.y + y, width, height, color)
        this.width = max(x + width, this.width)
        this.height = max(y + height, this.height)
    }

    fun getSize(text: String): IntSize {
        return nullRenderer.renderText(text, 0, 0, -1)
    }

    fun renderTextWithBG(text: String, x: Int, y: Int, width: Int, height: Int) {
        val size = getSize(text)
        renderRect(x, y, width, height, mod.backgroundColor.value)
        renderText(text, x + (width - size.width) / 2, y + (height - size.height) / 2)
    }

    fun renderBG(nullRenderer: Renderable) {
        this.renderer.renderRect(mod.x - 3, mod.y - 3, nullRenderer.width + 6, nullRenderer.height + 6, mod.backgroundColor.value)
    }
}