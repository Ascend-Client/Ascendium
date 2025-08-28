package io.github.betterclient.ascendium.mixin.text;

import io.github.betterclient.ascendium.TextBridge;
import io.github.betterclient.ascendium.TextStyleBridge;
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
