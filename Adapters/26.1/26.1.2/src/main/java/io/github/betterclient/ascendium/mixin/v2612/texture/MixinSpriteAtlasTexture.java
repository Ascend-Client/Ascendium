package io.github.betterclient.ascendium.mixin.v2612.texture;

import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlas.class)
public class MixinSpriteAtlasTexture {
    @Inject(method = "upload", at = @At(value = "HEAD"))
    public void onUpload(CallbackInfo ci) {
        CustomLoadingScreen.INSTANCE.setProgressText("Texture Manager");
    }
}