package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.BridgeRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

public class Renderer implements BridgeRenderer {
    DrawContext context;
    public Renderer(DrawContext context) {
        this.context = context;
    }

    @Override
    public void drawText(@NotNull String text, int x, int y, int color, float scale) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0F);
        context.getMatrices().translate(-x, -y, 0);
        context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, false);
        context.getMatrices().pop();
    }

    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    @Override
    public int getTextWidth(@NotNull String text) {
        return MinecraftClient.getInstance().textRenderer.getWidth(text);
    }
}
