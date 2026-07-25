package io.github.betterclient.ascendium.mixin.v2612.options;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.betterclient.ascendium.bridge.KeybindingBridge;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyMapping.class)
public class MixinKeybinding implements KeybindingBridge {
    @Shadow
    private boolean isDown;

    @Shadow
    protected InputConstants.Key key;

    @Override
    public boolean getButtonPressed() {
        return this.isDown;
    }

    @Override
    public @NotNull String getGetBoundKey() {
        return this.key.getDisplayName().getString();
    }
}
