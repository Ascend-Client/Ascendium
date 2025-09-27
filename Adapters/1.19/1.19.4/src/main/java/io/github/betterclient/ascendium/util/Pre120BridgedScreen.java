package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.bridge.BridgeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class Pre120BridgedScreen extends Screen {
    public BridgeScreen screen;

    public Pre120BridgedScreen(BridgeScreen screen) {
        super(Text.empty());
        this.screen = screen;
    }

    @Override
    public void render(MatrixStack context, int mouseX, int mouseY, float delta) {
        if (this.screen.shouldRenderBackground()) super.renderBackground(context);

        this.screen.setWidth(width);
        this.screen.setHeight(height);
        this.screen.render(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.screen.mouseClicked((int) mouseX, (int) mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.screen.mouseReleased((int) mouseX, (int) mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double v) {
        this.screen.mouseScrolled((int) mouseX, (int) mouseY, v, v);
        return super.mouseScrolled(mouseX, mouseY, v);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.screen.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.screen.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.screen.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
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
