package io.github.betterclient.ascendium.compose

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
import io.github.betterclient.ascendium.BridgeScreen
import io.github.betterclient.ascendium.minecraft
import java.awt.event.MouseEvent
import java.util.concurrent.CopyOnWriteArrayList
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
        handle = minecraft.window.windowHandle
        if (!::scene.isInitialized) {
            //scene should be created at init, once
            val window = minecraft.window
            val density = Density(window.scale.toFloat().div(2f))
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
            val window = minecraft.window
            val density = Density(window.scale.toFloat().div(2f))
            scene.size = IntSize(window.fbWidth, window.fbHeight)
            scene.density = density
        }
    }

    override fun render(mouseX: Int, mouseY: Int) {
        SkiaRenderer.withSkia {
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }

        renderHandlers.forEach { handler ->
            handler(mouseX, mouseY)
        }

        val event = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(handle),
            0, //NO BUTTON
            MouseEvent.MOUSE_MOVED
        )
        mouseEventHandlers.forEach {
            it(minecraft.mouse.xPos, minecraft.mouse.yPos, event) //this event is going out no matter what, sorry.
        }
        scene.sendPointerEvent(
            position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
            eventType = PointerEventType.Move,
            nativeEvent = event
        )
    }

    //awt events
    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        mouseHandlers.forEach { handler ->
            val composeMouse = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat())
            val mcMouse = Offset(mouseX.toFloat(), mouseY.toFloat())
            if (handler(composeMouse, mcMouse, button, true)) {
                return@forEach //this handler demands that we don't talk about this when talking to compose, ok!
            }
        }

        val event = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(handle),
            button,
            MouseEvent.MOUSE_PRESSED
        )
        mouseEventHandlers.forEach {
            if (it(minecraft.mouse.xPos, minecraft.mouse.yPos, event)) {
                //no event for compose :(
                return
            }
        }
        scene.sendPointerEvent(
            position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
            eventType = PointerEventType.Press,
            nativeEvent = event,
            button = PointerButton(button)
        )
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        mouseHandlers.forEach { handler ->
            val composeMouse = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat())
            val mcMouse = Offset(mouseX.toFloat(), mouseY.toFloat())
            if (handler(composeMouse, mcMouse, button, false)) {
                return //this handler demands that we don't talk about this when talking to compose, ok! (can you notice that I copied and pasted it?)
            }
        }
        val client = minecraft
        val event = AWTUtils.MouseEvent(
            client.mouse.xPos,
            client.mouse.yPos,
            AWTUtils.getAwtMods(handle),
            button,
            MouseEvent.MOUSE_RELEASED
        )
        mouseEventHandlers.forEach {
            if (it(client.mouse.xPos, client.mouse.yPos, event)) {
                if (it(client.mouse.xPos, client.mouse.yPos, AWTUtils.MouseEvent(
                        client.mouse.xPos,
                        client.mouse.yPos,
                        AWTUtils.getAwtMods(handle),
                        button,
                        MouseEvent.MOUSE_CLICKED
                    ))) {
                    return
                }
                return
            }
        }

        scene.sendPointerEvent(
            position = Offset(client.mouse.xPos.toFloat(), client.mouse.yPos.toFloat()),
            eventType = PointerEventType.Release,
            nativeEvent = event,
            button = PointerButton(button)
        )
    }

    override fun mouseScrolled(mouseX: Int, mouseY: Int, scrollX: Double, scrollY: Double) {
        val event = AWTUtils.MouseWheelEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            scrollY,
            AWTUtils.getAwtMods(handle),
            MouseEvent.MOUSE_WHEEL
        )
        mouseEventHandlers.forEach {
            if (it(minecraft.mouse.xPos, minecraft.mouse.yPos, event)) {
                return
            }
        }

        scene.sendPointerEvent(
            position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
            eventType = PointerEventType.Scroll,
            scrollDelta = Offset(scrollX.toFloat(), -scrollY.toFloat()),
            nativeEvent = event
        )
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
        renderHandlers.clear()
        _toast.value = { } //clear toast

        _content.value = newContent
    }

    private val mouseHandlers = CopyOnWriteArrayList<(composeMouse: Offset, mcMouse: Offset, button: Int, clicked: Boolean) -> Boolean>()
    private val renderHandlers = CopyOnWriteArrayList<(mouseX: Int, mouseY: Int) -> Unit>()
    private val mouseEventHandlers = CopyOnWriteArrayList<(mouseX: Int, mouseY: Int, event: MouseEvent) -> Boolean>()

    fun addMouseHandler(handler: (composeMouse: Offset, mcMouse: Offset, button: Int, clicked: Boolean) -> Boolean) {
        mouseHandlers.add(handler)
    }

    fun removeMouseHandler(handler: (Offset, Offset, Int, Boolean) -> Boolean) {
        mouseHandlers.remove(handler)
    }

    fun toast(content: @Composable () -> Unit) {
        _toast.value = content
    }

    fun addRenderHandler(function: (mouseX: Int, mouseY: Int) -> Unit) {
        renderHandlers.add(function)
    }

    fun addMouseEventHandler(function: (mouseX: Int, mouseY: Int, event: MouseEvent) -> Boolean) {
        mouseEventHandlers.add(function)
    }
}