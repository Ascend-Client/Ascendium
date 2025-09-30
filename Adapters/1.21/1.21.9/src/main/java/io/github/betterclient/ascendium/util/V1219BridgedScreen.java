package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.bridge.BridgeScreen;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class V1219BridgedScreen extends Screen {
    public BridgeScreen screen;

    public V1219BridgedScreen(BridgeScreen screen) {
        super(Text.empty());
        this.screen = screen;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.screen.shouldRenderBackground()) super.render(context, mouseX, mouseY, delta);

        this.screen.setWidth(width);
        this.screen.setHeight(height);
        this.screen.render(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        this.screen.mouseClicked((int) click.x(), (int) click.y(), click.button());
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        this.screen.mouseReleased((int) click.x(), (int) click.y(), click.button());
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.screen.mouseScrolled((int) mouseX, (int) mouseY, horizontalAmount, verticalAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        this.screen.keyPressed(input.getKeycode(), input.scancode(), input.modifiers());
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        this.screen.keyReleased(input.getKeycode(), input.scancode(), input.modifiers());
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        this.screen.charTyped((char) input.codepoint(), input.modifiers());
        return super.charTyped(input);
    }

    @Override
    protected void init() {
        this.screen.init();
        super.init();
    }

    @Override
    public void close() {
        super.close();
        this.screen.close();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.screen.shouldCloseOnEsc();
    }
}
