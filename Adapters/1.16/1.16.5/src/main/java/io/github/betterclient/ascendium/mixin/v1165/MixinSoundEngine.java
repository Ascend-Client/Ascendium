package io.github.betterclient.ascendium.mixin.v1165;

import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreen;
import net.minecraft.client.sound.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {
    @Inject(method = "init", at = @At("HEAD"))
    public void onInit(CallbackInfo ci) {
        CustomLoadingScreen.INSTANCE.setProgressText("Sound engine");
    }
}