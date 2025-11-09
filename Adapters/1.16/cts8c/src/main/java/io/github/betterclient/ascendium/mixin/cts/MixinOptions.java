package io.github.betterclient.ascendium.mixin.cts;

import io.github.betterclient.ascendium.bridge.*;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public class MixinOptions implements OptionsBridge {
    @Shadow @Final public KeyBinding keyForward;

    @Shadow @Final public KeyBinding keyBack;

    @Shadow @Final public KeyBinding keyLeft;

    @Shadow @Final public KeyBinding keyRight;

    @Shadow @Final public KeyBinding keyAttack;

    @Shadow @Final public KeyBinding keyUse;

    @Shadow @Final public KeyBinding keyJump;

    @Mutable @Shadow @Final public KeyBinding[] keysAll;

    @Override
    public @NotNull KeybindHelper addKeybinding(int defaultKey, @NotNull String name, @NotNull String category) {
        KeyBinding element = (KeyBinding) BridgeAdapterManager.INSTANCE.useBridgeUtil(BridgeAdapter::getKeybindingBridgeAdapter, name, defaultKey, category);
        this.keysAll = ArrayUtils.add(this.keysAll, element);
        return (KeybindHelper) element;
    }

    @Override
    public @NotNull KeybindingBridge getKeyForward() {
        return (KeybindingBridge) this.keyForward;
    }

    @Override
    public @NotNull KeybindingBridge getKeyBackward() {
        return (KeybindingBridge) this.keyBack;
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