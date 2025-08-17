package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.KeybindHelper;
import io.github.betterclient.ascendium.KeybindingBridge;
import io.github.betterclient.ascendium.OptionsBridge;
import io.github.betterclient.ascendium.util.KeybindingHelper;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public class MixinOptions implements OptionsBridge {
    @Shadow @Final @Mutable
    public KeyBinding[] allKeys;

    @Shadow @Final public KeyBinding forwardKey;
    @Shadow @Final public KeyBinding backKey;
    @Shadow @Final public KeyBinding leftKey;
    @Shadow @Final public KeyBinding rightKey;
    @Shadow @Final public KeyBinding attackKey;
    @Shadow @Final public KeyBinding useKey;
    @Shadow @Final public KeyBinding jumpKey;

    @Override
    public @NotNull KeybindHelper addKeybinding(int defaultKey, @NotNull String name, @NotNull String category) {
        KeybindingHelper element = new KeybindingHelper(name, defaultKey, category) ;
        this.allKeys = ArrayUtils.add(this.allKeys, element);
        return element;
    }

    @Override
    public @NotNull KeybindingBridge getKeyForward() {
        return (KeybindingBridge) this.forwardKey;
    }

    @Override
    public @NotNull KeybindingBridge getKeyBackward() {
        return (KeybindingBridge) this.backKey;
    }

    @Override
    public @NotNull KeybindingBridge getKeyLeft() {
        return (KeybindingBridge) this.leftKey;
    }

    @Override
    public @NotNull KeybindingBridge getKeyRight() {
        return (KeybindingBridge) this.rightKey;
    }

    @Override
    public @NotNull KeybindingBridge getKeyAttack() {
        return (KeybindingBridge) this.attackKey;
    }

    @Override
    public @NotNull KeybindingBridge getKeyUse() {
        return (KeybindingBridge) this.useKey;
    }

    @Override
    public @NotNull KeybindingBridge getKeyJump() {
        return (KeybindingBridge) this.jumpKey;
    }
}