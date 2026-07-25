package io.github.betterclient.ascendium.mixin.v2612.reload;

import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreen;
import net.minecraft.client.gui.font.providers.UnihexProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(UnihexProvider.Definition.class)
public class MixinUnihexFont {
    @Inject(method = "loadData", at = @At("HEAD"))
    public void onLoad(InputStream zipFile, CallbackInfoReturnable<UnihexProvider> cir) {
        CustomLoadingScreen.INSTANCE.setProgressText("Font Renderer");
    }
}
