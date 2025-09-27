package io.github.betterclient.ascendium.ui.bridge

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.bridge.BridgeScreen
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.module.ComposableHUDModule
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.properties.Delegates

@OptIn(InternalComposeUiApi::class)
open class ComposeUI(
    content: @Composable () -> Unit
) : BridgeScreen() {
    private val _content = mutableStateOf(content)
    private val _toast = mutableStateOf<@Composable () -> Unit>(value = { })

    private lateinit var scene: ComposeScene
    private var handle by Delegates.notNull<Long>()

    companion object {
        lateinit var current: ComposeUI
        private val tasks = ConcurrentLinkedQueue<() -> Unit>()

        var myRenderer = SkiaRenderer() //reuse
    }

    fun init0() {
        if (Ascendium.settings.uiBackend != "Compose" && myRenderer.adapter !is OffscreenSkiaRenderer) {
            myRenderer = SkiaRenderer()
        }

        current = this
        handle = minecraft.window.windowHandle
        if (!::scene.isInitialized) {
            val window = minecraft.window
            val density = Density(window.scale.toFloat().div(2f))
            scene = CanvasLayersComposeScene(
                density = density,
                size = IntSize(window.fbWidth, window.fbHeight),
                invalidate = { /* Minecraft should schedule? */ }
            )

            scene.setContent {
                val currentContent = _content.value
                Box {
                    currentContent()
                }
                Box {
                    _toast.value()
                }
            }
        } else if (scene.size != IntSize(minecraft.window.fbWidth, minecraft.window.fbHeight)) {
            val window = minecraft.window
            val density = Density(window.scale.toFloat().div(2f))
            scene.size = IntSize(window.fbWidth, window.fbHeight)
            scene.density = density
        }
    }

    override fun init() {
        myRenderer.task {
            init0()
        }
    }

    override fun render(mouseX: Int, mouseY: Int) {
        while (true) {
            val item = tasks.poll() ?: break
            item()
        }

        handle = minecraft.window.windowHandle
        myRenderer.task {
            init0()
        }

        myRenderer.withSkia {
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }

        renderHandlers.forEach { it(mouseX, mouseY) }

        myRenderer.task {
            scene.sendPointerEvent(
                position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                eventType = PointerEventType.Move,
                nativeEvent = AWTUtils.MouseEvent(
                    minecraft.mouse.xPos,
                    minecraft.mouse.yPos,
                    AWTUtils.getAwtMods(handle),
                    0, //NO BUTTON
                    MouseEvent.MOUSE_MOVED
                )
            )
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if (!::scene.isInitialized) return

        val event = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(handle),
            button,
            MouseEvent.MOUSE_PRESSED
        )

        val bye = mouseHandlers.any {
            it(
                Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                Offset(mouseX.toFloat(), mouseY.toFloat()),
                button, true, event
            )
        }
        if (bye) return

        myRenderer.task {
            scene.sendPointerEvent(
                position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                eventType = PointerEventType.Press,
                nativeEvent = event,
                button = PointerButton(button)
            )
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        if (!::scene.isInitialized) return

        val event = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(handle),
            button,
            MouseEvent.MOUSE_RELEASED
        )

        val bye = mouseHandlers.any {
            it(
                Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                Offset(mouseX.toFloat(), mouseY.toFloat()),
                button, false, event
            )
        }
        if (bye) return

        myRenderer.task {
            scene.sendPointerEvent(
                position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                eventType = PointerEventType.Release,
                nativeEvent = event,
                button = PointerButton(button)
            )
        }
    }

    override fun mouseScrolled(mouseX: Int, mouseY: Int, scrollX: Double, scrollY: Double) {
        if (!::scene.isInitialized) return

        val event = AWTUtils.MouseWheelEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            scrollY,
            AWTUtils.getAwtMods(handle),
            MouseEvent.MOUSE_WHEEL
        )

        val bye = mouseHandlers.any {
            it(
                Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                Offset(mouseX.toFloat(), mouseY.toFloat()),
                -1, false, event
            )
        }
        if (bye) return

        myRenderer.task {
            scene.sendPointerEvent(
                position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                eventType = PointerEventType.Scroll,
                scrollDelta = Offset(scrollX.toFloat(), -scrollY.toFloat()),
                nativeEvent = event
            )
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (!::scene.isInitialized) return

        val awtKey = glfwToAwtKeyCode(keyCode)
        val time = System.nanoTime() / 1_000_000

        myRenderer.task {
            scene.sendKeyEvent(
                AWTUtils.KeyEvent(
                    KeyEvent.KEY_PRESSED,
                    time,
                    AWTUtils.getAwtMods(handle),
                    awtKey,
                    0.toChar(),
                    KeyEvent.KEY_LOCATION_STANDARD
                )
            )
        }
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (!::scene.isInitialized) return

        val awtKey = glfwToAwtKeyCode(keyCode)
        val time = System.nanoTime() / 1_000_000

        myRenderer.task {
            scene.sendKeyEvent(
                AWTUtils.KeyEvent(
                    KeyEvent.KEY_RELEASED,
                    time,
                    AWTUtils.getAwtMods(handle),
                    awtKey,
                    0.toChar(),
                    KeyEvent.KEY_LOCATION_STANDARD
                )
            )
        }
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        if (!::scene.isInitialized) return

        val time = System.nanoTime() / 1_000_000
        myRenderer.task {
            scene.sendKeyEvent(
                AWTUtils.KeyEvent(
                    KeyEvent.KEY_TYPED,
                    time,
                    AWTUtils.getAwtMods(handle),
                    Key.Unknown.keyCode.toInt(),
                    chr,
                    KeyEvent.KEY_LOCATION_UNKNOWN
                )
            )
        }
    }

    fun switchTo(newContent: @Composable () -> Unit) {
        _toast.value = { } //clear toast on switch
        renderHandlers.clear()
        mouseHandlers.clear()

        _content.value = newContent
    }

    fun toast(content: @Composable () -> Unit) {
        _toast.value = content
    }

    fun onRenderThread(function: () -> Unit) {
        tasks.add(function)
    }

    val mouseHandlers = CopyOnWriteArrayList<(Offset, Offset, Int, Boolean, MouseEvent) -> Boolean>()
    val renderHandlers = CopyOnWriteArrayList<(Int, Int) -> Unit>()

    fun addMouseEventHandler(function: (x: Int, y: Int, event: MouseEvent) -> Boolean) {
        mouseHandlers.add { _, a, _, _, e ->
            return@add function(a.x.toInt(), a.y.toInt(), e)
        }
    }

    fun addMouseHandler(function: (composeCoords: Offset, mcMouse: Offset, button: Int, clicked: Boolean) -> Boolean) {
        mouseHandlers.add { a, b, c, d, e ->
            if (e is MouseWheelEvent) return@add false //not capturing

            return@add function(a, b, c, d)
        }
    }

    fun addRenderHandler(function: (Int, Int) -> Unit) {
        renderHandlers.add(function)
    }
}