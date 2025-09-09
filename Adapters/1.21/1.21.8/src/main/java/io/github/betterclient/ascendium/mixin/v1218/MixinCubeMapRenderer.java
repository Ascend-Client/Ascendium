package io.github.betterclient.ascendium.mixin.v1218;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CubeMapRenderer.class)
public class MixinCubeMapRenderer {
    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/AbstractTexture;getGlTextureView()Lcom/mojang/blaze3d/textures/GpuTextureView;"))
    public GpuTextureView onGetGlTextureView(AbstractTexture instance) {
        try {
            return instance.getGlTextureView();
        } catch (Exception e) {
            return null;
        }
    }
}
