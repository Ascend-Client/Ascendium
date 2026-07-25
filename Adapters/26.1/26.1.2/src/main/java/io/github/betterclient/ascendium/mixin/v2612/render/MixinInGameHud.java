package io.github.betterclient.ascendium.mixin.v2612.render;

import io.github.betterclient.ascendium.event.RenderHudEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinInGameHud {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        new RenderHudEvent().broadcast();
    }
}
