package io.github.betterclient.ascendium.ui.chrome

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.AscendiumTheme
import io.github.betterclient.ascendium.compose.Center
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.compose.detectInsideEvent
import io.github.betterclient.ascendium.ui.mods.ModsUI
import kotlinx.coroutines.runBlocking
import java.awt.event.KeyEvent
import java.awt.event.MouseWheelEvent

@Composable
fun ComposeChrome() {
    AscendiumTheme {
        Button(onClick = {
            ComposeUI.current.switchTo { ModsUI(false) }
        }) { Text("Back") }
        Center {
            val browser = remember { OffscreenBrowser() }
            var frame by remember { mutableStateOf<ImageBitmap?>(null) }
            LaunchedEffect(Unit) {
                Thread {
                    Thread.sleep(500)
                    while (true) {
                        try {
                            frame = runBlocking { browser.getBuffer() }
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }.start()
            }

            DisposableEffect(Unit) {
                onDispose { browser.close() }
            }

            frame?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(600.dp)
                        .focusable()
                        .onKeyEvent { keyEvent ->
                            val p0 = keyEvent.nativeKeyEvent as KeyEvent
                            val p1 = KeyEvent(browser.browser.uiComponent, p0.id, p0.`when`, p0.modifiersEx, p0.keyCode, p0.keyChar, p0.keyLocation)
                            browser.browser.sendKeyEvent(p1)
                            println(p1)
                            true
                        }
                        .detectInsideEvent(browser.browser.uiComponent) { mouseX, mouseY, event ->
                            if (event is MouseWheelEvent) {
                                browser.browser.sendMouseWheelEvent(event)
                            } else {
                                browser.browser.sendMouseEvent(event)
                            }
                        }
                )
            }
        }
    }
}
