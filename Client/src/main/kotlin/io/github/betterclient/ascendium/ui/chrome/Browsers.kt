package io.github.betterclient.ascendium.ui.chrome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.betterclient.ascendium.bridge.createOpenGLTexture
import io.github.betterclient.ascendium.bridge.minecraft
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.browser.CefRendering
import org.cef.handler.CefDisplayHandler
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefNativeRenderHandler
import org.cef.handler.CefScreenInfo
import org.cef.network.CefRequest
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

abstract class BaseBrowser(
    app: CefApp,
    private val url: String,
    private val transparent: Boolean = false
) : CefNativeRenderHandler {
    val client = app.createClient().alsoAddPopupPrevention()
    private var cefBrowser: CefBrowser? = null
    private var browserImage: BufferedImage? = null
    private var browserComponent: Component? = null
    var screenPosition by mutableStateOf(Point(0, 0))

    val browser: CefBrowser?
        get() = cefBrowser

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

    fun back() = cefBrowser!!.goBack()
    fun forward() = cefBrowser!!.goForward()
    fun setUrl(url: String) = cefBrowser!!.loadURL(url)

    override fun getViewRect(browser: CefBrowser?): Rectangle {
        return browserComponent?.bounds ?: Rectangle(0, 0, 1, 1)
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

        handleRender(browserImage!!)
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

    override fun getScreenPoint(browser: CefBrowser?, viewPoint: Point?): Point {
        if (viewPoint == null) return Point(0, 0)
        return Point(screenPosition.x + viewPoint.x, screenPosition.y + viewPoint.y)
    }

    fun wasResized(width: Int, height: Int) {
        cefBrowser?.wasResized(width, height)
    }

    fun setFocus(focused: Boolean) {
        cefBrowser?.setFocus(focused)
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

    abstract fun handleRender(image: BufferedImage)
}

class OpenGLBrowser(
    client: CefApp,
    url: String,
    transparent: Boolean = false
) : BaseBrowser(client, url, transparent) {
    val texture = createOpenGLTexture()

    override fun handleRender(image: BufferedImage) {
        texture.update(image)
    }

    fun render() {
        texture.blit()
    }
}

class ComposeBrowser(
    app: CefApp,
    url: String,
    transparent: Boolean = false
) : BaseBrowser(app, url, transparent), CefLoadHandler, CefDisplayHandler {
    private var browserBitmap: ImageBitmap? = null

    var bitmap: ImageBitmap? by mutableStateOf(null)
        private set

    var myURL by mutableStateOf(url)
        private set
    var canGoBack by mutableStateOf(false)
        private set
    var canGoForward by mutableStateOf(false)
        private set

    init {
        client.addLoadHandler(this)
        client.addDisplayHandler(this)
    }

    override fun onLoadingStateChange(
        browser: CefBrowser?,
        isLoading: Boolean,
        canGoBack: Boolean,
        canGoForward: Boolean
    ) {
        this.canGoBack = canGoBack
        this.canGoForward = canGoForward
    }

    override fun onAddressChange(browser: CefBrowser?, frame: CefFrame?, url: String?) {
        if (url != null) {
            this.myURL = url
        }
    }

    override fun onTitleChange(browser: CefBrowser?, title: String?) {}
    override fun onLoadStart(browser: CefBrowser?, frame: CefFrame?, transitionType: CefRequest.TransitionType?) {}
    override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {}
    override fun onLoadError(browser: CefBrowser?, frame: CefFrame?, errorCode: CefLoadHandler.ErrorCode?, errorText: String?, failedUrl: String?) {}
    override fun onFullscreenModeChange(browser: CefBrowser?, fullscreen: Boolean) {}
    override fun onTooltip(browser: CefBrowser?, text: String?): Boolean = false
    override fun onStatusMessage(browser: CefBrowser?, value: String?) {}
    override fun onConsoleMessage(browser: CefBrowser?, level: org.cef.CefSettings.LogSeverity?, message: String?, source: String?, line: Int): Boolean = false

    override fun handleRender(image: BufferedImage) {
        this.browserBitmap = image.toComposeImageBitmap()
    }
}