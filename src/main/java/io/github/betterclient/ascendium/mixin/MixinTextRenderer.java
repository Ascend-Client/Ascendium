package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.TextRendererBridge;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextRenderer.class)
public abstract class MixinTextRenderer implements TextRendererBridge {
    //public int getWidth(@NotNull String text)
    //already exists, use existing method

    @Shadow @Final public int fontHeight;

    @Override
    public int getFontHeight() {
        return this.fontHeight;
    }
}
