package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.ui.bridge.compose.SkiaRenderAdapter
import org.jetbrains.skia.*

class V1165SkiaRenderAdapter : SkiaRenderAdapter {
    override fun withSkia(block: (Canvas) -> Unit) {
        V1165SkiaRenderAdapterObject.withSkia(block)
    }

    override fun task(block: () -> Unit) {
        block() //this is gpu rendering, not needed
    }
}

object V1165SkiaRenderAdapterObject {
    val dc = DirectContext.makeGL()
    var imageInfo: ImageInfo? = null
    var surface: Surface? = null

    fun withSkia(block: (Canvas) -> Unit) {
        var img = imageInfo
        if(img == null || img.width != minecraft.window.fbWidth || img.height != minecraft.window.fbHeight) {
            surface?.close()
            imageInfo = ImageInfo(
                minecraft.window.fbWidth,
                minecraft.window.fbHeight,
                ColorType.RGBA_8888,
                ColorAlphaType.PREMUL,
                ColorSpace.sRGB
            )
            img = imageInfo

            surface = Surface.makeRenderTarget(
                context = dc,
                budgeted = true,
                imageInfo = img!!,
                sampleCount = 0,
                origin = SurfaceOrigin.BOTTOM_LEFT,
                surfaceProps = SurfaceProps(),
            )
        }

        surface?.canvas?.let { block(it) }
        dc.flush(surface!!)
    }
}