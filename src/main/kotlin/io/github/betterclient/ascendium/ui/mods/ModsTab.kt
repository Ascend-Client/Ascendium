package io.github.betterclient.ascendium.ui.mods

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.module.Module
import io.github.betterclient.ascendium.module.config.ConfigManager
import io.github.betterclient.ascendium.ui.bridge.DynamicUI
import io.github.betterclient.ascendium.ui.config.ConfigUI
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme

@Composable
fun ModsTab() {
    Box(Modifier.fillMaxSize()) {
        val items = remember { ModManager.modules }
        val scroll = rememberLazyListState()

        LazyColumn(state = scroll) {
            itemsIndexed(items.chunked(3)) { index, mods ->
                Row {
                    Spacer(Modifier.width(SPACING.dp))
                    mods.forEachIndexed { index0, module ->
                        ModuleView(module)
                        Spacer(Modifier.width(SPACING.dp))
                    }
                }
                if (index != (items.size - 1)) Spacer(Modifier.height(SPACING.dp))
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scroll),
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

const val SPACING = 32
const val WIDTH = 172
const val HEIGHT = 120

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ModuleView(module: Module) {
    var enabled by remember { mutableStateOf(module.enabled) }
    val cornerRadius by animateDpAsState(
        targetValue = if (enabled) 32.dp else 16.dp
    )

    val theme = AscendiumTheme.colorScheme
    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) theme.primaryContainer else theme.surfaceContainer
    )

    Box(Modifier
        .size(WIDTH.dp, HEIGHT.dp)
        .dropShadow(
            RoundedCornerShape(cornerRadius),
            Shadow(8.dp, backgroundColor)
        )
        .background(
            backgroundColor,
            RoundedCornerShape(cornerRadius)
        )
        .clip(RoundedCornerShape(cornerRadius))
        .onClick(
            matcher = PointerMatcher.mouse(PointerButton.Secondary),
            onClick = {
                //TODO: config ui server
                DynamicUI.current.switchTo({ _, _ -> ByteArray(0) }) {
                    ConfigUI(module, true)
                }
            }
        )
        .clickable { enabled = !enabled; module.toggle(); ConfigManager.saveConfig() }
    ) {
        Text(
            module.name,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}