package io.github.betterclient.ascendium.mixin.v1194;

import io.github.betterclient.ascendium.event.RenderHudEvent;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack drawContext, float tickCounter, CallbackInfo callbackInfo) {
        new RenderHudEvent().broadcast();
    }
}
