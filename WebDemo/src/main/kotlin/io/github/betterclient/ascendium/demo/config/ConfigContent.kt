package io.github.betterclient.ascendium.ui.config

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.demo.config.ModToggle
import io.github.betterclient.ascendium.demo.config.SettingEditor
import io.github.betterclient.ascendium.demo.module.Module
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Int8Array

@Composable
fun ConfigContent(preview: Boolean, mod: Module) {
    Box(Modifier.fillMaxSize()) {
        val state = rememberScrollState()
        Column(Modifier.verticalScroll(state)) {
            Spacer(Modifier.height(4.dp))
            Row { Spacer(Modifier.width(32.dp)); Text(
                text = mod.description,
                fontSize = 24.sp,
                fontFamily = MaterialTheme.typography.headlineSmall.fontFamily,
                color = MaterialTheme.colorScheme.onBackground
            ) }
            Spacer(Modifier.height(4.dp))
            ModToggle(mod)
            Spacer(Modifier.height(4.dp))
            for (setting in mod.settings) {
                val visible by remember { derivedStateOf { setting.condition() } }
                if (visible) {
                    SettingEditor(setting)
                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        VerticalScrollbar(rememberScrollbarAdapter(state), modifier = Modifier.align(Alignment.CenterEnd))

        val previewState = remember { mutableStateOf(false) }
        LaunchedEffect(preview) { previewState.value = preview }

        val corner by animateDpAsState(if (preview) 16.dp else 0.dp)
        AnimatedVisibility(
            visible = preview,
            modifier = Modifier
                .align(Alignment.TopEnd),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            RenderPreview(mod, corner)
        }
    }
}

var img: ImageBitmap? by mutableStateOf(null)

@Composable
fun RenderPreview(mod: Module, corner: Dp) {
    var modSize by remember { mutableStateOf(Offset.Zero) }
    Box(
        Modifier
            .size(
                300.dp,
                animateDpAsState(mod.previewHeight.dp).value
            )
            .clip(RoundedCornerShape(corner)),
        contentAlignment = Alignment.Center
    ) {
        if (img == null) {
            LaunchedEffect(Unit) {
                val img0 = window.fetch("/preview.png").await()
                val buf = img0.arrayBuffer().await()
                val bites = Int8Array(buf).unsafeCast<ByteArray>()

                img = bites.decodeToImageBitmap()
            }
        }
        img?.let {
            Image(
                bitmap = it,
                contentDescription = "Preview image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    modSize = Offset(it.size.width.toFloat(), it.size.height.toFloat())
                }
                .graphicsLayer {
                    val scaleX0 = this.size.width / modSize.x
                    val scaleY0 = this.size.height / modSize.y
                    val scale = minOf(scaleX0, scaleY0, 3f)
                    scaleX = scale
                    scaleY = scale
                },
            contentAlignment = Alignment.Center
        ) {
            mod.RenderComposable()
        }
    }
}