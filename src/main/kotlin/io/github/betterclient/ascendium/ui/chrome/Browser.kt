package io.github.betterclient.ascendium.ui.chrome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefRendering
import org.cef.handler.CefNativeRenderHandler
import org.cef.handler.CefScreenInfo
import java.awt.Component
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Browser(
    private val client: CefClient,
    private val url: String,
    private val transparent: Boolean = false
) : CefNativeRenderHandler {
    private var cefBrowser: CefBrowser? = null
    private var browserImage: BufferedImage? = null
    private var browserBitmap: ImageBitmap? = null
    private var browserComponent: Component? = null
    var screenPosition by mutableStateOf(Point(0, 0))

    var bitmap: ImageBitmap? by mutableStateOf(null)
        private set

    fun createBrowser(component: Component) {
        if (cefBrowser != null) return
        this.browserComponent = component
        val rendering = CefRendering.CefRenderingWithHandler(this, component)
        cefBrowser = client.createBrowser(url, rendering, transparent)
        cefBrowser?.createImmediately()
    }

    fun close() {
        cefBrowser?.close(true)
    }

    override fun getViewRect(browser: CefBrowser?): Rectangle {
        return browserComponent?.bounds ?: Rectangle(0, 0, 1, 1)
    }

    override fun getScreenPoint(browser: CefBrowser?, viewPoint: Point?): Point {
        if (viewPoint == null) return Point(0, 0)
        return Point(screenPosition.x + viewPoint.x, screenPosition.y + viewPoint.y)
    }

    override fun onPaint(
        browser: CefBrowser?,
        popup: Boolean,
        dirtyRects: Array<out Rectangle>?,
        buffer: ByteBuffer?,
        width: Int,
        height: Int
    ) {
        if (width == 0 || height == 0 || buffer == null) return

        if (browserImage == null || browserImage!!.width != width || browserImage!!.height != height) {
            browserImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE)
        }

        buffer.order(ByteOrder.LITTLE_ENDIAN)
        val pixels = (browserImage!!.raster.dataBuffer as DataBufferInt).data
        buffer.asIntBuffer().get(pixels, 0, pixels.size)

        browserBitmap = browserImage!!.toComposeImageBitmap()
        bitmap = browserBitmap
    }

    override fun getScreenInfo(browser: CefBrowser?, screenInfo: CefScreenInfo?): Boolean {
        if (screenInfo == null || browserComponent == null) return false

        val gc = browserComponent!!.graphicsConfiguration ?: return false
        val scale = gc.defaultTransform.scaleX

        val bounds = gc.bounds
        val availableBounds = gc.bounds

        screenInfo.Set(
            scale,
            32,
            8,
            false,
            bounds,
            availableBounds
        )
        return true
    }

    fun wasResized(width: Int, height: Int) {
        cefBrowser?.wasResized(width, height)
    }

    fun setFocus(focused: Boolean) {
        cefBrowser?.setFocus(focused)
    }

    override fun onPaintWithSharedMem(
        browser: CefBrowser?,
        popup: Boolean,
        dirtyRectsCount: Int,
        sharedMemName: String?,
        sharedMemHandle: Long,
        width: Int,
        height: Int
    ) {
        println("onPaintWithSharedMem received (not rendered): handle=$sharedMemHandle")
    }

    fun sendKeyEvent(event: KeyEvent) {
        cefBrowser?.sendKeyEvent(event)
    }

    fun sendMouseEvent(event: MouseEvent) {
        cefBrowser?.sendMouseEvent(event)
    }

    fun sendMouseWheelEvent(event: MouseWheelEvent) {
        cefBrowser?.sendMouseWheelEvent(event)
    }

    override fun getDeviceScaleFactor(browser: CefBrowser?): Double = 1.0
    override fun onPopupShow(browser: CefBrowser?, show: Boolean) {}
    override fun onPopupSize(browser: CefBrowser?, size: Rectangle?) {}
    override fun onCursorChange(browser: CefBrowser?, cursorType: Int): Boolean = false
    override fun startDragging(
        browser: CefBrowser?,
        dragData: org.cef.callback.CefDragData?,
        mask: Int,
        x: Int,
        y: Int
    ): Boolean = false
    override fun updateDragCursor(browser: CefBrowser?, operation: Int) {}
    override fun disposeNativeResources() { }
}