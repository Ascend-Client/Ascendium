package io.github.betterclient.ascendium.mixin.v1219;

import io.github.betterclient.ascendium.event.*;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
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
    public boolean mouseClicked(Click click, boolean doubled) {
        new MouseScreenEvent(true, click.button()).broadcast();
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        new MouseScreenEvent(false, click.button()).broadcast();
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        new MouseScrollScreenEvent(horizontalAmount, verticalAmount).broadcast();
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void onKeyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        new KeyboardScreenEvent(input.getKeycode(), true).broadcast();
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        new KeyboardScreenEvent(input.getKeycode(), false).broadcast();
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        new KeyboardCharScreenEvent((char) input.codepoint()).broadcast();
        return super.charTyped(input);
    }
}
