package io.github.betterclient.ascendium.mixin.v2612.options;

import io.github.betterclient.ascendium.bridge.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Options.class)
public class MixinOptions implements OptionsBridge {
    @Shadow @Final public KeyMapping keyUp;
    @Shadow @Final public KeyMapping keyDown;
    @Shadow @Final public KeyMapping keyLeft;
    @Shadow @Final public KeyMapping keyRight;
    @Shadow @Final public KeyMapping keyAttack;
    @Shadow @Final public KeyMapping keyUse;
    @Shadow @Final public KeyMapping keyJump;

    @Mutable
    @Shadow
    @Final
    public KeyMapping[] keyMappings;

    @Override
    public @NotNull KeybindHelper addKeybinding(int defaultKey, @NotNull String name, @NotNull String category) {
        KeyMapping element = (KeyMapping) BridgeAdapterManager.INSTANCE.useBridgeUtil(BridgeAdapter::getKeybindingBridgeAdapter, name, defaultKey, category);
        this.keyMappings = ArrayUtils.add(this.keyMappings, element);
        return (KeybindHelper) element;
    }

    @Override
    public @NotNull KeybindingBridge getKeyForward() {
        return (KeybindingBridge) this.keyUp;
    }

    @Override
    public @NotNull KeybindingBridge getKeyBackward() {
        return (KeybindingBridge) this.keyDown;
    }

    @Override
    public @NotNull KeybindingBridge getKeyLeft() {
        return (KeybindingBridge) this.keyLeft;
    }

    @Override
    public @NotNull KeybindingBridge getKeyRight() {
        return (KeybindingBridge) this.keyRight;
    }

    @Override
    public @NotNull KeybindingBridge getKeyAttack() {
        return (KeybindingBridge) this.keyAttack;
    }

    @Override
    public @NotNull KeybindingBridge getKeyUse() {
        return (KeybindingBridge) this.keyUse;
    }

    @Override
    public @NotNull KeybindingBridge getKeyJump() {
        return (KeybindingBridge) this.keyJump;
    }
}