package io.github.betterclient.ascendium.mixin.v2612.render;

import io.github.betterclient.ascendium.event.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractContainerEventHandler {
    @Inject(method = "extractRenderState", at = @At("RETURN"))
    public void onRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        new RenderScreenEvent().broadcast();
    }

    @Redirect(method = "extractRenderStateWithTooltipAndSubtitles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
    public void onRenderBackground(Screen instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        instance.extractBackground(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        new MouseScreenEvent(true, click.button()).broadcast();
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        new MouseScreenEvent(false, click.button()).broadcast();
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        new MouseScrollScreenEvent(horizontalAmount, verticalAmount).broadcast();
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        new KeyboardScreenEvent(event.key(), true).broadcast();
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        new KeyboardScreenEvent(input.key(), false).broadcast();
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        new KeyboardCharScreenEvent((char) input.codepoint()).broadcast();
        return super.charTyped(input);
    }
}
