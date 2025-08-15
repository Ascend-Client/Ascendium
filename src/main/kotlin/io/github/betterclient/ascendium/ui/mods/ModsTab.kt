package io.github.betterclient.ascendium.ui.mods

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.module.Module

@Composable
fun ModsTab() {
    Box(Modifier.fillMaxSize()) {
        val items = remember { List(16) { ModManager.modules }.flatten().chunked(3) }
        val scroll = rememberLazyListState()

        LazyColumn(state = scroll) {
            itemsIndexed(items) { index, mods ->
                Row {
                    Spacer(Modifier.width(16.dp))
                    mods.forEachIndexed { index0, module ->
                        ModuleView(module)
                        Spacer(Modifier.width(16.dp))
                    }
                }
                if (index != (items.size - 1)) Spacer(Modifier.height(16.dp))
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scroll),
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun ModuleView(module: Module) {
    var enabled by remember { mutableStateOf(module.enabled) }
    val theme = MaterialTheme.colorScheme

    val cornerRadius by animateDpAsState(
        targetValue = if (enabled) 48.dp else 16.dp
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) theme.primaryContainer else theme.surfaceContainer
    )
    val contentColor by animateColorAsState(
        targetValue = if (enabled) theme.onPrimaryContainer else theme.onSurface
    )

    Box(Modifier
        .size(192.dp, 140.dp)
        .background(
            backgroundColor,
            RoundedCornerShape(cornerRadius)
        )
        .clip(RoundedCornerShape(cornerRadius))
        .clickable { enabled = !enabled; module.toggle() }
    ) {
        Text(
            module.name,
            modifier = Modifier.align(Alignment.Center),
            color = contentColor
        )
    }
}