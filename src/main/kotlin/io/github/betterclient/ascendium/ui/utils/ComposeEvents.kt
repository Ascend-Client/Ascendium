package io.github.betterclient.ascendium.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.compose.getScaled
import org.jetbrains.skia.IRect
import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

@Composable
fun Modifier.detectOutsideClick(
    onOutsideClick: () -> Unit
): Modifier {
    var position by remember { mutableStateOf<Rect?>(null) }
    LaunchedEffect(Unit) {
        fun handler(composeCoords: Offset, mcMouse: Offset, button: Int, clicked: Boolean): Boolean {
            if (position == null) return false
            if (button != 0) return false
            if (!clicked) return false

            if (composeCoords.x < position!!.left || composeCoords.x > position!!.right ||
                composeCoords.y < position!!.top || composeCoords.y > position!!.bottom) {
                onOutsideClick()
                ComposeUI.current.removeMouseHandler(::handler)
                return true
            }

            return false
        }
        ComposeUI.current.addMouseHandler(::handler)
    }

    return this.onGloballyPositioned { positions ->
        position = positions.toRect()
    }
}

private fun LayoutCoordinates.toRect() = Rect(
    left = this.positionInRoot().x,
    top = this.positionInRoot().y,
    right = this.positionInRoot().x + this.size.width,
    bottom = this.positionInRoot().y + this.size.height
)

@Composable
fun Modifier.renderWithMC(visible: State<Boolean>, block: (context: BridgeRenderer, coords: IRect) -> Unit): Modifier {
    var position by remember { mutableStateOf<Rect?>(null) }

    LaunchedEffect(Unit) {
        ComposeUI.current.addRenderHandler { context, mouseX, mouseY ->
            if (position == null) return@addRenderHandler
            if (!visible.value) return@addRenderHandler

            block(context, IRect.makeLTRB(
                position!!.left.getScaled().toInt(),
                position!!.top.getScaled().toInt(),
                position!!.right.getScaled().toInt(),
                position!!.bottom.getScaled().toInt()
            ))
        }
    }

    return this.onGloballyPositioned {
        position = it.toRect()
    }
}

@Composable
fun Modifier.detectInsideEvent(component: Component, func: (mouseX: Int, mouseY: Int, event: MouseEvent) -> Unit): Modifier {
    var position by remember { mutableStateOf<Rect?>(null) }
    LaunchedEffect(Unit) {
        ComposeUI.current.addMouseEventHandler { x, y, event ->
            if (position == null) return@addMouseEventHandler false

            if (x < position!!.left || x > position!!.right ||
                y < position!!.top || y > position!!.bottom) {
                return@addMouseEventHandler false //outside
            }

            func(
                (x - position!!.left).toInt(),
                (y - position!!.top).toInt(),
                translateMouseEvent(component, position!!.left.toInt(), position!!.top.toInt(), event)
            )
            return@addMouseEventHandler false
        }
    }
    return this.onGloballyPositioned {
        position = it.toRect()
    }
}

private fun translateMouseEvent(
    component: Component,
    x: Int,
    y: Int,
    event: MouseEvent
): MouseEvent {
    val translatedX = event.x - x
    val translatedY = event.y - y

    return when (event) {
        is MouseWheelEvent -> MouseWheelEvent(
            component,
            event.id,
            event.`when`,
            event.modifiersEx,
            translatedX,
            translatedY,
            event.clickCount,
            event.isPopupTrigger,
            event.scrollType,
            event.scrollAmount,
            event.wheelRotation
        )
        else -> MouseEvent(
            component,
            event.id,
            event.`when`,
            event.modifiersEx,
            translatedX,
            translatedY,
            event.clickCount,
            event.isPopupTrigger,
            event.button
        )
    }
}