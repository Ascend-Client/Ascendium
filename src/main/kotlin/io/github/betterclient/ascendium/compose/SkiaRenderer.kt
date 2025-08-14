package io.github.betterclient.ascendium.compose

import io.github.betterclient.ascendium.Bridge
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.DirectContext
import org.jetbrains.skia.FramebufferFormat
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceColorFormat
import org.jetbrains.skia.SurfaceOrigin
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL21C

object SkiaRenderer {
    private var vpW = 0
    private var vpH = 0
    private var context = DirectContext.makeGL()
    private lateinit var renderTarget: BackendRenderTarget
    private lateinit var surface: Surface

    private fun setKnownGoodStateForSkia() {
        //"known good state for skia" - Gemini
        GL21C.glBindBuffer(GL21C.GL_PIXEL_UNPACK_BUFFER, 0)
        GL11C.glPixelStorei(GL11C.GL_UNPACK_SWAP_BYTES, GL11C.GL_FALSE)
        GL11C.glPixelStorei(GL11C.GL_UNPACK_LSB_FIRST, GL11C.GL_FALSE)
        GL11C.glPixelStorei(GL11C.GL_UNPACK_ROW_LENGTH, 0)
        GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_ROWS, 0)
        GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_PIXELS, 0)
        GL11C.glPixelStorei(GL11C.GL_UNPACK_ALIGNMENT, 4)

        if (GL11C.glIsEnabled(GL11C.GL_SCISSOR_TEST)) {
            GL11C.glDisable(GL11C.GL_SCISSOR_TEST)
        }
    }

    private fun setKnownGoodStateForMc() {
        if (GL11C.glIsEnabled(GL11C.GL_SCISSOR_TEST)) {
            GL11C.glDisable(GL11C.GL_SCISSOR_TEST)
        }
    }

    fun withSkia(block: (Canvas) -> Unit) {
        GlStateUtil.save()
        setKnownGoodStateForSkia()
        this.context.resetGLAll()

        block(surface.canvas)

        this.context.flush()
        GlStateUtil.restore()
        setKnownGoodStateForMc()
    }

    fun init() {
        val window = Bridge.client.window

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

    fun <T : Number> getUnscaled(i: T): Float {
        val scaleFactor = Bridge.client.window.scale
        return ((i.toDouble() * scaleFactor).toFloat())
    }

    fun <T : Number> getScaled(i: T): Float {
        val scaleFactor = Bridge.client.window.scale
        return ((i.toDouble() / scaleFactor).toFloat())
    }
}

fun Int.asPaint() = Paint().apply { color = this@asPaint }
fun Number.getScaled() = SkiaRenderer.getScaled(this)
fun Number.getUnscaled() = SkiaRenderer.getUnscaled(this)