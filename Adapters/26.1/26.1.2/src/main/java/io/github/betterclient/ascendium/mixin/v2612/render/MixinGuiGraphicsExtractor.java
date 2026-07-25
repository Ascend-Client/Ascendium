package io.github.betterclient.ascendium.mixin.v2612.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiGraphicsExtractor.class)
public abstract class MixinGuiGraphicsExtractor {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void innerBlit(RenderPipeline pipeline, GpuTextureView textureView, GpuSampler sampler, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color);

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void innerBlit(final RenderPipeline renderPipeline, final Identifier location, final int x0, final int x1, final int y0, final int y1, final float u0, final float u1, final float v0, final float v1, final int color) {
        try {
            AbstractTexture texture = this.minecraft.getTextureManager().getTexture(location);
            this.innerBlit(renderPipeline, texture.getTextureView(), texture.getSampler(), x0, y0, x1, y1, u0, u1, v0, v1, color);
        } catch (Exception e) {
            return;
        }
    }
}
