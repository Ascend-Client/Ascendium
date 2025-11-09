package io.github.betterclient.ascendium.ui.mods

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.util.ui.AscendiumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModsContent() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val titles = listOf("Mods", "Client", "Configs")
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = AscendiumTheme.colorScheme.background.copy(alpha = 0.4f),
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

    Spacer(modifier = Modifier.height(32.dp))

    when(selectedTab) {
        0 -> ModsTab()
        1 -> ClientTab()
        2 -> ConfigTab()
    }
}