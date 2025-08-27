package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.module.impl.hud.*

object ModManager {
    val modules = listOf<Module>(
        FPSMod,
        CPSMod,
        KeystrokesMod,
        PingMod,
        ServerDisplayMod,
        ReachDisplayMod,
        ArmorDisplayMod,
        PositionDisplayMod,
        HitHistoryMod
    )

    fun getHUDModules() = modules.filterIsInstance<ComposableHUDModule>().filter { it `is` enabled }
}