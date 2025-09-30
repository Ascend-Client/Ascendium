package io.github.betterclient.ascendium.mixin.v1219;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrawContext.class)
public abstract class MixinDrawContext {
    @Shadow protected abstract void drawSpriteTiled(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height, int u, int v, int tileWidth, int tileHeight, int textureWidth, int textureHeight, int color);
    @Shadow protected abstract void drawSpriteNineSliced(RenderPipeline pipeline, Sprite sprite, Scaling.NineSlice nineSlice, int x, int y, int width, int height, int color);

    @Shadow public abstract void drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height);

    @Shadow public abstract void drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height, int color);

    @Shadow @Final private SpriteAtlasTexture spriteAtlasTexture;

    @Shadow protected static Scaling getScaling(Sprite par1) {
        throw new AssertionError("err");
    }

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

    /**
     * @author betterclient
     * @reason i hate you
     */
    @Overwrite
    public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, int color) {
        try {
            Sprite sprite2 = this.spriteAtlasTexture.getSprite(sprite);
            Scaling scaling = getScaling(sprite2);
            if (scaling instanceof Scaling.Stretch) {
                this.drawSpriteStretched(pipeline, sprite2, x, y, width, height, color);
            } else if (scaling instanceof Scaling.Tile(int width1, int height1)) {
                this.drawSpriteTiled(pipeline, sprite2, x, y, width, height, 0, 0, width1, height1, width1, height1, color);
            } else if (scaling instanceof Scaling.NineSlice nineSlice) {
                this.drawSpriteNineSliced(pipeline, sprite2, nineSlice, x, y, width, height, color);
            }
        } catch (Exception ignored) {
            //i hate you
        }
    }
}
