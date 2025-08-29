package io.github.betterclient.ascendium.mixin.render;

import io.github.betterclient.ascendium.event.RenderHudEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext drawContext, RenderTickCounter tickCounter, CallbackInfo callbackInfo) {
        new RenderHudEvent().broadcast();
    }
}
