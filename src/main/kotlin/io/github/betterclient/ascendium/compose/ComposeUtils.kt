package io.github.betterclient.ascendium.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot

@Composable
fun Modifier.detectOutsideClick(
    onOutsideClick: () -> Unit
): Modifier {
    var position by remember { mutableStateOf<Rect?>(null) }
    LaunchedEffect(Unit) {
        ComposeUI.current.addMouseHandler { composeCoords, mcCoords, button, clicked ->
            if (position == null) return@addMouseHandler false

            if (composeCoords.x < position!!.left || composeCoords.x > position!!.right ||
                composeCoords.y < position!!.top || composeCoords.y > position!!.bottom) {
                onOutsideClick()
                return@addMouseHandler true
            }

            return@addMouseHandler false
        }
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
