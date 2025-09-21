package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.ui.bridge.compose.SkiaRenderAdapter
import org.jetbrains.skia.*
import org.lwjgl.opengl.GL33C

class V1214SkiaRenderAdapter : SkiaRenderAdapter {
    override fun withSkia(block: (Canvas) -> Unit) {
        V1214SkiaRenderAdapterObject.withSkia(block)
    }

    override fun task(block: () -> Unit) {
        block() //this is gpu rendering, not needed
    }
}

object V1214SkiaRenderAdapterObject {
    private var vpW = 0
    private var vpH = 0
    private var context = DirectContext.makeGL()
    private lateinit var renderTarget: BackendRenderTarget
    private lateinit var surface: Surface

    private fun setKnownGoodStateForSkia() {
        //"known good state for skia" - Gemini
        GL33C.glBindBuffer(GL33C.GL_PIXEL_UNPACK_BUFFER, 0)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_SWAP_BYTES, GL33C.GL_FALSE)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_LSB_FIRST, GL33C.GL_FALSE)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_ROW_LENGTH, 0)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_SKIP_ROWS, 0)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_SKIP_PIXELS, 0)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_ALIGNMENT, 4)
    }

    fun withSkia(block: (Canvas) -> Unit) {
        init()
        GlStateUtil.save()
        setKnownGoodStateForSkia()

        this.context.resetAll()
        block(surface.canvas)
        this.context.flush()

        GlStateUtil.restore()
    }

    fun init() {
        val window = minecraft.window

        if (vpW == vpH && vpW == 0) {
            renderTarget = BackendRenderTarget.makeGL(
                window.fbWidth,
                window.fbHeight,
                0,
                8,
                window.fbo,
                FramebufferFormat.GR_GL_RGBA8
            )

            surface = Surface.makeFromBackendRenderTarget(
                context,
                renderTarget,
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.sRGB
            )!!
            vpW = window.fbWidth
            vpH = window.fbHeight
        } else if (vpW != window.fbWidth || vpH != window.fbHeight) {
            if (::surface.isInitialized) surface.close()
            if (::renderTarget.isInitialized) renderTarget.close()
            context = DirectContext.makeGL()

            renderTarget = BackendRenderTarget.makeGL(
                window.fbWidth,
                window.fbHeight,
                0,
                8,
                window.fbo,
                FramebufferFormat.GR_GL_RGBA8
            )

            surface = Surface.makeFromBackendRenderTarget(
                context,
                renderTarget,
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.sRGB
            )!!

            vpW = window.fbWidth
            vpH = window.fbHeight
        }
    }
}