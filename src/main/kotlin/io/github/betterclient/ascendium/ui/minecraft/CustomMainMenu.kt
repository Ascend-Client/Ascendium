package io.github.betterclient.ascendium.ui.minecraft

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme

object CustomMainMenu : ComposeUI({
    AscendiumTheme {
        Box(Modifier.fillMaxSize().background(AscendiumTheme.colorScheme.background)) {

        }
    }
}) {
    override fun shouldRenderBackground() = false
}