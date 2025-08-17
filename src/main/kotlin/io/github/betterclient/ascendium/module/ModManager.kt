package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.module.impl.CPSMod
import io.github.betterclient.ascendium.module.impl.FPSMod
import io.github.betterclient.ascendium.module.impl.KeystrokesMod
import io.github.betterclient.ascendium.module.impl.PingMod
import io.github.betterclient.ascendium.module.impl.ReachDisplayMod
import io.github.betterclient.ascendium.module.impl.ServerDisplayMod

object ModManager {
    val modules = listOf<Module>(FPSMod, CPSMod, KeystrokesMod, PingMod, ServerDisplayMod, ReachDisplayMod)

    fun getHUDModules() = modules.filterIsInstance<HUDModule>().filter { it `is` enabled }
}