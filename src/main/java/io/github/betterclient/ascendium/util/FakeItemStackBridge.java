package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.IdentifierBridge;
import io.github.betterclient.ascendium.ItemStackBridge;
import org.jetbrains.annotations.NotNull;

public class FakeItemStackBridge implements ItemStackBridge {
    int count;
    float durability;
    IdentifierBridge bridge;
    public FakeItemStackBridge(int count, float durability, IdentifierBridge bridge) {
        this.count = count;
        this.durability = durability;
        this.bridge = bridge;
    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public @NotNull IdentifierBridge getItemIdentifier() {
        return bridge;
    }

    @Override
    public float getDurability() {
        return durability;
    }
}
