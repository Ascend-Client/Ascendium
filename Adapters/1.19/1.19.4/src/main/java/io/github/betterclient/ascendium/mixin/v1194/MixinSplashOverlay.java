package io.github.betterclient.ascendium.mixin.v1194;

import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreen;
import io.github.betterclient.ascendium.ui.minecraft.CustomLoadingScreenKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class MixinSplashOverlay {
    @Shadow private float progress;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;renderProgressBar(Lnet/minecraft/client/util/math/MatrixStack;IIIIF)V"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        CustomLoadingScreen.INSTANCE.render(progress);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private static void init(MinecraftClient client, CallbackInfo ci) {
        CustomLoadingScreen.INSTANCE.init();
        CustomLoadingScreen.INSTANCE.setProgressText("Ascendium");
    }

    //hooks to disable vanilla loading screen
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    public void noRender(Screen instance, MatrixStack matrixStack, int i1, int i2, float v) {
        if (CustomLoadingScreenKt.getDidAnim()) instance.render(matrixStack, i1, i2, v);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;renderProgressBar(Lnet/minecraft/client/util/math/MatrixStack;IIIIF)V"))
    public void noRender(SplashOverlay instance, MatrixStack matrixStack, int i1, int i2, int i3, int i4, float v) { }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFIIII)V"))
    public void noRender(MatrixStack matrixStack, int i1, int i2, int i3, int i4, float v5, float v6, int i7, int i8, int i9, int i0) { }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"))
    public void noRender(MatrixStack matrixStack, int i1, int i2, int i3, int i4, int i5) { }
}