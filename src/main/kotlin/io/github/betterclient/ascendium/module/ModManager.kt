package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.module.impl.hud.ArmorDisplayMod
import io.github.betterclient.ascendium.module.impl.hud.CPSMod
import io.github.betterclient.ascendium.module.impl.hud.FPSMod
import io.github.betterclient.ascendium.module.impl.hud.KeystrokesMod
import io.github.betterclient.ascendium.module.impl.hud.PingMod
import io.github.betterclient.ascendium.module.impl.hud.PositionDisplayMod
import io.github.betterclient.ascendium.module.impl.hud.ReachDisplayMod
import io.github.betterclient.ascendium.module.impl.hud.ServerDisplayMod

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