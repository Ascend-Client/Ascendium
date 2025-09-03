package io.github.betterclient.ascendium.mixin.v1214.text;

import io.github.betterclient.ascendium.bridge.TextBridge;
import io.github.betterclient.ascendium.bridge.TextStyleBridge;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Text.class)
interface MixinText extends TextBridge {
    @Shadow String getString();

    @Override
    default @NotNull TextStyleBridge getStyle() {
        return (TextStyleBridge) ((Text) this).getStyle();
    }

    @Override
    default @NotNull String getText() {
        return this.getString();
    }

    @Override
    default @NotNull List<@NotNull TextBridge> getBridgedSiblings() {
        return ((Text) this).getSiblings().stream().map(text -> (TextBridge) text).toList();
    }
}
