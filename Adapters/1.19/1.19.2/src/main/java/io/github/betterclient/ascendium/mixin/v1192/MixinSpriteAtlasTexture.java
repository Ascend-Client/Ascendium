package io.github.betterclient.ascendium.mixin.v1192;

import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreen;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpriteAtlasTexture.class)
public class MixinSpriteAtlasTexture {
    @Inject(method = "upload", at = @At(value = "HEAD"))
    public void onUpload(SpriteAtlasTexture.Data data, CallbackInfo ci) {
        CustomLoadingScreen.INSTANCE.setProgressText("Texture Manager");
    }
}