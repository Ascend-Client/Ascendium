package io.github.betterclient.ascendium.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.github.betterclient.ascendium.Bridge
import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.BridgeScreen
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.min
import kotlin.properties.Delegates
import java.awt.event.KeyEvent as AwtKeyEvent

@OptIn(InternalComposeUiApi::class)
open class ComposeUI(
    internal val content: @Composable () -> Unit
) : BridgeScreen() {
    private val _content = mutableStateOf<@Composable () -> Unit>(value = {
        content()
    })
    private val _toast = mutableStateOf<@Composable () -> Unit>(value = { })

    private lateinit var scene: ComposeScene
    private var handle by Delegates.notNull<Long>()

    companion object {
        lateinit var current: ComposeUI
    }

    override fun init() {
        handle = Bridge.client.window.windowHandle
        if (!::scene.isInitialized) {
            //scene should be created at init, once
            val window = Bridge.client.window
            val density = Density(min(window.scale.toFloat().div(2), 1f))
            scene = CanvasLayersComposeScene(
                density = density,
                size = IntSize(window.fbWidth, window.fbHeight),
                invalidate = {/*Minecraft should schedule?*/}
            )
            current = this

            scene.setContent {
                //for switching
                val content = _content.value

                Box {
                    content()
                }

                Box {
                    _toast.value()
                }
            }
        } else {
            //scene already initalized, just update size
            val window = Bridge.client.window
            val density = Density(min(window.scale.toFloat().div(2), 1f))
            scene.size = IntSize(window.fbWidth, window.fbHeight)
            scene.density = density
        }

        SkiaRenderer.init()
    }

    override fun render(renderer: BridgeRenderer, mouseX: Int, mouseY: Int) {
        SkiaRenderer.withSkia {
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }

        val client = Bridge.client
        scene.sendPointerEvent(
            position = Offset(client.mouse.xPos.toFloat(), client.mouse.yPos.toFloat()),
            eventType = PointerEventType.Move,
            nativeEvent = AWTUtils.MouseEvent(AWTUtils.getAwtMods(handle), 0 /*NO_BUTTON*/)
        )
    }

    //awt events
    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        mouseHandlers.forEach { handler ->
            val composeMouse = Offset(Bridge.client.mouse.xPos.toFloat(), Bridge.client.mouse.yPos.toFloat())
            val mcMouse = Offset(mouseX.toFloat(), mouseY.toFloat())
            if (handler(composeMouse, mcMouse, button, true)) {
                return@forEach //this handler demands that we don't talk about this when talking to compose, ok!
            }
        }

        val client = Bridge.client
        scene.sendPointerEvent(
            position = Offset(client.mouse.xPos.toFloat(), client.mouse.yPos.toFloat()),
            eventType = PointerEventType.Press,
            nativeEvent = AWTUtils.MouseEvent(AWTUtils.getAwtMods(handle), button)
        )
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        mouseHandlers.forEach { handler ->
            val composeMouse = Offset(Bridge.client.mouse.xPos.toFloat(), Bridge.client.mouse.yPos.toFloat())
            val mcMouse = Offset(mouseX.toFloat(), mouseY.toFloat())
            if (handler(composeMouse, mcMouse, button, false)) {
                return@forEach //this handler demands that we don't talk about this when talking to compose, ok! (can you notice that I copied and pasted it?)
            }
        }

        val client = Bridge.client
        scene.sendPointerEvent(
            position = Offset(client.mouse.xPos.toFloat(), client.mouse.yPos.toFloat()),
            eventType = PointerEventType.Release,
            nativeEvent = AWTUtils.MouseEvent(AWTUtils.getAwtMods(handle), button)
        )
    }

    override fun mouseScrolled(mouseX: Int, mouseY: Int, scrollX: Double, scrollY: Double) {
        val client = Bridge.client
        scene.sendPointerEvent(
            position = Offset(client.mouse.xPos.toFloat(), client.mouse.yPos.toFloat()),
            eventType = PointerEventType.Scroll,
            scrollDelta = Offset(scrollX.toFloat(), -scrollY.toFloat()),
            nativeEvent = AWTUtils.MouseWheelEvent(AWTUtils.getAwtMods(handle))
        )
        super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) {
        val awtKey = glfwToAwtKeyCode(keyCode)
        val time = System.nanoTime() / 1_000_000

        scene.sendKeyEvent(
            AWTUtils.KeyEvent(
                AwtKeyEvent.KEY_PRESSED,
                time,
                AWTUtils.getAwtMods(handle),
                awtKey,
                0.toChar(),
                AwtKeyEvent.KEY_LOCATION_STANDARD
            )
        )
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        val awtKey = glfwToAwtKeyCode(keyCode)
        val time = System.nanoTime() / 1_000_000

        scene.sendKeyEvent(
            AWTUtils.KeyEvent(
                AwtKeyEvent.KEY_RELEASED,
                time,
                AWTUtils.getAwtMods(handle),
                awtKey,
                0.toChar(),
                AwtKeyEvent.KEY_LOCATION_STANDARD
            )
        )
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        val time = System.nanoTime() / 1_000_000
        scene.sendKeyEvent(
            AWTUtils.KeyEvent(
                AwtKeyEvent.KEY_TYPED,
                time,
                AWTUtils.getAwtMods(handle),
                Key.Unknown.keyCode.toInt(),
                chr,
                AwtKeyEvent.KEY_LOCATION_UNKNOWN
            )
        )
    }

    fun switchTo(newContent: @Composable () -> Unit) {
        mouseHandlers.clear()

        _content.value = newContent
    }

    private val mouseHandlers = CopyOnWriteArrayList<(composeMouse: Offset, mcMouse: Offset, button: Int, clicked: Boolean) -> Boolean>()

    fun addMouseHandler(handler: (composeMouse: Offset, mcMouse: Offset, button: Int, clicked: Boolean) -> Boolean) {
        mouseHandlers.add(handler)
    }

    fun removeMouseHandler(handler: (Offset, Offset, Int, Boolean) -> Boolean) {
        mouseHandlers.remove(handler)
    }

    fun toast(content: @Composable () -> Unit) {
        _toast.value = content
    }
}