package io.github.betterclient.ascendium.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.module.Module

//TODO: remove after ModMoveUI
class ModsUI : ComposeUI({
    ModsUI(false)
})

@Composable
fun ModsUI(smallen: Boolean) {
    MaterialTheme(darkColorScheme()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            var expanded by remember { mutableStateOf(false) }
            val targetSize: Dp = if (smallen) {
                (if (!expanded) 512 + 256 + 128 else 512 + 128).dp
            } else {
                (if (!expanded) 60 else 512 + 128).dp
            }
            val animatedWidth by animateDpAsState(targetValue = targetSize)

            val targetSize0: Dp = if (smallen) {
                (if (!expanded) 512 + 256 else 512).dp
            } else {
                (if (!expanded) 48 else 512).dp
            }
            val animatedHeight by animateDpAsState(targetValue = targetSize0)

            LaunchedEffect(Unit) {
                expanded = true
            }

            Box(Modifier
                .size(animatedWidth, animatedHeight)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f), RoundedCornerShape(32.dp))
                .safeContentPadding()
                .clip(RoundedCornerShape(32.dp))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                    ModsContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModsContent() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val titles = listOf("Mods", "Client", "Configs")
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth(0.5f),
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
    }

    when(selectedTab) {
        0 -> ModsTab()
        1 -> {}//TODO
        2 -> {}//TODO
    }
}


@Composable
private fun ModsTab() {
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