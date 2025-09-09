package io.github.betterclient.ascendium.mixin.v1218;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrawContext.class)
public class MixinDrawContext {
    @Redirect(method = "drawTexturedQuad(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIIFFFFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/AbstractTexture;getGlTextureView()Lcom/mojang/blaze3d/textures/GpuTextureView;"))
    public GpuTextureView onGetTextureView(AbstractTexture instance) {
        try {
            return instance.getGlTextureView();
        } catch (Exception e) {
            if (instance instanceof ResourceTexture a) {
                try {
                    a.loadContents(MinecraftClient.getInstance().getResourceManager());
                    return instance.getGlTextureView();
                } catch (Exception ex) {
                    return null;
                }

            }
            return null;
        }
    }
}
