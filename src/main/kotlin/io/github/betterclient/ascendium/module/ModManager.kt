package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.module.impl.ArmorDisplayMod
import io.github.betterclient.ascendium.module.impl.CPSMod
import io.github.betterclient.ascendium.module.impl.FPSMod
import io.github.betterclient.ascendium.module.impl.KeystrokesMod
import io.github.betterclient.ascendium.module.impl.PingMod
import io.github.betterclient.ascendium.module.impl.PositionDisplayMod
import io.github.betterclient.ascendium.module.impl.ReachDisplayMod
import io.github.betterclient.ascendium.module.impl.ServerDisplayMod

object ModManager {
    val modules = listOf<Module>(
        FPSMod,
        CPSMod,
        KeystrokesMod,
        PingMod,
        ServerDisplayMod,
        ReachDisplayMod,
        ArmorDisplayMod,
        PositionDisplayMod
    )

    fun getHUDModules() = modules.filterIsInstance<ComposableHUDModule>().filter { it `is` enabled }
}