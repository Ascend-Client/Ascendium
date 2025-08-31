package io.github.betterclient.ascendium.module.impl.notifications

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import io.github.betterclient.ascendium.MCScreen
import io.github.betterclient.ascendium.compose.AWTUtils
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.compose.glfwToAwtKeyCode
import io.github.betterclient.ascendium.event.*
import io.github.betterclient.ascendium.minecraft
import io.github.betterclient.ascendium.module.impl.notifications.Notifications.multipleNotifications
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

object NotificationEvents {
    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onRender(event: RenderHudEvent) {
        multipleNotifications = minecraft.screen == MCScreen.CHAT_SCREEN
        NotificationScene.init()
        SkiaRenderer.withSkia {
            NotificationScene.scene.render(it.asComposeCanvas(), System.nanoTime())
        }
    }

    @EventTarget
    fun onChat(chatEvent: ChatEvent) {
        //TODO: server notifications
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onScreenRender(event: RenderScreenEvent) {
        if (minecraft.isWorldNull) return
        NotificationScene.init()
        SkiaRenderer.withSkia {
            NotificationScene.scene.render(it.asComposeCanvas(), System.nanoTime())
        }

        val event = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(minecraft.window.windowHandle),
            0, //NO BUTTON
            MouseEvent.MOUSE_MOVED
        )
        NotificationScene.scene.sendPointerEvent(
            position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
            eventType = PointerEventType.Move,
            nativeEvent = event
        )
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onScreenMouse(event: MouseScreenEvent) {
        if (minecraft.isWorldNull) return
        NotificationScene.init()
        val event0 = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(minecraft.window.windowHandle),
            event.button,
            if (event.pressed) MouseEvent.MOUSE_PRESSED else MouseEvent.MOUSE_RELEASED
        )

        NotificationScene.scene.sendPointerEvent(
            position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
            eventType = if (event.pressed) PointerEventType.Press else PointerEventType.Release,
            nativeEvent = event0,
            button = PointerButton(event.button)
        )
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onScreenScroll(event: MouseScrollScreenEvent) {
        if (minecraft.isWorldNull) return
        NotificationScene.init()
        val event0 = AWTUtils.MouseWheelEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            event.scrollY,
            AWTUtils.getAwtMods(minecraft.window.windowHandle),
            MouseEvent.MOUSE_WHEEL
        )

        NotificationScene.scene.sendPointerEvent(
            position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
            eventType = PointerEventType.Scroll,
            scrollDelta = Offset(event.scrollX.toFloat(), -event.scrollY.toFloat()),
            nativeEvent = event0
        )
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onKeyboardScreen(event: KeyboardScreenEvent) {
        if (minecraft.isWorldNull) return
        NotificationScene.init()
        val awtKey = glfwToAwtKeyCode(event.key)
        val time = System.nanoTime() / 1_000_000

        NotificationScene.scene.sendKeyEvent(
            AWTUtils.KeyEvent(
                if (event.pressed) KeyEvent.KEY_PRESSED else KeyEvent.KEY_RELEASED,
                time,
                AWTUtils.getAwtMods(minecraft.window.windowHandle),
                awtKey,
                0.toChar(),
                KeyEvent.KEY_LOCATION_STANDARD
            )
        )
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onKeyboardChar(event: KeyboardCharScreenEvent) {
        if (minecraft.isWorldNull) return
        NotificationScene.init()
        val time = System.nanoTime() / 1_000_000
        NotificationScene.scene.sendKeyEvent(
            AWTUtils.KeyEvent(
                KeyEvent.KEY_TYPED,
                time,
                AWTUtils.getAwtMods(minecraft.window.windowHandle),
                Key.Unknown.keyCode.toInt(),
                event.char,
                KeyEvent.KEY_LOCATION_UNKNOWN
            )
        )
    }
}