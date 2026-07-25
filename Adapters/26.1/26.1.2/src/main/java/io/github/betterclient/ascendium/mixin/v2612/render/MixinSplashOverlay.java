package io.github.betterclient.ascendium.mixin.v2612.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreen;
import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreenKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class MixinSplashOverlay {
    @Shadow private float currentProgress;

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        CustomLoadingScreen.INSTANCE.render(currentProgress);
    }

    @Inject(method = "registerTextures", at = @At("HEAD"))
    private static void init(TextureManager textureManager, CallbackInfo ci) {
        CustomLoadingScreen.INSTANCE.init();
        CustomLoadingScreen.INSTANCE.setProgressText("Ascendium");
    }

    //hooks to disable vanilla loading screen
    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
    public void noRender(Screen instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (CustomLoadingScreenKt.getDidAnim()) instance.extractRenderStateWithTooltipAndSubtitles(graphics, mouseX, mouseY, a);
    }

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;extractProgressBar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIF)V"))
    public void noRender(LoadingOverlay instance, GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, float fade) { }

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    public void noRender(GuiGraphicsExtractor instance, int x0, int y0, int x1, int y1, int col) { }

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIIIII)V"))
    public void noRender(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int srcWidth, int srcHeight, int textureWidth, int textureHeight, int color) { }
}