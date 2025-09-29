package io.github.betterclient.ascendium.demo.module.impl

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import io.github.betterclient.ascendium.demo.module.Module

object FPSMod : Module("FPS", "Display your frames per second.", 150) {
    val template by string("Template", "%FPS% fps")

    @Composable
    override fun Render() {
        Text(template.replace("%PFS%", "123"))
    }
}

object PingMod : Module("Ping", "Display your ping", 150) {
    val template by string("Template", "%PING%ms")

    @Composable
    override fun Render() {
        Text(template.replace("%PING%", "-1"))
    }
}