package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.module.impl.CPSMod
import io.github.betterclient.ascendium.module.impl.FPSMod

object ModManager {
    val modules = listOf<Module>(FPSMod, CPSMod)

    fun getHUDModules() = modules.filterIsInstance<HUDModule>().filter { it `is` enabled }
}