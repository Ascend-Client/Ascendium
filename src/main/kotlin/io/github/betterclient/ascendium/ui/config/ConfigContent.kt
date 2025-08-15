package io.github.betterclient.ascendium.ui.config

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.compose.renderWithMC
import io.github.betterclient.ascendium.module.HUDModule
import io.github.betterclient.ascendium.module.Module
import org.jetbrains.skia.IRect

@Composable
fun ConfigContent(preview: Boolean, mod: Module) {
    Box(Modifier.fillMaxSize()) {
        val state = rememberScrollState()
        Column(Modifier.verticalScroll(state)) {
            Spacer(Modifier.height(4.dp))
            for (setting in mod.settings) {
                SettingEditor(setting)
                Spacer(Modifier.height(4.dp))
            }
        }

        VerticalScrollbar(rememberScrollbarAdapter(state), modifier = Modifier.align(Alignment.CenterEnd))

        if (mod is HUDModule) {
            val previewState = remember { mutableStateOf(false) }
            LaunchedEffect(preview) { previewState.value = preview }

            val corner by animateDpAsState(if (preview) 16.dp else 0.dp)
            AnimatedVisibility(
                visible = preview,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .renderWithMC(previewState) { context, rect ->
                        renderModInMiddle(context, rect, mod)
                    },
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                RenderPreview(mod, corner)
            }
        }
    }
}

@Composable
fun RenderPreview(mod: HUDModule, corner: Dp) {
    Image(
        bitmap = remember { Ascendium::class.java.getResourceAsStream("/assets/ascendium/preview.png")!!.readAllBytes().decodeToImageBitmap() },
        contentDescription = "Preview image",
        modifier = Modifier
            .size(300.dp, if (mod.width > mod.height) 150.dp else 300.dp)
            .clip(RoundedCornerShape(corner)),
        contentScale = ContentScale.Crop
    )
}

fun renderModInMiddle(
    context: BridgeRenderer,
    rect: IRect,
    mod: HUDModule
) {
    val rectWidth = rect.right - rect.left
    val rectHeight = rect.bottom - rect.top
    val usableWidth = rectWidth * 0.9
    val usableHeight = rectHeight * 0.9

    val baseWidth = mod.width / mod.scale
    val baseHeight = mod.height / mod.scale

    val scaleX = usableWidth / baseWidth
    val scaleY = usableHeight / baseHeight
    val targetScale = minOf(scaleX, scaleY).coerceIn(0.25, 3.0)

    val drawWidth = baseWidth * targetScale
    val drawHeight = baseHeight * targetScale
    val left = rect.left + ((rectWidth - drawWidth) / 2).toInt()
    val top = rect.top + ((rectHeight - drawHeight) / 2).toInt()

    mod.renderAt(left, top, targetScale, context)
}
