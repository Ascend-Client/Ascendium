package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.bridge.RequiresChromium;

public class RequireChromium implements RequiresChromium {
    @Override
    public boolean getDoes() {
        return true;
    }
}
