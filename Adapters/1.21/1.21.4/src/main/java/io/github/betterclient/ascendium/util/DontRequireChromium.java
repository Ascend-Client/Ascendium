package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.bridge.RequiresChromium;

public class DontRequireChromium implements RequiresChromium {
    @Override
    public boolean getDoes() {
        return false;
    }
}
