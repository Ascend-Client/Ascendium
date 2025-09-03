package io.github.betterclient.ascendium.mixin.v1214.reload;

import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreen;
import net.minecraft.client.font.UnihexFont;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(UnihexFont.Loader.class)
public class MixinUnihexFont {
    @Inject(method = "loadHexFile", at = @At("HEAD"))
    public void onLoad(InputStream stream, CallbackInfoReturnable<UnihexFont> cir) {
        CustomLoadingScreen.INSTANCE.setProgressText("Font Renderer");
    }
}
