package io.github.betterclient.ascendium.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.demo.mods.Mods
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Int8Array

var content: @Composable () -> Unit by mutableStateOf({
    AscendiumTheme {
        ParallaxBackground()
        Box(Modifier.fillMaxSize()) {
            ModeButtons()
        }
    }
})

@Composable
fun MainMenu() {
    content()
}

@Composable
fun BoxScope.ModeButtons() {
    Column(Modifier.align(Alignment.Center), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onClick = {}, modifier = Modifier.width(512.dp)) {
            Text("Ascendium", fontSize = 72.sp)
        }
        Button(onClick = {}, modifier = Modifier.width(256.dp).alpha(0.4f).align(Alignment.CenterHorizontally)) {
            Text("Demo version", fontSize = 24.sp)
        }

        val alpha = 0.7f //this should probably not be configurable
        Spacer(Modifier.height(192.dp))

        Button(
            onClick = {
                //SWITCH
                content = {
                    Mods(false)
                }
            },
            modifier = Modifier
                .width(512.dp)
                .alpha(alpha)
        ) {
            Text("Test the UI", fontSize = 24.sp)
        }

        Button(
            onClick = {
                //TRIGGER YOUTUBE
                window.open(
                    "https://www.youtube.com/watch?v=Y6VBWgT2YH4"
                )
            },
            modifier = Modifier
                .width(512.dp)
                .alpha(alpha)
        ) {
            Text("Watch the demo", fontSize = 24.sp)
        }

        Button(
            onClick = {
                //TRIGGER DOWNLOAD
                window.open(
                    "https://github.com/Ascend-Client/Ascendium"
                )
            },
            modifier = Modifier
                .width(512.dp)
                .alpha(alpha)
        ) {
            Text("Download jar", fontSize = 24.sp)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ParallaxBackground(
    modifier: Modifier = Modifier.fillMaxSize(),
    zoom: Float = 1.2f,
    maxOffset: Dp = 40.dp
) {
    val stopAmt = 4f
    var pointerOffset by remember { mutableStateOf(Offset.Zero) }
    var constraints0 by remember { mutableStateOf(Constraints()) }
    var targetOffset by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(
        modifier = modifier.pointerInput(Unit) {
            while (true) {
                val event = awaitPointerEventScope { awaitPointerEvent() }
                pointerOffset = event.changes.first().position
                val normalizedX = (pointerOffset.x / constraints0.maxWidth - 0.5f) * 2f
                val normalizedY = (pointerOffset.y / constraints0.maxHeight - 0.5f) * 2f
                val target = Offset(
                    (normalizedX * maxOffset.toPx()) / stopAmt,
                    (normalizedY * maxOffset.toPx()) / stopAmt
                )
                targetOffset = target
            }
        }
    ) {
        val imageModifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = targetOffset.x
                translationY = targetOffset.y
                scaleX = zoom
                scaleY = zoom
            }
        LaunchedEffect(constraints) {
            constraints0 = constraints
        }

        var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

        LaunchedEffect(Unit) {
            val img = window.fetch("/bg.png").await()
            val buf = img.arrayBuffer().await()
            val bites = Int8Array(buf).unsafeCast<ByteArray>()

            bitmap = bites.decodeToImageBitmap()
        }

        bitmap?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        }
    }
}