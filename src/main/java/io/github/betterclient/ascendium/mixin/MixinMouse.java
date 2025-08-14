package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.MouseBridge;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Mouse.class)
public class MixinMouse implements MouseBridge {
    @Shadow private double x;

    @Shadow private double y;

    public int getXPos() {
        return (int) this.x;
    }

    @Override
    public int getYPos() {
        return (int) this.y;
    }
}
