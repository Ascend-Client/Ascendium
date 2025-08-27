package io.github.betterclient.ascendium.ui.config

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.compose.getUnscaled
import io.github.betterclient.ascendium.module.ComposableHUDModule
import io.github.betterclient.ascendium.module.Module
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme

@Composable
fun ConfigContent(preview: Boolean, mod: Module) {
    Box(Modifier.fillMaxSize()) {
        val state = rememberScrollState()
        Column(Modifier.verticalScroll(state)) {
            Spacer(Modifier.height(4.dp))
            Row { Spacer(Modifier.width(32.dp)); Text(
                text = mod.description,
                fontSize = 24.sp,
                fontFamily = AscendiumTheme.typography.headlineSmall.fontFamily,
                color = AscendiumTheme.colorScheme.onBackground
            ) }
            Spacer(Modifier.height(4.dp))
            ModToggle(mod)
            Spacer(Modifier.height(4.dp))
            for (setting in mod.settings) {
                SettingEditor(setting)
                Spacer(Modifier.height(4.dp))
            }
        }

        VerticalScrollbar(rememberScrollbarAdapter(state), modifier = Modifier.align(Alignment.CenterEnd))

        if (mod is ComposableHUDModule) {
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
}

@Composable
fun RenderPreview(mod: ComposableHUDModule, corner: Dp) {
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
        Image(
            bitmap = remember {
                Ascendium::class.java.getResourceAsStream("/assets/ascendium/preview.png")!!
                    .readAllBytes().decodeToImageBitmap()
            },
            contentDescription = "Preview image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

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
            mod.RenderComposable(true)
        }
    }
}