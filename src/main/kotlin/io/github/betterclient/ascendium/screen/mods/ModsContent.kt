package io.github.betterclient.ascendium.screen.mods

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModsContent() {
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

    AnimatedContent(
        selectedTab
    ) {
        when(selectedTab) {
            0 -> ModsTab()
            1 -> {}//TODO
            2 -> ConfigTab()
        }
    }
}