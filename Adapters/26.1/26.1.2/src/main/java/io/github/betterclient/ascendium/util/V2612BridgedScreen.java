package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.bridge.BridgeScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class V2612BridgedScreen extends Screen {
    public BridgeScreen screen;

    public V2612BridgedScreen(BridgeScreen screen) {
        super(Component.empty());
        this.screen = screen;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (this.screen.shouldRenderBackground()) super.extractRenderState(context, mouseX, mouseY, delta);

        this.screen.setWidth(width);
        this.screen.setHeight(height);
        this.screen.render(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        this.screen.mouseClicked((int) click.x(), (int) click.y(), click.button());
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        this.screen.mouseReleased((int) click.x(), (int) click.y(), click.button());
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.screen.mouseScrolled((int) mouseX, (int) mouseY, horizontalAmount, verticalAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        this.screen.keyPressed(input.key(), input.scancode(), input.modifiers());
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        this.screen.keyReleased(input.key(), input.scancode(), input.modifiers());
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        this.screen.charTyped((char) input.codepoint(), input.codepoint());
        return super.charTyped(input);
    }

    @Override
    protected void init() {
        this.screen.init();
        super.init();
    }

    @Override
    public void onClose() {
        super.onClose();
        this.screen.close();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.screen.shouldCloseOnEsc();
    }
}
