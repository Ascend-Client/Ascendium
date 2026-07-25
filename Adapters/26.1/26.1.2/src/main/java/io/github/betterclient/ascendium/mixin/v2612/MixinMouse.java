package io.github.betterclient.ascendium.mixin.v2612;

import io.github.betterclient.ascendium.bridge.MouseBridge;
import io.github.betterclient.ascendium.event.MouseClickEvent;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouse implements MouseBridge {
    @Shadow private double xpos;
    @Shadow private double ypos;

    public int getXPos() {
        return (int) this.xpos;
    }

    @Override
    public int getYPos() {
        return (int) this.ypos;
    }

    @Inject(method = "onButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V", ordinal = 0))
    public void onClick(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
        new MouseClickEvent(rawButtonInfo.button(), action == 1).broadcast();
    }
}
