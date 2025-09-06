package io.github.betterclient.ascendium.ui.chrome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.ui.bridge.DynamicUI
import io.github.betterclient.ascendium.ui.mods.ModsUI
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme

val composeBrowser = ComposeBrowser(ChromiumDownloader.app!!, "https://google.com/")

@Composable
fun EasterEggUI() {
    AscendiumTheme {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center)) {
                val color =
                    AscendiumTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacityState.toFloat())
                Row(
                    Modifier
                        .size(800.dp, 100.dp)
                        .dropShadow(
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                            Shadow(8.dp, color)
                        )
                        .background(
                            color = color,
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        ),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Navigation()
                }

                BrowserView(
                    composeBrowser,
                    modifier = Modifier.size(800.dp, 600.dp),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun RowScope.Navigation() {
    var textFieldValue by remember(composeBrowser.myURL) { mutableStateOf(composeBrowser.myURL) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Spacer(Modifier.width(2.dp))

    Button(onClick = {
        //TODO: mods UI server
        DynamicUI.current.switchTo() { ModsUI(true) }
    }) { Text("Back") }

    IconButton(
        onClick = { composeBrowser.back() },
        enabled = composeBrowser.canGoBack
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = AscendiumTheme.colorScheme.onBackground
        )
    }

    IconButton(
        onClick = { composeBrowser.forward() },
        enabled = composeBrowser.canGoForward
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Forward",
            tint = AscendiumTheme.colorScheme.onBackground
        )
    }

    TextField(
        value = textFieldValue,
        onValueChange = { textFieldValue = it },
        modifier = Modifier.weight(1f),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(onGo = {
            composeBrowser.setUrl(textFieldValue)
            keyboardController?.hide()
        })
    )

    IconButton(onClick = {
        composeBrowser.setUrl("https://www.google.com")
    }) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = null,
            tint = AscendiumTheme.colorScheme.onBackground
        )
    }

    Spacer(Modifier.width(2.dp))
}