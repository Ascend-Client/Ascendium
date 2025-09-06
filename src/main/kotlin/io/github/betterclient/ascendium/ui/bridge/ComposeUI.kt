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
import io.github.betterclient.ascendium.bridge.BridgeScreen
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.ui.bridge.compose.AWTUtils
import io.github.betterclient.ascendium.ui.bridge.compose.SkiaRenderer
import io.github.betterclient.ascendium.ui.bridge.compose.glfwToAwtKeyCode
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import kotlin.properties.Delegates

@OptIn(InternalComposeUiApi::class)
class ComposeUI(
    content: @Composable () -> Unit
) : BridgeScreen() {
    private val _content = mutableStateOf(content)
    private val _toast = mutableStateOf<@Composable () -> Unit>(value = { })

    private lateinit var scene: ComposeScene
    private var handle by Delegates.notNull<Long>()

    fun init0() {
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

    val myRenderer = SkiaRenderer()
    override fun render(mouseX: Int, mouseY: Int) {
        handle = minecraft.window.windowHandle
        myRenderer.task {
            init0()
        }

        myRenderer.withSkia {
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }

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

        myRenderer.task {
            scene.sendPointerEvent(
                position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                eventType = PointerEventType.Press,
                nativeEvent = AWTUtils.MouseEvent(
                    minecraft.mouse.xPos,
                    minecraft.mouse.yPos,
                    AWTUtils.getAwtMods(handle),
                    button,
                    MouseEvent.MOUSE_PRESSED
                ),
                button = PointerButton(button)
            )
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        if (!::scene.isInitialized) return

        myRenderer.task {
            scene.sendPointerEvent(
                position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                eventType = PointerEventType.Release,
                nativeEvent = AWTUtils.MouseEvent(
                    minecraft.mouse.xPos,
                    minecraft.mouse.yPos,
                    AWTUtils.getAwtMods(handle),
                    button,
                    MouseEvent.MOUSE_RELEASED
                ),
                button = PointerButton(button)
            )
        }
    }

    override fun mouseScrolled(mouseX: Int, mouseY: Int, scrollX: Double, scrollY: Double) {
        if (!::scene.isInitialized) return

        myRenderer.task {
            scene.sendPointerEvent(
                position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                eventType = PointerEventType.Scroll,
                scrollDelta = Offset(scrollX.toFloat(), -scrollY.toFloat()),
                nativeEvent = AWTUtils.MouseWheelEvent(
                    minecraft.mouse.xPos,
                    minecraft.mouse.yPos,
                    scrollY,
                    AWTUtils.getAwtMods(handle),
                    MouseEvent.MOUSE_WHEEL
                )
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
        _content.value = newContent
    }

    fun toast(content: @Composable () -> Unit) {
        _toast.value = content
    }
}