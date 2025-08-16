package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.TextRendererBridge;
import net.minecraft.client.font.TextRenderer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextRenderer.class)
public abstract class MixinTextRenderer implements TextRendererBridge {
    @Shadow @Final public int fontHeight;

    @Shadow public abstract int getWidth(String text);

    @Override
    public int getFontHeight() {
        return this.fontHeight;
    }

    @Override
    public int getTextWidth(@NotNull String text) {
        return this.getWidth(text);
    }
}
