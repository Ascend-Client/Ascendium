package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.module.impl.FPSMod

object ModManager {
    val modules = listOf<Module>(FPSMod)

    fun getHUDModules() = modules.filter { it is HUDModule }.map { it as HUDModule }.toList()
}