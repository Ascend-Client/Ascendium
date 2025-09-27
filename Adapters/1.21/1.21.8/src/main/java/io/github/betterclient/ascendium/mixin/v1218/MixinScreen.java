package io.github.betterclient.ascendium.mixin.v1218;

import io.github.betterclient.ascendium.event.*;
import io.github.betterclient.ascendium.ui.bridge.ComposeUI;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractParentElement {
    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        new RenderScreenEvent().broadcast();
    }

    @Redirect(method = "renderWithTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    public void onRenderBackground(Screen instance, DrawContext context, int mouseX, int mouseY, float delta) {
        instance.renderBackground(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        new MouseScreenEvent(true, button).broadcast();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        new MouseScreenEvent(false, button).broadcast();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        new MouseScrollScreenEvent(horizontalAmount, verticalAmount).broadcast();
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        new KeyboardScreenEvent(keyCode, true).broadcast();
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        new KeyboardScreenEvent(keyCode, false).broadcast();
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        new KeyboardCharScreenEvent(chr).broadcast();
        return super.charTyped(chr, modifiers);
    }
}