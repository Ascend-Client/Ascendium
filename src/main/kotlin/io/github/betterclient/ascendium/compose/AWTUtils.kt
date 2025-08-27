package io.github.betterclient.ascendium.compose

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import io.github.betterclient.ascendium.minecraft
import org.lwjgl.glfw.GLFW.*
import java.awt.Component
import java.awt.event.InputEvent
import java.awt.event.KeyEvent as AwtKeyEvent
import java.awt.event.MouseWheelEvent

//from https://github.com/JetBrains/compose-multiplatform/blob/master/experimental/lwjgl-integration/src/main/kotlin/GlfwEvents.kt
internal object AWTUtils {
    val awtComponent = object : Component() { }

    fun getAwtMods(windowHandle: Long): Int {
        var awtMods = 0
        if (glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS)
            awtMods = awtMods or InputEvent.BUTTON1_DOWN_MASK
        if (glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_2) == GLFW_PRESS)
            awtMods = awtMods or InputEvent.BUTTON2_DOWN_MASK
        if (glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_3) == GLFW_PRESS)
            awtMods = awtMods or InputEvent.BUTTON3_DOWN_MASK
        if (glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_4) == GLFW_PRESS)
            awtMods = awtMods or (1 shl 14)
        if (glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_5) == GLFW_PRESS)
            awtMods = awtMods or (1 shl 15)
        if (glfwGetKey(windowHandle, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS)
            awtMods = awtMods or InputEvent.CTRL_DOWN_MASK
        if (glfwGetKey(windowHandle, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS)
            awtMods = awtMods or InputEvent.SHIFT_DOWN_MASK
        if (glfwGetKey(windowHandle, GLFW_KEY_LEFT_ALT) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_RIGHT_ALT) == GLFW_PRESS)
            awtMods = awtMods or InputEvent.ALT_DOWN_MASK
        return awtMods
    }

    fun glfwToAwtButton(glfwButton: Int): Int = when (glfwButton) {
        GLFW_MOUSE_BUTTON_1 -> java.awt.event.MouseEvent.BUTTON1
        GLFW_MOUSE_BUTTON_2 -> java.awt.event.MouseEvent.BUTTON2
        GLFW_MOUSE_BUTTON_3 -> java.awt.event.MouseEvent.BUTTON3
        else -> java.awt.event.MouseEvent.BUTTON1
    }

    fun MouseEvent(mouseX: Int, mouseY: Int, awtMods: Int, button: Int, eventType: Int) = java.awt.event.MouseEvent(
        awtComponent, eventType, System.currentTimeMillis(), awtMods, mouseX, mouseY, 1, false, glfwToAwtButton(button)
    )

    fun MouseWheelEvent(x: Int, y: Int, scrollY: Double, awtMods: Int, eventType: Int) =
        MouseWheelEvent(
            awtComponent,
            eventType, System.currentTimeMillis(),
            awtMods,
            x,
            y,
            0,
            false,
            MouseWheelEvent.WHEEL_UNIT_SCROLL,
            1,
            (-scrollY).toInt()
        )

    private fun isCtrlPressed(windowHandle: Long): Boolean {
        return glfwGetKey(windowHandle, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS;
    }

    private fun isShiftPressed(windowHandle: Long): Boolean {
        return glfwGetKey(windowHandle, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS;
    }

    private fun isAltPressed(windowHandle: Long): Boolean {
        return glfwGetKey(windowHandle, GLFW_KEY_LEFT_ALT) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_RIGHT_ALT) == GLFW_PRESS;
    }

    @OptIn(InternalComposeUiApi::class)
    //f*ck this method
    fun KeyEvent(awtId: Int, time: Long, awtMods: Int, key: Int, char: Char, location: Int): KeyEvent {
        val handle = minecraft.window.windowHandle
        return KeyEvent(
            key = Key(key, location),
            type = when (awtId) {
                AwtKeyEvent.KEY_PRESSED -> KeyEventType.KeyDown
                AwtKeyEvent.KEY_RELEASED -> KeyEventType.KeyUp
                else -> KeyEventType.Unknown
            },
            codePoint = char.code,
            nativeEvent = AwtKeyEvent(awtComponent, awtId, time, awtMods, key, char, location),
            isCtrlPressed = isCtrlPressed(handle),
            isAltPressed = isAltPressed(handle),
            isShiftPressed = isShiftPressed(handle)
        )
    }
}

fun glfwToAwtKeyCode(glfwKeyCode: Int): Int = when (glfwKeyCode) {
    GLFW_KEY_SPACE -> AwtKeyEvent.VK_SPACE
    GLFW_KEY_APOSTROPHE -> AwtKeyEvent.VK_QUOTE
    GLFW_KEY_COMMA -> AwtKeyEvent.VK_COMMA
    GLFW_KEY_MINUS -> AwtKeyEvent.VK_MINUS
    GLFW_KEY_PERIOD -> AwtKeyEvent.VK_PERIOD
    GLFW_KEY_SLASH -> AwtKeyEvent.VK_SLASH
    GLFW_KEY_0 -> AwtKeyEvent.VK_0
    GLFW_KEY_1 -> AwtKeyEvent.VK_1
    GLFW_KEY_2 -> AwtKeyEvent.VK_2
    GLFW_KEY_3 -> AwtKeyEvent.VK_3
    GLFW_KEY_4 -> AwtKeyEvent.VK_4
    GLFW_KEY_5 -> AwtKeyEvent.VK_5
    GLFW_KEY_6 -> AwtKeyEvent.VK_6
    GLFW_KEY_7 -> AwtKeyEvent.VK_7
    GLFW_KEY_8 -> AwtKeyEvent.VK_8
    GLFW_KEY_9 -> AwtKeyEvent.VK_9
    GLFW_KEY_SEMICOLON -> AwtKeyEvent.VK_SEMICOLON
    GLFW_KEY_EQUAL -> AwtKeyEvent.VK_EQUALS
    GLFW_KEY_A -> AwtKeyEvent.VK_A
    GLFW_KEY_B -> AwtKeyEvent.VK_B
    GLFW_KEY_C -> AwtKeyEvent.VK_C
    GLFW_KEY_D -> AwtKeyEvent.VK_D
    GLFW_KEY_E -> AwtKeyEvent.VK_E
    GLFW_KEY_F -> AwtKeyEvent.VK_F
    GLFW_KEY_G -> AwtKeyEvent.VK_G
    GLFW_KEY_H -> AwtKeyEvent.VK_H
    GLFW_KEY_I -> AwtKeyEvent.VK_I
    GLFW_KEY_J -> AwtKeyEvent.VK_J
    GLFW_KEY_K -> AwtKeyEvent.VK_K
    GLFW_KEY_L -> AwtKeyEvent.VK_L
    GLFW_KEY_M -> AwtKeyEvent.VK_M
    GLFW_KEY_N -> AwtKeyEvent.VK_N
    GLFW_KEY_O -> AwtKeyEvent.VK_O
    GLFW_KEY_P -> AwtKeyEvent.VK_P
    GLFW_KEY_Q -> AwtKeyEvent.VK_Q
    GLFW_KEY_R -> AwtKeyEvent.VK_R
    GLFW_KEY_S -> AwtKeyEvent.VK_S
    GLFW_KEY_T -> AwtKeyEvent.VK_T
    GLFW_KEY_U -> AwtKeyEvent.VK_U
    GLFW_KEY_V -> AwtKeyEvent.VK_V
    GLFW_KEY_W -> AwtKeyEvent.VK_W
    GLFW_KEY_X -> AwtKeyEvent.VK_X
    GLFW_KEY_Y -> AwtKeyEvent.VK_Y
    GLFW_KEY_Z -> AwtKeyEvent.VK_Z
    GLFW_KEY_LEFT_BRACKET -> AwtKeyEvent.VK_OPEN_BRACKET
    GLFW_KEY_BACKSLASH -> AwtKeyEvent.VK_BACK_SLASH
    GLFW_KEY_RIGHT_BRACKET -> AwtKeyEvent.VK_CLOSE_BRACKET
    GLFW_KEY_GRAVE_ACCENT -> AwtKeyEvent.VK_BACK_QUOTE
    GLFW_KEY_ESCAPE -> AwtKeyEvent.VK_ESCAPE
    GLFW_KEY_ENTER -> AwtKeyEvent.VK_ENTER
    GLFW_KEY_TAB -> AwtKeyEvent.VK_TAB
    GLFW_KEY_BACKSPACE -> AwtKeyEvent.VK_BACK_SPACE
    GLFW_KEY_INSERT -> AwtKeyEvent.VK_INSERT
    GLFW_KEY_DELETE -> AwtKeyEvent.VK_DELETE
    GLFW_KEY_RIGHT -> AwtKeyEvent.VK_RIGHT
    GLFW_KEY_LEFT -> AwtKeyEvent.VK_LEFT
    GLFW_KEY_DOWN -> AwtKeyEvent.VK_DOWN
    GLFW_KEY_UP -> AwtKeyEvent.VK_UP
    GLFW_KEY_PAGE_UP -> AwtKeyEvent.VK_PAGE_UP
    GLFW_KEY_PAGE_DOWN -> AwtKeyEvent.VK_PAGE_DOWN
    GLFW_KEY_HOME -> AwtKeyEvent.VK_HOME
    GLFW_KEY_END -> AwtKeyEvent.VK_END
    GLFW_KEY_CAPS_LOCK -> AwtKeyEvent.VK_CAPS_LOCK
    GLFW_KEY_SCROLL_LOCK -> AwtKeyEvent.VK_SCROLL_LOCK
    GLFW_KEY_NUM_LOCK -> AwtKeyEvent.VK_NUM_LOCK
    GLFW_KEY_PRINT_SCREEN -> AwtKeyEvent.VK_PRINTSCREEN
    GLFW_KEY_PAUSE -> AwtKeyEvent.VK_PAUSE
    GLFW_KEY_F1 -> AwtKeyEvent.VK_F1
    GLFW_KEY_F2 -> AwtKeyEvent.VK_F2
    GLFW_KEY_F3 -> AwtKeyEvent.VK_F3
    GLFW_KEY_F4 -> AwtKeyEvent.VK_F4
    GLFW_KEY_F5 -> AwtKeyEvent.VK_F5
    GLFW_KEY_F6 -> AwtKeyEvent.VK_F6
    GLFW_KEY_F7 -> AwtKeyEvent.VK_F7
    GLFW_KEY_F8 -> AwtKeyEvent.VK_F8
    GLFW_KEY_F9 -> AwtKeyEvent.VK_F9
    GLFW_KEY_F10 -> AwtKeyEvent.VK_F10
    GLFW_KEY_F11 -> AwtKeyEvent.VK_F11
    GLFW_KEY_F12 -> AwtKeyEvent.VK_F12
    GLFW_KEY_F13 -> AwtKeyEvent.VK_F13
    GLFW_KEY_F14 -> AwtKeyEvent.VK_F14
    GLFW_KEY_F15 -> AwtKeyEvent.VK_F15
    GLFW_KEY_F16 -> AwtKeyEvent.VK_F16
    GLFW_KEY_F17 -> AwtKeyEvent.VK_F17
    GLFW_KEY_F18 -> AwtKeyEvent.VK_F18
    GLFW_KEY_F19 -> AwtKeyEvent.VK_F19
    GLFW_KEY_F20 -> AwtKeyEvent.VK_F20
    GLFW_KEY_F21 -> AwtKeyEvent.VK_F21
    GLFW_KEY_F22 -> AwtKeyEvent.VK_F22
    GLFW_KEY_F23 -> AwtKeyEvent.VK_F23
    GLFW_KEY_F24 -> AwtKeyEvent.VK_F24
    GLFW_KEY_KP_0 -> AwtKeyEvent.VK_NUMPAD0
    GLFW_KEY_KP_1 -> AwtKeyEvent.VK_NUMPAD1
    GLFW_KEY_KP_2 -> AwtKeyEvent.VK_NUMPAD2
    GLFW_KEY_KP_3 -> AwtKeyEvent.VK_NUMPAD3
    GLFW_KEY_KP_4 -> AwtKeyEvent.VK_NUMPAD4
    GLFW_KEY_KP_5 -> AwtKeyEvent.VK_NUMPAD5
    GLFW_KEY_KP_6 -> AwtKeyEvent.VK_NUMPAD6
    GLFW_KEY_KP_7 -> AwtKeyEvent.VK_NUMPAD7
    GLFW_KEY_KP_8 -> AwtKeyEvent.VK_NUMPAD8
    GLFW_KEY_KP_9 -> AwtKeyEvent.VK_NUMPAD9
    GLFW_KEY_LEFT_SHIFT -> AwtKeyEvent.VK_SHIFT
    GLFW_KEY_LEFT_CONTROL -> AwtKeyEvent.VK_CONTROL
    GLFW_KEY_LEFT_ALT -> AwtKeyEvent.VK_ALT
    GLFW_KEY_RIGHT_SHIFT -> AwtKeyEvent.VK_SHIFT
    GLFW_KEY_RIGHT_CONTROL -> AwtKeyEvent.VK_CONTROL
    GLFW_KEY_RIGHT_ALT -> AwtKeyEvent.VK_ALT
    else -> AwtKeyEvent.VK_UNDEFINED
}