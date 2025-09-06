package io.github.betterclient.ascendium.ui.bridge

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.bridge.BridgeScreen
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.ui.bridge.compose.AWTUtils
import java.awt.event.MouseEvent
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList

//UI that can switch between Chromium - Compose on request.
open class DynamicUI(
    private var composable: @Composable () -> Unit,
    private var server: (fileName: String) -> ByteArray
) : BridgeScreen() {
    private var currentUI: BridgeScreen
    var activeBackend: UIBackend

    private val mouseHandlers = CopyOnWriteArrayList<(composeMouse: Offset, mcMouse: Offset, button: Int, clicked: Boolean) -> Boolean>()
    private val renderHandlers = CopyOnWriteArrayList<(mouseX: Int, mouseY: Int) -> Unit>()
    private val mouseEventHandlers = CopyOnWriteArrayList<(mouseX: Int, mouseY: Int, event: MouseEvent) -> Boolean>()

    companion object {
        lateinit var current: DynamicUI
        fun isInitialized() = ::current.isInitialized
        private val tasks = ConcurrentLinkedQueue<() -> Unit>()
    }

    init {
        activeBackend = if (Ascendium.settings._ui.value == "Compose") {
            UIBackend.COMPOSE
        } else {
            UIBackend.CHROMIUM
        }

        currentUI = createUIBackend(activeBackend)
    }

    private fun createUIBackend(backend: UIBackend): BridgeScreen {
        return when (backend) {
            UIBackend.COMPOSE -> ComposeUI(composable)
            UIBackend.CHROMIUM -> object : ChromiumUI("index.html") {
                override fun serve(fileName: String) = server(fileName)
            }
        }
    }

    override fun init() {
        current = this

        //reload ui, maybe its using (other ui library) now
        activeBackend = if (Ascendium.settings._ui.value == "Compose") {
            UIBackend.COMPOSE
        } else {
            UIBackend.CHROMIUM
        }
        when (activeBackend) {
            UIBackend.COMPOSE -> if (currentUI is ChromiumUI) {
                currentUI = createUIBackend(activeBackend)
            }
            UIBackend.CHROMIUM -> if (currentUI is ComposeUI) {
                currentUI = createUIBackend(activeBackend)
            }
        }

        currentUI.init()
    }

    //Sorry, you have to provide both.
    fun switchTo(server: (String) -> ByteArray, composable: @Composable () -> Unit) {
        mouseHandlers.clear()
        renderHandlers.clear()
        mouseEventHandlers.clear()

        when (activeBackend) {
            UIBackend.COMPOSE -> {
                this.composable = composable
                (currentUI as ComposeUI).switchTo(composable)
            }
            UIBackend.CHROMIUM -> {
                this.server = server
                currentUI = createUIBackend(activeBackend)
                currentUI.init()
            }
        }
    }

    fun onRenderThread(task: () -> Unit) {
        tasks.add(task)
    }

    fun addMouseHandler(handler: (composeMouse: Offset, mcMouse: Offset, button: Int, clicked: Boolean) -> Boolean) {
        mouseHandlers.add(handler)
    }

    /**
     * Only available on compose
     * Will not do anything on chromium
     */
    fun toast(content: @Composable () -> Unit) {
        (currentUI as? ComposeUI)?.toast(content)
    }

    fun addRenderHandler(function: (mouseX: Int, mouseY: Int) -> Unit) {
        renderHandlers.add(function)
    }

    fun addMouseEventHandler(function: (mouseX: Int, mouseY: Int, event: MouseEvent) -> Boolean) {
        mouseEventHandlers.add(function)
    }

    override fun render(mouseX: Int, mouseY: Int) {
        while (true) {
            val item = tasks.poll() ?: break
            item()
        }

        renderHandlers.forEach { it(mouseX, mouseY) }

        val handle = minecraft.window.windowHandle
        val moveEvent = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(handle),
            0, //NO BUTTON
            MouseEvent.MOUSE_MOVED
        )
        mouseEventHandlers.forEach { it(minecraft.mouse.xPos, minecraft.mouse.yPos, moveEvent) }

        currentUI.render(mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        val composeMouse = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat())
        val mcMouse = Offset(mouseX.toFloat(), mouseY.toFloat())

        if (mouseHandlers.any { it(composeMouse, mcMouse, button, true) }) {
            return
        }

        val handle = minecraft.window.windowHandle
        val pressEvent = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(handle),
            button,
            MouseEvent.MOUSE_PRESSED
        )
        if (mouseEventHandlers.any { it(minecraft.mouse.xPos, minecraft.mouse.yPos, pressEvent) }) {
            return
        }

        currentUI.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        val composeMouse = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat())
        val mcMouse = Offset(mouseX.toFloat(), mouseY.toFloat())

        if (mouseHandlers.any { it(composeMouse, mcMouse, button, false) }) {
            return
        }

        val handle = minecraft.window.windowHandle
        val client = minecraft
        val releaseEvent = AWTUtils.MouseEvent(
            client.mouse.xPos,
            client.mouse.yPos,
            AWTUtils.getAwtMods(handle),
            button,
            MouseEvent.MOUSE_RELEASED
        )
        val clickEvent = AWTUtils.MouseEvent(
            client.mouse.xPos,
            client.mouse.yPos,
            AWTUtils.getAwtMods(handle),
            button,
            MouseEvent.MOUSE_CLICKED
        )

        if (mouseEventHandlers.any { handler ->
                if (handler(client.mouse.xPos, client.mouse.yPos, releaseEvent)) {
                    handler(client.mouse.xPos, client.mouse.yPos, clickEvent)
                    true //yum
                } else false
            }) {
            return
        }

        currentUI.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Int, mouseY: Int, scrollX: Double, scrollY: Double) {
        val handle = minecraft.window.windowHandle
        val scrollEvent = AWTUtils.MouseWheelEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            scrollY,
            AWTUtils.getAwtMods(handle),
            MouseEvent.MOUSE_WHEEL
        )
        if (mouseEventHandlers.any { it(minecraft.mouse.xPos, minecraft.mouse.yPos, scrollEvent) }) {
            return
        }

        currentUI.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = currentUI.keyPressed(keyCode, scanCode, modifiers)
    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) = currentUI.keyReleased(keyCode, scanCode, modifiers)
    override fun charTyped(chr: Char, modifiers: Int) = currentUI.charTyped(chr, modifiers)
    override fun close() = currentUI.close()
}

enum class UIBackend {
    COMPOSE, CHROMIUM
}