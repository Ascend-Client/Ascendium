package io.github.betterclient.ascendium.mixin.v2612.common;

import io.github.betterclient.ascendium.bridge.TextBridge;
import io.github.betterclient.ascendium.bridge.TextStyleBridge;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Component.class)
interface MixinText extends TextBridge {
    @Shadow String getString();

    @Override
    default @NotNull TextStyleBridge getStyle() {
        return TextStyleBridge.class.cast(((Component) this).getStyle());
    }

    @Override
    default @NotNull String getText() {
        return this.getString();
    }

    @Override
    default @NotNull List<@NotNull TextBridge> getBridgedSiblings() {
        return ((Component) this).getSiblings().stream().map(text -> (TextBridge) text).toList();
    }
}
