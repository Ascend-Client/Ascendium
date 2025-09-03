package io.github.betterclient.ascendium.mixin.v1214.util;

import io.github.betterclient.ascendium.bridge.MouseBridge;
import io.github.betterclient.ascendium.event.MouseClickEvent;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse implements MouseBridge {
    @Shadow private double x;

    @Shadow private double y;

    public int getXPos() {
        return (int) this.x;
    }

    @Override
    public int getYPos() {
        return (int) this.y;
    }

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V", ordinal = 0))
    public void onClick(long window, int button, int action, int mods, CallbackInfo ci) {
        new MouseClickEvent(button, action == 1).broadcast();
    }
}
