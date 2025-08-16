package io.github.betterclient.ascendium.ui.mods

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.module.Module
import io.github.betterclient.ascendium.module.config.ConfigManager
import io.github.betterclient.ascendium.ui.config.ConfigUI

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
    val theme = MaterialTheme.colorScheme

    val cornerRadius by animateDpAsState(
        targetValue = if (enabled) 32.dp else 16.dp
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) theme.primaryContainer else theme.surfaceContainer
    )

    Box(Modifier
        .size(WIDTH.dp, HEIGHT.dp)
        .background(
            backgroundColor,
            RoundedCornerShape(cornerRadius)
        )
        .clip(RoundedCornerShape(cornerRadius))
        .onClick(
            matcher = PointerMatcher.mouse(PointerButton.Secondary),
            onClick = {
                ComposeUI.current.switchTo {
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