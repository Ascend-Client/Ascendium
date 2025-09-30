package io.github.betterclient.ascendium.mixin.v1219;

import io.github.betterclient.ascendium.bridge.KeybindingBridge;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public class MixinKeybinding implements KeybindingBridge {
    @Shadow private boolean pressed;

    @Shadow private InputUtil.Key boundKey;
    @Override
    public boolean getButtonPressed() {
        return this.pressed;
    }

    @Override
    public @NotNull String getGetBoundKey() {
        return this.boundKey.getLocalizedText().getString();
    }
}
