package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.module.impl.notifications.Notifications
import io.github.betterclient.ascendium.module.impl.hud.*

object ModManager {
    val modules = listOf(
        FPSMod,
        CPSMod,
        KeystrokesMod,
        PingMod,
        ServerDisplayMod,
        ReachDisplayMod,
        ArmorDisplayMod,
        PositionDisplayMod,
        HitHistoryMod,
        Notifications
    )

    fun getHUDModules() = modules.filterIsInstance<ComposableHUDModule>().filter { it `is` enabled }
}