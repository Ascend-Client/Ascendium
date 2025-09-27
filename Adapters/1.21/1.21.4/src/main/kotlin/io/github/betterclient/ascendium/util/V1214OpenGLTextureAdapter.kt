package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.TextureBridge
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skiko.toImage
import java.awt.image.BufferedImage

class V1214OpenGLTextureAdapter() : TextureBridge {
    @Volatile private var pendingImage: Image? = null
    private var image: Image? = null

    override fun update(image: BufferedImage) {
        pendingImage = image.toImage()
    }

    override fun blit() {
        val newImage = pendingImage?.also { pendingImage = null }
        if (newImage != null) image = newImage

        image?.let {
            V1214SkiaRenderAdapterObject.withSkia { canvas ->
                canvas.drawImageRect(it, Rect.makeWH(it.width.toFloat(), it.height.toFloat()))
            }
        }
    }
}