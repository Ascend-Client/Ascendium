package io.github.betterclient.ascendium.ui.chrome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.minecraft
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import org.cef.CefApp
import org.lwjgl.glfw.GLFW.glfwGetWindowPos
import org.lwjgl.system.MemoryStack
import java.awt.Component
import java.awt.Point
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import javax.swing.JPanel

private const val SCROLL_MULTIPLIER = 30f

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BrowserView(browser: Browser, modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(16.dp)) {
    CefApp.getInstance().doMessageLoopWork(1)

    val focusRequester = remember { FocusRequester() }
    val dummyComponent = remember { JPanel() }

    DisposableEffect(Unit) {
        onDispose {
            browser.close()
        }
    }

    browser.bitmap?.let {
        Image(
            modifier = modifier
                .clip(shape)
                .chromiumModifier(dummyComponent, focusRequester),
            contentDescription = null,
            bitmap = it
        )
    }
    if (browser.bitmap == null) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = AscendiumTheme.colorScheme.secondary,
                trackColor = AscendiumTheme.colorScheme.surfaceVariant,
            )
        }
    }

    DisposableEffect(dummyComponent) {
        browser.createBrowser(dummyComponent)
        onDispose { }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.chromiumModifier(dummyComponent: Component, focusRequester: FocusRequester) = this
    .onGloballyPositioned { coordinates ->
    MemoryStack.stackPush().use { stack ->
        val xPos = stack.mallocInt(1)
        val yPos = stack.mallocInt(1)
        glfwGetWindowPos(minecraft.window.windowHandle, xPos, yPos)
        val windowX = xPos.get(0)
        val windowY = yPos.get(0)

        val canvasPos = coordinates.positionInWindow()
        browser.screenPosition = Point(
            windowX + canvasPos.x.toInt(),
            windowY + canvasPos.y.toInt()
        )
    }
}
    .onSizeChanged { size ->
        dummyComponent.setSize(size.width, size.height)
        browser.wasResized(size.width, size.height)
    }
    .onPointerEvent(
        eventType = PointerEventType.Move,
        onEvent = { event ->
            val awtEvent = event.toAwtMouseEvent(MouseEvent.MOUSE_MOVED, dummyComponent)
            browser.sendMouseEvent(awtEvent)
        }
    )
    .onPointerEvent(
        eventType = PointerEventType.Press,
        onEvent = { event ->
            val awtEvent = event.toAwtMouseEvent(MouseEvent.MOUSE_PRESSED, dummyComponent)
            browser.sendMouseEvent(awtEvent)
            focusRequester.requestFocus()
        }
    )
    .onPointerEvent(
        eventType = PointerEventType.Release,
        onEvent = { event ->
            val awtEvent = event.toAwtMouseEvent(MouseEvent.MOUSE_RELEASED, dummyComponent)
            browser.sendMouseEvent(awtEvent)
        }
    )
    .onPointerEvent(
        eventType = PointerEventType.Scroll,
        onEvent = { event ->
            val change = event.changes.first()
            val awtEvent = MouseWheelEvent(
                dummyComponent,
                MouseWheelEvent.MOUSE_WHEEL,
                System.currentTimeMillis(),
                0,
                change.position.x.toInt(),
                change.position.y.toInt(),
                0,
                false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL,
                1,
                (-change.scrollDelta.y * SCROLL_MULTIPLIER).toInt()
            )
            browser.sendMouseWheelEvent(awtEvent)
        }
    )
    .onKeyEvent { keyEvent ->
        val awtEvent = keyEvent.toAwtKeyEvent(dummyComponent)
        browser.sendKeyEvent(awtEvent)
        true
    }
    .focusRequester(focusRequester)
    .onFocusChanged { focusState -> browser.setFocus(focusState.isFocused) }
    .focusable()

@OptIn(ExperimentalComposeUiApi::class)
private fun PointerEvent.toAwtMouseEvent(
    id: Int,
    component: Component
): MouseEvent {
    val change = this.changes.first()
    val awtModifiers = this.keyboardModifiers.toAwtModifiers() or
            this.buttons.toAwtModifiers()

    val awtButton = this.button?.toAwtButton() ?: this.buttons.toAwtButton()

    return MouseEvent(
        component,
        id,
        System.currentTimeMillis(),
        awtModifiers,
        change.position.x.toInt(),
        change.position.y.toInt(),
        1,
        false,
        awtButton
    )
}

private fun PointerButton.toAwtButton(): Int {
    return when (this.index) {
        0 -> MouseEvent.BUTTON1
        1 -> MouseEvent.BUTTON2
        2 -> MouseEvent.BUTTON3
        else -> MouseEvent.NOBUTTON
    }
}

private fun PointerButtons.toAwtButton(): Int {
    if (isPrimaryPressed) return MouseEvent.BUTTON1
    if (isSecondaryPressed) return MouseEvent.BUTTON2
    if (isTertiaryPressed) return MouseEvent.BUTTON3
    return MouseEvent.NOBUTTON
}

private fun PointerKeyboardModifiers.toAwtModifiers(): Int {
    var awtModifiers = 0
    if (isShiftPressed) awtModifiers = awtModifiers or InputEvent.SHIFT_DOWN_MASK
    if (isCtrlPressed) awtModifiers = awtModifiers or InputEvent.CTRL_DOWN_MASK
    if (isAltPressed) awtModifiers = awtModifiers or InputEvent.ALT_DOWN_MASK
    if (isMetaPressed) awtModifiers = awtModifiers or InputEvent.META_DOWN_MASK
    return awtModifiers
}

private fun PointerButtons.toAwtModifiers(): Int {
    var awtModifiers = 0
    if (isPrimaryPressed) awtModifiers = awtModifiers or InputEvent.BUTTON1_DOWN_MASK
    if (isSecondaryPressed) awtModifiers = awtModifiers or InputEvent.BUTTON3_DOWN_MASK
    if (isTertiaryPressed) awtModifiers = awtModifiers or InputEvent.BUTTON2_DOWN_MASK
    return awtModifiers
}

private fun androidx.compose.ui.input.key.KeyEvent.toAwtKeyEvent(
    component: Component
): KeyEvent {
    return KeyEvent(
        component,
        this.type.toAwtId(),
        System.currentTimeMillis(),
        this.awtModifiers,
        this.key.toAwtKeyCode(),
        this.utf16CodePoint.toChar(),
        this.key.toAwtKeyLocation()
    )
}

private fun KeyEventType.toAwtId(): Int = when (this) {
    KeyEventType.KeyDown -> KeyEvent.KEY_PRESSED
    KeyEventType.KeyUp -> KeyEvent.KEY_RELEASED
    KeyEventType.Unknown -> KeyEvent.KEY_TYPED
    else -> KeyEvent.KEY_TYPED
}

private val androidx.compose.ui.input.key.KeyEvent.awtModifiers: Int
    get() {
        var awtModifiers = 0
        if (isShiftPressed) awtModifiers = awtModifiers or InputEvent.SHIFT_DOWN_MASK
        if (isCtrlPressed) awtModifiers = awtModifiers or InputEvent.CTRL_DOWN_MASK
        if (isAltPressed) awtModifiers = awtModifiers or InputEvent.ALT_DOWN_MASK
        if (isMetaPressed) awtModifiers = awtModifiers or InputEvent.META_DOWN_MASK
        return awtModifiers
    }

private fun Key.toAwtKeyCode(): Int {
    return this.nativeKeyCode
}

private fun Key.toAwtKeyLocation(): Int {
    return this.nativeKeyLocation
}