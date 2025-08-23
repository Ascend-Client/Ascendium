package io.github.betterclient.ascendium.module

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent

abstract class HUDModule(name: String, description: String, hasBackground: Boolean = true) : ComposableHUDModule(name, description, hasBackground) {
    override val minecraftFont: Boolean by boolean("Use minecraft font", true)
}

abstract class TextModule(name: String, description: String) : ComposableHUDModule(name, description, true) {
    override val minecraftFont: Boolean by boolean("Use minecraft font", true)

    var text by mutableStateOf("")

    @Composable
    override fun Render() {
        Text(text)
    }

    @EventTarget
    fun __render(event: RenderHudEvent) {
        text = renderModule()
    }

    abstract fun renderModule(): String
}