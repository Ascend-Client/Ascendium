package io.github.betterclient.ascendium.demo.module

import io.github.betterclient.ascendium.demo.module.impl.CompassMod
import io.github.betterclient.ascendium.demo.module.impl.FPSMod
import io.github.betterclient.ascendium.demo.module.impl.HeadTrackerMod
import io.github.betterclient.ascendium.demo.module.impl.KeystrokesMod
import io.github.betterclient.ascendium.demo.module.impl.PingMod

object ModManager {
    val modules = listOf(
        CompassMod,
        HeadTrackerMod,
        KeystrokesMod,
        FPSMod,
        PingMod
    )
}