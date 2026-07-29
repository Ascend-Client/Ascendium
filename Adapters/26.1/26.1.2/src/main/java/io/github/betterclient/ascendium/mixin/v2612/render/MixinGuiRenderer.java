package io.github.betterclient.ascendium.mixin.v2612.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.CubeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiRenderer.class)
public class MixinGuiRenderer {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/CubeMap;render(FF)V"))
    public void onCubeMapRender(CubeMap instance, float rotXInDegrees, float rotYInDegrees) {
        try {
            instance.render(rotXInDegrees, rotYInDegrees);
        } catch (Exception e) {
            instance.registerTextures(Minecraft.getInstance().getTextureManager());
        }
    }
}
