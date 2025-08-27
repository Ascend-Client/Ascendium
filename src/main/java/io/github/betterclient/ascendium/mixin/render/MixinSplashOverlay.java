package io.github.betterclient.ascendium.mixin.render;

import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(SplashOverlay.class)
public class MixinSplashOverlay {
    @Shadow private float progress;

    @Inject(method = "render", at = @At("RETURN"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        CustomLoadingScreen.INSTANCE.render(progress);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private static void init(TextureManager textureManager, CallbackInfo ci) {
        CustomLoadingScreen.INSTANCE.init();
        CustomLoadingScreen.INSTANCE.setProgressText("Ascendium");
    }

    //hooks to disable vanilla loading screen
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    public void noRender(Screen instance, DrawContext context, int mouseX, int mouseY, float delta) { }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V"))
    public void noRender(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) { }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;renderProgressBar(Lnet/minecraft/client/gui/DrawContext;IIIIF)V"))
    public void noRender(SplashOverlay instance, DrawContext context, int minX, int minY, int maxX, int maxY, float opacity) { }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIII)V"))
    public void noRender(DrawContext instance, RenderLayer layer, int x1, int y1, int x2, int y2, int color) { }
}