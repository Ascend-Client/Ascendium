package io.github.betterclient.ascendium.ui.bridge

import io.github.betterclient.ascendium.bridge.BridgeScreen
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.ui.bridge.compose.AWTUtils
import io.github.betterclient.ascendium.ui.bridge.compose.glfwToAwtKeyCode
import io.github.betterclient.ascendium.ui.chrome.ChromiumDownloader
import io.github.betterclient.ascendium.ui.chrome.OpenGLBrowser
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.callback.CefCallback
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefResourceHandler
import org.cef.handler.CefResourceRequestHandler
import org.cef.misc.BoolRef
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import org.cef.network.CefURLRequest
import org.jetbrains.skia.Color
import org.jetbrains.skia.Rect
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.io.ByteArrayInputStream
import java.net.URLConnection
import javax.swing.JPanel

abstract class ChromiumUI(val startingFile: String) : BridgeScreen() {
    abstract fun serve(fileName: String): ByteArray

    companion object {
        lateinit var browser: OpenGLBrowser
        fun isBrowserInitialized() = ::browser.isInitialized
        val dummyComponent = JPanel()
    }
    init {
        if (isBrowserInitialized()) {
            //add routing manager
            browser.client.removeRequestHandler() //we probably had a chrome ui before this, (its initialized), remove that
            browser.client.addRequestHandler(ChromeUIRequestHandler.createHandler(this))
            browser.setUrl("ascendium://$startingFile")
        }
    }

    override fun render(mouseX: Int, mouseY: Int) {
        if (!isBrowserInitialized() && ChromiumDownloader.app == null) {
            renderUtil.rect(Rect(
                0f, 0f, width.toFloat(), height.toFloat()
            ), Color.BLACK)
            renderUtil.text("Downloading chromium, please wait.", width / 2 - 200, height / 2 - 5, -1)
        } else if (!isBrowserInitialized() && ChromiumDownloader.app != null) {
            browser = OpenGLBrowser(ChromiumDownloader.app!!, "ascendium://$startingFile")

            browser.client.addRequestHandler(ChromeUIRequestHandler.createHandler(this))
            val window = minecraft.window
            dummyComponent.setSize(window.fbWidth, window.fbHeight)
            browser.wasResized(window.fbWidth, window.fbHeight)
            browser.createBrowser(dummyComponent)
            browser.setFocus(true)
        } else {
            browser.render()

            val awtEvent = MouseEvent(
                dummyComponent,
                MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(),
                AWTUtils.getAwtMods(minecraft.window.windowHandle),
                minecraft.mouse.xPos,
                minecraft.mouse.yPos,
                1,
                false,
                1
            )
            browser.sendMouseEvent(awtEvent)
        }
    }

    override fun init() {
        if (isBrowserInitialized()) {
            val window = minecraft.window
            dummyComponent.setSize(window.fbWidth, window.fbHeight)
            browser.wasResized(window.fbWidth, window.fbHeight)

            browser.setFocus(false)
            browser.setFocus(true)
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if (!isBrowserInitialized()) return
        val mods = AWTUtils.getAwtMods(minecraft.window.windowHandle)
        val event = MouseEvent(
            dummyComponent,
            MouseEvent.MOUSE_PRESSED,
            System.currentTimeMillis(),
            mods,
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            1,
            false,
            AWTUtils.glfwToAwtButton(button)
        )
        browser.sendMouseEvent(event)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        if (!isBrowserInitialized()) return
        val mods = AWTUtils.getAwtMods(minecraft.window.windowHandle)

        val releaseEvent = MouseEvent(
            dummyComponent,
            MouseEvent.MOUSE_RELEASED,
            System.currentTimeMillis(),
            mods,
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            1,
            false,
            AWTUtils.glfwToAwtButton(button)
        )
        browser.sendMouseEvent(releaseEvent)
    }

    override fun mouseScrolled(mouseX: Int, mouseY: Int, scrollX: Double, scrollY: Double) {
        if (!isBrowserInitialized()) return
        val mods = AWTUtils.getAwtMods(minecraft.window.windowHandle)
        val event = MouseWheelEvent(
            dummyComponent,
            MouseEvent.MOUSE_WHEEL,
            System.currentTimeMillis(),
            mods,
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            1,
            false,
            MouseWheelEvent.WHEEL_UNIT_SCROLL,
            1,
            (scrollY * 40).toInt()
        )
        browser.sendMouseWheelEvent(event)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (!isBrowserInitialized()) return
        val mods = AWTUtils.getAwtMods(minecraft.window.windowHandle)
        val event = KeyEvent(
            dummyComponent,
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            mods,
            glfwToAwtKeyCode(keyCode),
            KeyEvent.CHAR_UNDEFINED
        )
        browser.sendKeyEvent(event)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (!isBrowserInitialized()) return
        val mods = AWTUtils.getAwtMods(minecraft.window.windowHandle)
        val event = KeyEvent(
            dummyComponent,
            KeyEvent.KEY_RELEASED,
            System.currentTimeMillis(),
            mods,
            glfwToAwtKeyCode(keyCode),
            KeyEvent.CHAR_UNDEFINED
        )
        browser.sendKeyEvent(event)
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        if (!isBrowserInitialized()) return
        val mods = AWTUtils.getAwtMods(minecraft.window.windowHandle)
        val event = KeyEvent(
            dummyComponent,
            KeyEvent.KEY_TYPED,
            System.currentTimeMillis(),
            mods,
            KeyEvent.VK_UNDEFINED,
            chr
        )
        browser.sendKeyEvent(event)
    }
}

internal object ChromeUIRequestHandler {
    fun createHandler(ui: ChromiumUI): CefRequestHandlerAdapter {
        return AscendiumRequestHandler(ui)
    }
}

internal class AscendiumRequestHandler(val ui: ChromiumUI) : CefRequestHandlerAdapter() {
    fun serve(fileName: String): ByteArray {
        return ui.serve(fileName)
    }

    override fun getResourceRequestHandler(
        browser: CefBrowser?,
        frame: CefFrame,
        request: CefRequest?,
        isNavigation: Boolean,
        isDownload: Boolean,
        requestInitiator: String?,
        disableDefaultHandling: BoolRef?
    ) = request?.url?.takeIf { it.startsWith("ascendium://") }
        ?.let { AscendiumResourceRequestHandler(it, ::serve) }
}

class AscendiumResourceRequestHandler(
    private val url: String,
    private val serve: (String) -> ByteArray
) : CefResourceRequestHandler {

    override fun getCookieAccessFilter(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ) = null

    override fun onBeforeResourceLoad(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ) = false

    override fun getResourceHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ): CefResourceHandler {
        val fileName = url.removePrefix("ascendium://")
        val data = serve(fileName)
        val mime = guessMime(fileName, data)
        return AscendiumResourceHandler(mime, data)
    }

    override fun onResourceRedirect(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        response: CefResponse?,
        new_url: StringRef?
    ) {
    }

    override fun onResourceResponse(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        response: CefResponse?
    ) = false

    override fun onResourceLoadComplete(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        response: CefResponse?,
        status: CefURLRequest.Status?,
        receivedContentLength: Long
    ) {}

    override fun onProtocolExecution(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        allowOsExecution: BoolRef?
    ) {
        allowOsExecution?.set(false)
    }

    private fun guessMime(fileName: String, data: ByteArray): String {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        val byExt = when (ext) {
            "html", "htm" -> "text/html"
            "css" -> "text/css"
            "js" -> "application/javascript"
            "json" -> "application/json"
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "gif" -> "image/gif"
            "svg" -> "image/svg+xml"
            "txt" -> "text/plain"
            else -> null
        }
        if (byExt != null) return byExt

        return URLConnection.guessContentTypeFromStream(ByteArrayInputStream(data))
            ?: "application/octet-stream"
    }
}

class AscendiumResourceHandler(val mime: String, val data: ByteArray) : CefResourceHandler {
    private var stream = ByteArrayInputStream(data)

    override fun processRequest(req: CefRequest?, callback: CefCallback?): Boolean {
        callback?.Continue()
        return true
    }

    override fun getResponseHeaders(response: CefResponse, responseLength: IntRef, redirectUrl: StringRef) {
        response.mimeType = mime
        response.status = 200
        responseLength.set(data.size)
    }

    override fun readResponse(
        dataOut: ByteArray,
        bytesToRead: Int,
        bytesRead: IntRef,
        callback: CefCallback
    ): Boolean {
        val n = stream.read(dataOut, 0, bytesToRead)
        return if (n == -1) {
            bytesRead.set(0)
            false
        } else {
            bytesRead.set(n)
            true
        }
    }

    override fun cancel() {
        stream.close()
    }
}