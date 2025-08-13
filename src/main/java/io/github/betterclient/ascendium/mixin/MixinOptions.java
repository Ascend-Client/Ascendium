package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.KeybindHelper;
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

    @Override
    public @NotNull KeybindHelper addKeybinding(int defaultKey, @NotNull String name, @NotNull String category) {
        KeybindingHelper element = new KeybindingHelper(name, defaultKey, category) ;
        this.allKeys = ArrayUtils.add(this.allKeys, element);
        return element;
    }
}