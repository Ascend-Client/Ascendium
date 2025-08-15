package io.github.betterclient.ascendium.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.detectOutsideClick(
    onOutsideClick: () -> Unit
): Modifier {
    var position by remember { mutableStateOf<Rect?>(null) }
    LaunchedEffect(Unit) {
        fun handler(composeCoords: Offset, mcMouse: Offset, button: Int, clicked: Boolean): Boolean {
            if (position == null) return false
            if (button != 0) return false

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

val darkColorScheme = darkColorScheme()
fun showToast(text: String) {
    ComposeUI.current.toast {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val no = false
            var yes by remember { mutableStateOf(no) }
            LaunchedEffect(Unit) {
                yes = true
            }

            AnimatedVisibility(
                visible = yes,
            ) {
                Column(
                    Modifier
                        .detectOutsideClick {
                            yes = no //hehe
                            Thread {
                                Thread.sleep(300)
                                ComposeUI.current.toast {  }
                            }.start()
                        }
                        .background(darkColorScheme.primaryContainer, RoundedCornerShape(16.dp)).padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = text,
                        color = Color.White
                    )

                    Button(
                        onClick = {
                            yes = no
                            Thread {
                                Thread.sleep(300)
                                ComposeUI.current.toast {  }
                            }.start()
                        }
                    ) { Text("OK") }
                }
            }
        }
    }
}