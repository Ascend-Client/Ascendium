package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.RequiresOffscreen

class DontRequireOffscreen() : RequiresOffscreen {
    override val does: Boolean = false
}

class RequireOffscreen() : RequiresOffscreen {
    override val does: Boolean = true
}