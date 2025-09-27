package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.bridge.RequiresOffscreen;

public class RequireOffscreen implements RequiresOffscreen {
    @Override
    public boolean getDoes() {
        return true;
    }
}
